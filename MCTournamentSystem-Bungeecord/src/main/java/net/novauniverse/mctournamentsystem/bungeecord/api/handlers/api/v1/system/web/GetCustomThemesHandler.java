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

public class GetCustomThemesHandler extends TournamentEndpoint {
	public GetCustomThemesHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		JSONArray themes = new JSONArray();

		TournamentSystem.getInstance().getCustomAdminUIThemes().values().forEach(data -> {
			JSONObject theme = new JSONObject();
			theme.put("name", data.getName());
			theme.put("url", data.getUrl());
			theme.put("base_theme", data.getBaseTheme());
			theme.put("server_console_theme", data.getServerConsoleTheme());
			themes.put(theme);
		});

		json.put("themes", themes);

		return new JSONResponse(json);
	}
}