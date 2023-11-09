package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.usermanagements.user;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class GetUsersHandler extends TournamentEndpoint {
	public GetUsersHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONArray result = new JSONArray();

		TournamentSystem.getInstance().getAuthDB().getUsers().forEach(u -> {
			JSONObject userData = new JSONObject();

			userData.put("username", u.getUsername());
			userData.put("hide_ips", u.isHideIps());
			userData.put("allow_manage_users", u.isAllowManagingAccounts());
			userData.put("permissions", u.getPermissionsAsJSON());

			result.put(userData);
		});

		return new JSONResponse(result);
	}
}