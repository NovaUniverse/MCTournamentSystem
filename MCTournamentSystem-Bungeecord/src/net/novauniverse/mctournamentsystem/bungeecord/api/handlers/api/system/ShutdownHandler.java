package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class ShutdownHandler extends APIEndpoint {
	public ShutdownHandler() {
		super(true);
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
}