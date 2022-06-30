package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.user;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APITokenStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUserStore;

@SuppressWarnings("restriction")
public class LoginHandler extends APIEndpoint {
	public LoginHandler() {
		super(false);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject result = new JSONObject();

		if (params.containsKey("username")) {
			if (params.containsKey("password")) {
				String username = params.get("username");
				String password = params.get("password");

				APIUser user = APIUserStore.getUsers().stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst().orElse(null);

				if (user != null) {
					APIAccessToken token = APITokenStore.createToken(user);

					result.put("success", true);
					result.put("token", token.getUuid().toString());
				} else {
					result.put("success", false);
					result.put("error", "login_fail");
					result.put("message", "Invalid username or password");
				}

			} else {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Missing parameter: password");
			}
		} else {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing parameter: username");
		}

		return result;
	}
}