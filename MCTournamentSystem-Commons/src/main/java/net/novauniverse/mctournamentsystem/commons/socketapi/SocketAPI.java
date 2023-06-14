package net.novauniverse.mctournamentsystem.commons.socketapi;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

import org.json.JSONObject;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.AMQP.BasicProperties;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.TournamentRabbitMQManager;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;

public class SocketAPI {
	public static final BasicProperties PROPERTIES = new BasicProperties.Builder().deliveryMode(2).expiration("" + TournamentRabbitMQManager.EXPIRATION).build();

	public static boolean trySendAsync(String eventName) {
		return SocketAPI.trySendAsync(eventName, new JSONObject());
	}

	public static boolean trySendAsync(String eventName, JSONObject data) {
		TournamentRabbitMQManager manager = TournamentSystemCommons.getRabbitMQManager();
		if (manager != null) {
			if (manager.isConnected()) {
				final Channel channel = manager.getWebsocketAPIChannel();
				final JSONObject json = new JSONObject();

				json.put("key", eventName);
				json.put("value", data);

				AsyncManager.runAsync(() -> {
					try {
						// Log.trace("SocketAPI", "Sending message with event name " + eventName);
						channel.basicPublish(TournamentRabbitMQManager.WEBSOCKET_EXCHANGE, "", PROPERTIES, json.toString().getBytes(StandardCharsets.UTF_8));
					} catch (IOException e) {
						Log.error("SocketAPI", "Failed to send message with key " + eventName + ". RabbitMQ might not be connected. " + e.getClass().getName() + " " + e.getMessage());
						e.printStackTrace();
					}
				});
				return true;
			}
		}
		return false;
	}

	public static boolean isAvailable() {
		TournamentRabbitMQManager manager = TournamentSystemCommons.getRabbitMQManager();
		if (manager != null) {
			return manager.isConnected();
		}
		return false;
	}
}