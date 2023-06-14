package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.util;

import java.util.UUID;

import org.json.JSONObject;

import com.google.common.base.Charsets;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class OfflineUsernameToUUIDHandler extends TournamentEndpoint {
	public OfflineUsernameToUUIDHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		if (request.getQueryParameters().containsKey("username")) {
			String name = request.getQueryParameters().get("username");
			json.put("name", name);
			json.put("uuid", UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString());
			return new JSONResponse(json);
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: username");
			json.put("http_response_code", 400);
			return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
		}
	}
}