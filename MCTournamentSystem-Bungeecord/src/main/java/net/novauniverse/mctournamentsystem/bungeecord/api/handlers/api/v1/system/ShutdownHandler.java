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
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.TournamentSystemAuth;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.zeeraa.novacore.commons.log.Log;

public class ShutdownHandler extends TournamentEndpoint {
	public ShutdownHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.SHUTDOWN;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		Log.info("TournamentSystem", "Shutdown initialised by user " + ((TournamentSystemAuth) authentication).getDescriptiveUserName());

		TournamentSystem.getInstance().getManagedServers().stream().filter(ManagedServer::isRunning).forEach(ManagedServer::stop);
		try {
			Thread.sleep(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}

		ProxyServer.getInstance().stop("Server shutting down");

		json.put("success", true);
		return new JSONResponse(json);
	}
}