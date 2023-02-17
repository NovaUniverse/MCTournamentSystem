package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.zeeraa.novacore.commons.log.Log;

public class SetMOTDHandler extends APIEndpoint {
	public SetMOTDHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_SETTINGS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("motd")) {
			String motd = URLDecoder.decode(params.get("motd"), StandardCharsets.UTF_8.name());
			Log.info("SetScoreboardURL", "Setting motd to " + motd);
			try {
				TournamentSystem.getInstance().setMotd(motd);
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
			json.put("message", "missing parameter: motd");
			json.put("http_response_code", 400);
		}

		return json;
	}
}