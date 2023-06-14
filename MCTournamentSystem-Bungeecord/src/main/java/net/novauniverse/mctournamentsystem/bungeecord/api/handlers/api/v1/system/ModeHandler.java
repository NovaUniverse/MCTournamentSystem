package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class ModeHandler extends TournamentEndpoint {
	public ModeHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		json.put("offline_mode", TournamentSystem.getInstance().isOfflineMode());
		json.put("has_skin_restorer", ProxyServer.getInstance().getPluginManager().getPlugin("SkinsRestorer") != null);

		return new JSONResponse(json);
	}
}