package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

@SuppressWarnings("restriction")
public class GetCustomThemesHandler extends APIEndpoint {
	public GetCustomThemesHandler() {
		super(false);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		JSONArray themes = new JSONArray();

		TournamentSystem.getInstance().getCustomAdminUIThemes().values().forEach(data -> {
			JSONObject theme = new JSONObject();
			theme.put("name", data.getName());
			theme.put("url", data.getUrl());
			theme.put("base_theme", data.getBaseTheme());
			themes.put(theme);
		});

		json.put("themes", themes);

		return json;
	}
}