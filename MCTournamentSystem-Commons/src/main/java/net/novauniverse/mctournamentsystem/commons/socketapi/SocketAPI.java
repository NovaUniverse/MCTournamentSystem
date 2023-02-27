package net.novauniverse.mctournamentsystem.commons.socketapi;

import java.io.IOException;
import java.net.URISyntaxException;

import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Manager;
import io.socket.client.Socket;
import io.socket.client.SocketOptionBuilder;
import io.socket.emitter.Emitter;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;

public class SocketAPI {
	private SocketAPIConfig config;
	private Socket client;

	public SocketAPI(SocketAPIConfig config) throws IOException, URISyntaxException {
		this.config = config;
		this.client = null;

		this.connect();
	}

	public void disconnect() {
		if (client != null) {
			if (client.connected()) {
				client.disconnect();
				client = null;
			}
		}
	}

	public void connect() throws IOException, URISyntaxException {
		if (client != null) {
			disconnect();
		}

		Log.info("SocketAPI", "Connecting to " + config.getUrl());

		SocketOptionBuilder builder = IO.Options.builder();

		builder.setUpgrade(true);
		builder.setPath("/socket.io/");
		builder.setReconnection(true);
		builder.setReconnectionAttempts(Integer.MAX_VALUE);

		IO.Options options = builder.build();
		client = IO.socket(config.getUrl(), options);

		client.on(Manager.EVENT_RECONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Log.info("SocketAPI", "Reconnected");
				sendAuthData();
			}
		});

		client.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Log.info("SocketAPI", "Connected");
				sendAuthData();
			}
		});

		client.on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				Log.warn("SocketAPI", "Disconnected");
			}
		});

		client.on("error", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = new JSONObject(args[0] + "");
				Log.warn("SocketAPI", "Received error " + data.optString("message"));
			}
		});

		client.on("auth_response", new Emitter.Listener() {
			@Override
			public void call(Object... args) {
				JSONObject data = new JSONObject(args[0] + "");
				Log.warn("SocketAPI", "Received error " + data.optString("message"));
			}
		});

		client.connect();
	}

	public SocketAPIConfig getConfig() {
		return config;
	}

	public void sendEventAsync(String name, JSONObject data) {
		AsyncManager.runAsync(() -> {
			if (client != null) {
				if (client.connected()) {
					client.emit("message", name, data);
				}
			}
		});
	}

	private void sendAuthData() {
		JSONObject json = new JSONObject();
		json.put("key", config.getKey());
		sendEventAsync("auth", json);
	}

	public static final JSONObject emptyResponse() {
		return new JSONObject();
	}
}