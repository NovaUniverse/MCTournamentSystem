package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.nextmingame;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class SetNextMinigameHandler extends APIEndpoint {
	public SetNextMinigameHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("name")) {
			String name = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8.name());

			if (TournamentSystemCommons.setNextMinigame(name)) {
				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_error");
				json.put("message", "Failed to update database");
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: name");
		}

		return json;
	}
}