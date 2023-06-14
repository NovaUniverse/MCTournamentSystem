package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist;

import java.sql.PreparedStatement;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ClearWhitelistHandler extends TournamentEndpoint {
	public ClearWhitelistHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}
	
	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.MANAGE_WHITELIST;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		String sql = "TRUNCATE whitelist";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
		ps.executeUpdate();
		ps.close();

		json.put("success", true);

		return new JSONResponse(json);
	}
}