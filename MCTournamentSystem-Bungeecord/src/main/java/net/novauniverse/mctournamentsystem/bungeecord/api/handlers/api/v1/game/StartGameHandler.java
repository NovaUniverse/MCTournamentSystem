package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.TournamentRabbitMQManager;

public class StartGameHandler extends TournamentEndpoint {
	public StartGameHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.START_GAME;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

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
					code = 500;
				}
			} else {
				json.put("success", false);
				json.put("error", "message_broker_down");
				json.put("message", "The message broker is not connected");
				json.put("http_response_code", 409);
				code = 409;
			}
		} else {
			json.put("success", false);
			json.put("error", "no_message_broker");
			json.put("message", "The message broker has not been configured");
			json.put("http_response_code", 409);
			code = 409;
		}

		return new JSONResponse(json, code);
	}
}