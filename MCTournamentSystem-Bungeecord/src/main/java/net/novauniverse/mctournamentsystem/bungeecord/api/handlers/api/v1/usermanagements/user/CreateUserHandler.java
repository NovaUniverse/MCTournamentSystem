package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.usermanagements.user;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.User;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserAuth;
import net.novauniverse.mctournamentsystem.bungeecord.authdb.AuthDB;

public class CreateUserHandler extends TournamentEndpoint {
	public CreateUserHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.PUT);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();

		if (authentication instanceof UserAuth) {
			UserAuth auth = (UserAuth) authentication;
			if (auth.getUser().isAllowManagingAccounts()) {
				try {
					JSONObject json = new JSONObject(request.getBody());
					boolean paramsOk = true;

					if (!json.has("username")) {
						result.put("message", "Missing username");
						result.put("response_code", 400);
						paramsOk = false;
					}

					if (!json.has("password")) {
						result.put("message", "Missing password");
						result.put("response_code", 400);
						paramsOk = false;
					}

					if (!json.has("hide_ips")) {
						result.put("message", "Missing hide_ips");
						result.put("response_code", 400);
						paramsOk = false;
					}

					if (!json.has("allow_manage_users")) {
						result.put("message", "Missing allow_manage_users");
						result.put("response_code", 400);
						paramsOk = false;
					}

					if (!json.has("permissions")) {
						result.put("message", "Missing permissions");
						result.put("response_code", 400);
						paramsOk = false;
					}

					if (paramsOk) {
						String username = json.getString("username");
						String password = json.getString("password");
						boolean hideIps = json.getBoolean("hide_ips");
						boolean allowManageUsers = json.getBoolean("allow_manage_users");
						JSONArray permissions = json.getJSONArray("permissions");

						AuthDB authDb = TournamentSystem.getInstance().getAuthDB();

						if (!authDb.getUsers().stream().anyMatch(u -> u.getUsername().equals(username))) {
							User user = User.createNew(username, password, allowManageUsers);
							user.setHideIps(hideIps);

							for (int i = 0; i < permissions.length(); i++) {
								try {
									user.getPermissions().add(AuthPermission.valueOf(permissions.getString(i)));
								} catch (Exception e) {
								}
							}

							authDb.getUsers().add(user);
							authDb.save();
						} else {
							result.put("message", "User already exists");
							result.put("response_code", 409);
						}
					}
				} catch (JSONException e) {
					result.put("message", "Failed to parse body");
					result.put("response_code", 400);
				}
			} else {
				result.put("message", "You dont have permission to manage users");
				result.put("response_code", 403);
			}
		} else {
			result.put("message", "Auth type " + authentication.getClass().getName() + " can not manage users");
			result.put("response_code", 403);
		}
		return new JSONResponse(result, result.optInt("response_code", HTTPResponseCode.OK.getCode()));
	}
}
