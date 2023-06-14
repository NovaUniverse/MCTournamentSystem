package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.nextmingame;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class NextMinigameHandler extends TournamentEndpoint {
	public NextMinigameHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.POST, HTTPMethod.DELETE);

		setMethodBasedPermission(HTTPMethod.POST, AuthPermission.SET_NEXT_MINIGAME);
		setMethodBasedPermission(HTTPMethod.DELETE, AuthPermission.SET_NEXT_MINIGAME);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getMethod() == HTTPMethod.GET) {
			json.put("next_game", TournamentSystemCommons.getNextMinigame());
		} else if (request.getMethod() == HTTPMethod.POST) {
			String name = request.getBody();
			if (TournamentSystemCommons.setNextMinigame(name.trim().length() == 0 ? null : name)) {
				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_error");
				json.put("message", "Failed to update database");
				json.put("http_response_code", 500);
				code = 500;
			}
		} else if (request.getMethod() == HTTPMethod.DELETE) {
			if (TournamentSystemCommons.setNextMinigame(null)) {
				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_error");
				json.put("message", "Failed to update database");
				json.put("http_response_code", 500);
				code = 500;
			}
		}
		return new JSONResponse(json, code);
	}
}