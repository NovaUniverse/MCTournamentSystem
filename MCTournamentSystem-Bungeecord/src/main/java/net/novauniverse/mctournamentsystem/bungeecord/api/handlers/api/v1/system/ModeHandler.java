package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class ModeHandler extends APIEndpoint {
	public ModeHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		json.put("offline_mode", TournamentSystem.getInstance().isOfflineMode());
		json.put("has_skin_restorer", ProxyServer.getInstance().getPluginManager().getPlugin("SkinsRestorer") != null);
		
		return json;
	}
}