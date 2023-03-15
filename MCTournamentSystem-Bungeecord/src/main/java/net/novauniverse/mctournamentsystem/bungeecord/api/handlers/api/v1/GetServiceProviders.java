package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class GetServiceProviders extends APIEndpoint {
	public GetServiceProviders() {
		super(false);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject data = new JSONObject();

		data.put("mojang_api_proxy", TournamentSystem.getInstance().getMojangAPIProxyURL());

		return data;
	}
}