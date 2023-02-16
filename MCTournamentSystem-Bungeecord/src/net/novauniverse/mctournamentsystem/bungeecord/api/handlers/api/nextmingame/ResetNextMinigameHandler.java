package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.nextmingame;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ResetNextMinigameHandler extends APIEndpoint {
	public ResetNextMinigameHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.SET_NEXT_MINIGAME;
	}
	
	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (TournamentSystemCommons.setNextMinigame(null)) {
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "server_error");
			json.put("message", "Failed to update database");
			json.put("http_response_code", 500);
		}

		return json;
	}
}