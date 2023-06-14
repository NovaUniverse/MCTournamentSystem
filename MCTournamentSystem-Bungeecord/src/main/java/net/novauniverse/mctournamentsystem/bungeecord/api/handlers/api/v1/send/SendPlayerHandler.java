package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send;

import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;

public class SendPlayerHandler extends TournamentEndpoint {
	public SendPlayerHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.SEND_PLAYERS;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getQueryParameters().containsKey("player")) {
			if (request.getQueryParameters().containsKey("server")) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(request.getQueryParameters().get("player")));

				if (player != null) {
					ServerInfo server = ProxyServer.getInstance().getServerInfo(request.getQueryParameters().get("server"));

					if (server != null) {
						player.connect(server);
						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "server_not_found");
						json.put("message", "could not find server with that name");
						json.put("http_response_code", 404);
						code = 404;
					}
				} else {
					json.put("success", false);
					json.put("error", "player_not_found");
					json.put("message", "could not find player with that uuid");
					json.put("http_response_code", 404);
					code = 404;
				}
			} else {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "missing parameter: server");
				json.put("http_response_code", 400);
				code = 400;
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: player");
			json.put("http_response_code", 400);
			code = 400;
		}

		return new JSONResponse(json, code);
	}
}