package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.web;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class GetCSSModsHandler extends TournamentEndpoint {
	public GetCSSModsHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONArray mods = new JSONArray();

		TournamentSystem.getInstance().getCssMods().forEach(m -> {
			JSONObject mod = new JSONObject();
			mod.put("name", m.getName());
			mod.put("url", m.getCssUrl());
			mods.put(mod);
		});

		return new JSONResponse(mods);
	}
}