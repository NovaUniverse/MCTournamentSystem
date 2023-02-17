package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.zeeraa.novacore.commons.log.Log;

public class ShutdownHandler extends APIEndpoint {
	public ShutdownHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.SHUTDOWN;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		Log.info("TournamentSystem", "Shutdown initialised by user " + authentication.getUser().getUsername());

		TournamentSystem.getInstance().getManagedServers().stream().filter(ManagedServer::isRunning).forEach(ManagedServer::stop);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProxyServer.getInstance().stop("Server shutting down");
		json.put("success", true);
		return json;
	}

	@Override
	public void afterRequestProcessed(boolean didProcess, JSONObject response, HttpExchange exchange, Map<String, String> params, Authentication authentication) {
		if (didProcess) {
			TournamentSystem.getInstance().getWebServer().kill();
		}
	}
}