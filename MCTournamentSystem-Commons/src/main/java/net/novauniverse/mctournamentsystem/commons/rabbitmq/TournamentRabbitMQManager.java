package net.novauniverse.mctournamentsystem.commons.rabbitmq;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeoutException;
import java.util.function.Consumer;

import org.json.JSONException;
import org.json.JSONObject;

import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.AMQP.BasicProperties;
import com.rabbitmq.client.BuiltinExchangeType;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;

import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;

public class TournamentRabbitMQManager {
	public static final String INTERNAL_EXCHANGE = "ts_internal_exchange";
	public static final String INTERNAL_QUEUE = "ts_internal_queue";
	public static final String WEBSOCKET_EXCHANGE = "ts_ws_data";

	public static final int EXPIRATION = 1000 * 2; // 2 seconds

	private final String connectionString;

	private final String host;
	private final int port;

	private Connection connection;
	private Channel internalChannel;
	private Channel websocketAPIChannel;

	private BasicProperties properties;

	private List<MassageListener> listeners;

	public TournamentRabbitMQManager(String connectionString) {
		this(connectionString, null, 0);
	}

	public TournamentRabbitMQManager(String host, int port) {
		this(null, host, port);
	}

	private TournamentRabbitMQManager(String connectionString, String host, int port) {
		this.connectionString = connectionString;

		this.host = host;
		this.port = port;

		this.connection = null;
		this.internalChannel = null;
		this.websocketAPIChannel = null;

		this.properties = new BasicProperties.Builder().deliveryMode(2).expiration("" + TournamentRabbitMQManager.EXPIRATION).build();

		this.listeners = new ArrayList<>();

		try {
			this.connect();
		} catch (Exception e) {
			Log.error("RabbitMQ", "Failed to connect to RabbitMQ server. " + e.getClass().getName() + " " + e.getMessage());
		}
	}

	public Channel getInternalChannel() {
		return internalChannel;
	}

	public Channel getWebsocketAPIChannel() {
		return websocketAPIChannel;
	}

	public boolean isConnected() {
		if (connection != null) {
			return connection.isOpen();
		}
		return false;
	}

	public void close() {
		if (internalChannel != null) {
			if (internalChannel.isOpen()) {
				try {
					internalChannel.close();
					internalChannel = null;
				} catch (Exception e) {
					Log.error("RabbitMQ", "Failed to close internal channel");
				}
			}
		}

		if (websocketAPIChannel != null) {
			if (websocketAPIChannel.isOpen()) {
				try {
					websocketAPIChannel.close();
					websocketAPIChannel = null;
				} catch (Exception e) {
					Log.error("RabbitMQ", "Failed to close websocket api channel");
				}
			}
		}

		if (connection != null) {
			if (connection.isOpen()) {
				try {
					connection.close();
					connection = null;
				} catch (Exception e) {
					Log.error("RabbitMQ", "Failed to close connection");
				}
			}
		}
	}

	public void connect() throws KeyManagementException, NoSuchAlgorithmException, URISyntaxException, IOException, TimeoutException {
		this.close();

		ConnectionFactory factory = new ConnectionFactory();

		if (connectionString == null) {
			factory.setHost(host);
			factory.setPort(port);
		} else {
			factory.setUri(connectionString);
		}
		connection = factory.newConnection();

		internalChannel = connection.createChannel();
		websocketAPIChannel = connection.createChannel();

		internalChannel.exchangeDeclare(INTERNAL_EXCHANGE, BuiltinExchangeType.FANOUT);
		websocketAPIChannel.exchangeDeclare(WEBSOCKET_EXCHANGE, BuiltinExchangeType.FANOUT);

		internalChannel.queueDeclare(INTERNAL_QUEUE, false, false, false, null);
		internalChannel.queueBind(INTERNAL_QUEUE, INTERNAL_EXCHANGE, "#");

		internalChannel.basicConsume(INTERNAL_QUEUE, true, new DefaultConsumer(internalChannel) {
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) {
				String route = envelope.getRoutingKey();
				String content = new String(body, StandardCharsets.UTF_8);
				JSONObject json;
				try {
					json = new JSONObject(content);
				} catch (JSONException e) {
					e.printStackTrace();
					Log.error("RabbitMQManager", "Failed to parse message with route key " + route + ". " + e.getClass().getName() + " " + e.getMessage());
					Log.debug("RabbitMQManager", "The previous message that failed to parse had the content: " + content);
					return;
				}

				listeners.stream().filter(l -> l.getRoute().equalsIgnoreCase(route)).forEach(listener -> {
					try {
						listener.getConsumer().accept(json);
					} catch (Exception e) {
						Log.error("RabbitMQManager", "Error occured in listener while processing message with route key " + route + ". " + e.getClass().getName() + " " + e.getMessage());
						e.printStackTrace();
					}
				});
			}
		});
	}
	
	public void addMessageReceiver(String route, Consumer<JSONObject> consumer) {
		listeners.add(new MassageListener(route, consumer));
	}

	public boolean sendMessage(String route, JSONObject json) {
		try {
			internalChannel.basicPublish(TournamentRabbitMQManager.INTERNAL_EXCHANGE, route, properties, json.toString().getBytes(StandardCharsets.UTF_8));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void sendMessageAsync(String route, JSONObject json) {
		this.sendMessageAsync(route, json, null);
	}

	public void sendMessageAsync(String route, JSONObject json, Consumer<Boolean> resultCallback) {
		AsyncManager.runAsync(() -> {
			boolean success = sendMessage(route, json);
			if (resultCallback != null) {
				resultCallback.accept(success);
			}
		});
	}
	
	public static JSONObject empty() {
		return new JSONObject();
	}
}

class MassageListener {
	private final String route;
	private final Consumer<JSONObject> consumer;

	public MassageListener(String route, Consumer<JSONObject> consumer) {
		this.route = route;
		this.consumer = consumer;
	}

	public String getRoute() {
		return route;
	}

	public Consumer<JSONObject> getConsumer() {
		return consumer;
	}
}