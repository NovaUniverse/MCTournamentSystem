package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.web;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class PHPMyAdminUrlHandler extends APIEndpoint {
	public PHPMyAdminUrlHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		json.put("url", TournamentSystem.getInstance().getPHPMyAdminURL());

		return json;
	}
}