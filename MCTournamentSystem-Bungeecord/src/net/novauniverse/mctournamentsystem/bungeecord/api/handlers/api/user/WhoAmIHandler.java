package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.user;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;

@SuppressWarnings("restriction")
public class WhoAmIHandler extends APIEndpoint {
	public WhoAmIHandler() {
		super(false);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject result = new JSONObject();

		result.put("success", true);

		if (accessToken == null) {
			result.put("logged_in", false);
		} else {
			result.put("logged_in", true);
			result.put("username", accessToken.getUser().getUsername());
			result.put("permissions", accessToken.getUser().getPermissionsAsJSON());
		}

		return result;
	}
}