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

public class SetTournamentNameHandler extends APIEndpoint {
	public SetTournamentNameHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_SETTINGS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("name")) {
			String name = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8.name());
			Log.info("SetTournamentNameHandler", "Reanaming tournament to " + name);
			try {
				TournamentSystemCommons.setTournamentName(name);
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
			json.put("message", "missing parameter: name");
			json.put("http_response_code", 400);
		}

		return json;
	}
}