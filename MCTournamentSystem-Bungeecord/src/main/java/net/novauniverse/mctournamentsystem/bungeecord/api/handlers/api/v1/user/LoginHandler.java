package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKeyAuth;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APITokenStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;

public class LoginHandler extends TournamentEndpoint {
	public LoginHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONObject loginBody = null;
		int code = 200;

		try {
			loginBody = new JSONObject(request.getBody());
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			result.put("http_response_code", 400);
			code = 400;
		}

		if (loginBody != null) {
			if (loginBody.has("username")) {
				if (loginBody.has("password")) {
					String username = loginBody.getString("username");
					String password = loginBody.getString("password");

					APIUser user = TournamentSystem.getInstance().getApiUsers().stream().filter(u -> u.getUsername().equals(username) && u.getPassword().equals(password)).findFirst().orElse(null);

					if (user != null) {
						APIKeyAuth token = APITokenStore.createToken(user);

						result.put("success", true);
						result.put("token", token.getKey());
					} else {
						result.put("success", false);
						result.put("error", "login_fail");
						result.put("message", "Invalid username or password");
						result.put("http_response_code", 401);
						code = 401;
					}

				} else {
					result.put("success", false);
					result.put("error", "bad_request");
					result.put("message", "Missing parameter: password");
					result.put("http_response_code", 400);
					code = 400;
				}
			} else {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Missing parameter: username");
				result.put("http_response_code", 400);
				code = 400;
			}
		}

		return new JSONResponse(result, code);
	}
}