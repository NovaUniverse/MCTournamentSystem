package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class GetServiceProvidersHandler extends TournamentEndpoint {
	public GetServiceProvidersHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();

		result.put("mojang_api_proxy", TournamentSystem.getInstance().getMojangAPIProxyURL());
		result.put("chat_filter", TournamentSystem.getInstance().getChatFilterURL());
		result.put("skin_render_api", TournamentSystem.getInstance().getSkinRenderAPIUrl());

		return new JSONResponse(result);
	}
}