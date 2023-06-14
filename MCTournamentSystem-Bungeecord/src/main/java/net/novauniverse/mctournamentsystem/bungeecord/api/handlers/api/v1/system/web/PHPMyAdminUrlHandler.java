package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.web;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class PHPMyAdminUrlHandler extends TournamentEndpoint {
	public PHPMyAdminUrlHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}
	
	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		json.put("url", TournamentSystem.getInstance().getPHPMyAdminURL());
		
		return new JSONResponse(json);
	}
}