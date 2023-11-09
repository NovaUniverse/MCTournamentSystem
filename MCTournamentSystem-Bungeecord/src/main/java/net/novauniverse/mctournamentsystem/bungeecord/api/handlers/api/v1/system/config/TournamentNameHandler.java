package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class TournamentNameHandler extends TournamentEndpoint {
	public TournamentNameHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.POST);
		setMethodBasedPermission(HTTPMethod.POST, AuthPermission.MANAGE_SETTINGS);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getMethod() == HTTPMethod.GET) {
			json.put("success", true);
			json.put("tournament_name", TournamentSystemCommons.getTournamentName());
		} else {
			String name = request.getBody();
			Log.info("TournamentSystemAPI", "Reanaming tournament to " + name);
			try {
				TournamentSystemCommons.setTournamentName(name);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
				json.put("http_response_code", 500);
			}
		}

		return new JSONResponse(json, code);
	}
}