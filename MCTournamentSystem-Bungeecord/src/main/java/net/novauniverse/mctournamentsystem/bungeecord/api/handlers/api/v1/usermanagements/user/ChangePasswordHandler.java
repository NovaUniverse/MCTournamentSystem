package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.usermanagements.user;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.User;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserAuth;
import net.novauniverse.mctournamentsystem.bungeecord.authdb.AuthDB;

public class ChangePasswordHandler extends TournamentEndpoint {
	public ChangePasswordHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();

		if (authentication instanceof UserAuth) {
			UserAuth auth = (UserAuth) authentication;
			String username = request.getQueryParameters().get("username");

			if (request.getQueryParameters().containsKey("username")) {
				if (auth.getUser().isAllowManagingAccounts() || auth.getUser().getUsername().equalsIgnoreCase(username)) {

					String password = request.getBody();
					if (password.trim().length() > 0) {
						AuthDB authDb = TournamentSystem.getInstance().getAuthDB();

						User user = authDb.getUsers().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
						if (user != null) {
							user.changePassword(password);
							authDb.save();
						} else {
							result.put("message", "User not found");
							result.put("response_code", 404);
						}
					} else {
						result.put("message", "Empty post body");
						result.put("response_code", 400);
					}
				} else {
					result.put("message", "You dont have permission to manage other users");
					result.put("response_code", 403);
				}
			} else {
				result.put("message", "Missing parameter: username");
				result.put("response_code", 400);
			}
		} else {
			result.put("message", "Auth type " + authentication.getClass().getName() + " can not manage users");
			result.put("response_code", 403);
		}
		return new JSONResponse(result, result.optInt("response_code", HTTPResponseCode.OK.getCode()));
	}
}