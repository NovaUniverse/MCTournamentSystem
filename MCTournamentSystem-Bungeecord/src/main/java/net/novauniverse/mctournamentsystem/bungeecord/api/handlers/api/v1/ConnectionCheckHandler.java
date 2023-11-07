package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class ConnectionCheckHandler extends TournamentEndpoint {
	public ConnectionCheckHandler() {
		super(false);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject response = new JSONObject();
		response.put("success", true);
		return new JSONResponse(response);
	}
}