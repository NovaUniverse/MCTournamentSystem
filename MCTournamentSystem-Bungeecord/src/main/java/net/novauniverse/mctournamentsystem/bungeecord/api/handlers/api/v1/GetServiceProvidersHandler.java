package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class GetServiceProvidersHandler extends APIEndpoint {
	public GetServiceProvidersHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}
	
	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject result = new JSONObject();
		
		result.put("mojang_api_proxy", TournamentSystem.getInstance().getMojangAPIProxyURL());
		result.put("chat_filter", TournamentSystem.getInstance().getChatFilterURL());
		result.put("skin_render_api", TournamentSystem.getInstance().getSkinRenderAPIUrl());
		
		return result;
	}
}