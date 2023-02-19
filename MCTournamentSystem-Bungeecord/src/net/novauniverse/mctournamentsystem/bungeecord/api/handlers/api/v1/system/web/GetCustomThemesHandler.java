package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.web;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class GetCustomThemesHandler extends APIEndpoint {
	public GetCustomThemesHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
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

		return json;
	}
}