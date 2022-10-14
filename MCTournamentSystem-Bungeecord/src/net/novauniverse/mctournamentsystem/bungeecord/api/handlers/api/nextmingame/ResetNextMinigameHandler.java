package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.nextmingame;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class ResetNextMinigameHandler extends APIEndpoint {
	public ResetNextMinigameHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (TournamentSystemCommons.setNextMinigame(null)) {
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "server_error");
			json.put("message", "Failed to update database");
		}

		return json;
	}
}