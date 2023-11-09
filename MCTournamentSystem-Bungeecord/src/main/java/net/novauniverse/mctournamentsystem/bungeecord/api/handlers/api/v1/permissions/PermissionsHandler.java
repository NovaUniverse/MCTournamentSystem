package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.permissions;

import org.json.JSONArray;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;

public class PermissionsHandler extends TournamentEndpoint {
	public PermissionsHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONArray result = new JSONArray();

		for (AuthPermission perm : AuthPermission.values()) {
			result.put(perm.name());
		}

		return new JSONResponse(result);
	}
}