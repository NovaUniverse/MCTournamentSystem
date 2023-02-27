package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class WhoAmIHandler extends APIEndpoint {
	public WhoAmIHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject result = new JSONObject();

		result.put("success", true);

		if (authentication == null) {
			result.put("logged_in", false);
		} else {
			result.put("logged_in", true);
			result.put("username", authentication.getUser().getUsername());
			result.put("permissions", authentication.getUser().getPermissionsAsJSON());
		}

		return result;
	}
}