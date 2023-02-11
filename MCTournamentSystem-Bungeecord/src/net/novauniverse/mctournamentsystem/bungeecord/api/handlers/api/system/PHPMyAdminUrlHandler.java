package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class PHPMyAdminUrlHandler extends APIEndpoint {
	public PHPMyAdminUrlHandler() {
		super(false);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		json.put("url", TournamentSystem.getInstance().getPHPMyAdminURL());

		return json;
	}
}