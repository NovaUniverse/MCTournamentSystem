package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.TournamentRabbitMQManager;

public class StartGameHandler extends APIEndpoint {
	public StartGameHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.START_GAME;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (TournamentSystemCommons.hasRabbitMQManager()) {
			TournamentRabbitMQManager manager = TournamentSystemCommons.getRabbitMQManager();
			if (manager.isConnected()) {
				if (manager.sendMessage("start_game", TournamentRabbitMQManager.empty())) {
					json.put("success", true);
				} else {
					json.put("success", false);
					json.put("error", "failed");
					json.put("message", "Failed to send message to downstream servers");
					json.put("http_response_code", 500);
				}
			} else {
				json.put("success", false);
				json.put("error", "message_broker_down");
				json.put("message", "The message broker is not connected");
				json.put("http_response_code", 409);
			}
		} else {
			json.put("success", false);
			json.put("error", "no_message_broker");
			json.put("message", "The message broker has not been configured");
			json.put("http_response_code", 409);
		}

		return json;
	}
}