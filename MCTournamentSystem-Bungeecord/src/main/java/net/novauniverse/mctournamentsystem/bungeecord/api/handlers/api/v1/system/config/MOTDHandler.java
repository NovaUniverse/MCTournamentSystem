package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.config;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.zeeraa.novacore.commons.log.Log;

public class MOTDHandler extends TournamentEndpoint {
	public MOTDHandler() {
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
			json.put("motd", TournamentSystem.getInstance().getMotd());
		} else {
			String motd = request.getBody();
			Log.info("TournamentSystemAPI", "Setting motd to " + motd);
			try {
				TournamentSystem.getInstance().setMotd(motd);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
				json.put("http_response_code", 500);
				code = 500;
			}
		}

		return new JSONResponse(json, code);
	}
}