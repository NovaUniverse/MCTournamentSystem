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

public class EditUserHandler  extends TournamentEndpoint {
	public EditUserHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();

		if (authentication instanceof UserAuth) {
			UserAuth auth = (UserAuth) authentication;
			if (auth.getUser().isAllowManagingAccounts()) {
				if (request.getQueryParameters().containsKey("username")) {
					try {
						JSONObject json = new JSONObject(request.getBody());
						boolean paramsOk = true;

						if (!json.has("hide_ips")) {
							result.put("message", "Missing hide_ips");
							result.put("response_code", 400);
							paramsOk = false;
						}

						if (!json.has("permissions")) {
							result.put("message", "Missing permissions");
							result.put("response_code", 400);
							paramsOk = false;
						}

						if (paramsOk) {
							boolean hideIps = json.getBoolean("hide_ips");
							JSONArray permissions = json.getJSONArray("permissions");

							String username = request.getQueryParameters().get("username");
							AuthDB authDb = TournamentSystem.getInstance().getAuthDB();

							User user = authDb.getUsers().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
							if (user != null) {
								user.getPermissions().clear();
								for (int i = 0; i < permissions.length(); i++) {
									try {
										user.getPermissions().add(AuthPermission.valueOf(permissions.getString(i)));
									} catch (Exception e) {
									}
								}
								user.setHideIps(hideIps);
								authDb.save();
							} else {
								result.put("message", "User not found");
								result.put("response_code", 404);
							}
						}
					} catch (JSONException e) {
						result.put("message", "Failed to parse body");
						result.put("response_code", 400);
					}
				} else {
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