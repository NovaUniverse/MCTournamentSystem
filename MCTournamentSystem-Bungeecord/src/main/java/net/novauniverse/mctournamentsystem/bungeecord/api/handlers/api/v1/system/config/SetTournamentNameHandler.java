package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config;

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
import net.zeeraa.novacore.commons.log.Log;

public class SetTournamentNameHandler extends APIEndpoint {
	public SetTournamentNameHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.POST);
		setMethodBasedPermission(HTTPMethod.POST, UserPermission.MANAGE_SETTINGS);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_SETTINGS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (method == HTTPMethod.GET) {
			json.put("success", true);
			json.put("tournament_name", TournamentSystemCommons.getTournamentName());
		} else {
			String name = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			Log.info("TournamentSystemAPI", "Reanaming tournament to " + name);
			try {
				TournamentSystemCommons.setTournamentName(name);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
				json.put("http_response_code", 500);
			}
		}

		return json;
	}
}