package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class SetScoreboardURLHandler extends APIEndpoint {
	public SetScoreboardURLHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_SETTINGS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("url")) {
			String url = URLDecoder.decode(params.get("url"), StandardCharsets.UTF_8.name());
			Log.info("SetScoreboardURL", "Setting scoreboard url to " + url);
			try {
				TournamentSystemCommons.setScoreboardURL(url);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
				json.put("http_response_code", 500);
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: url");
			json.put("http_response_code", 400);
		}

		return json;
	}
}