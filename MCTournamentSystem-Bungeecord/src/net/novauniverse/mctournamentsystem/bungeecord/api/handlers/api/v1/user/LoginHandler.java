package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APITokenStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;

public class LoginHandler extends APIEndpoint {
	public LoginHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONObject loginBody = null;
		try {
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			loginBody = new JSONObject(body);
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			result.put("http_response_code", 400);
		}

		if (loginBody != null) {
			if (loginBody.has("username")) {
				if (loginBody.has("password")) {
					String username = loginBody.getString("username");
					String password = loginBody.getString("password");

					APIUser user = TournamentSystem.getInstance().getApiUsers().stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst().orElse(null);

					if (user != null) {
						APIAccessToken token = APITokenStore.createToken(user);

						result.put("success", true);
						result.put("token", token.getUuid().toString());
					} else {
						result.put("success", false);
						result.put("error", "login_fail");
						result.put("message", "Invalid username or password");
						result.put("http_response_code", 401);
					}

				} else {
					result.put("success", false);
					result.put("error", "bad_request");
					result.put("message", "Missing parameter: password");
					result.put("http_response_code", 400);
				}
			} else {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Missing parameter: username");
				result.put("http_response_code", 400);
			}
		}
		return result;
	}
}