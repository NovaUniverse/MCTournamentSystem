package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.nextmingame;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class NextMinigameHandler extends APIEndpoint {
	public NextMinigameHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.POST, HTTPMethod.DELETE);

		setMethodBasedPermission(HTTPMethod.POST, UserPermission.SET_NEXT_MINIGAME);
		setMethodBasedPermission(HTTPMethod.DELETE, UserPermission.SET_NEXT_MINIGAME);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (method == HTTPMethod.GET) {
			json.put("next_game", TournamentSystemCommons.getNextMinigame());
		} else if (method == HTTPMethod.POST) {
			String name = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			if (TournamentSystemCommons.setNextMinigame(name.trim().length() == 0 ? null : name)) {
				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_error");
				json.put("message", "Failed to update database");
				json.put("http_response_code", 500);
			}
		} else if (method == HTTPMethod.DELETE) {
			if (TournamentSystemCommons.setNextMinigame(null)) {
				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_error");
				json.put("message", "Failed to update database");
				json.put("http_response_code", 500);
			}
		}
		return json;
	}
}