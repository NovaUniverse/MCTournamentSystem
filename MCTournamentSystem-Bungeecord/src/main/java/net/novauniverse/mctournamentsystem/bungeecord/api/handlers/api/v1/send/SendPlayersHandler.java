package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send;

import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;

public class SendPlayersHandler extends TournamentEndpoint {
	public SendPlayersHandler() {
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

		if (request.getQueryParameters().containsKey("server")) {

			ServerInfo server = ProxyServer.getInstance().getServerInfo(request.getQueryParameters().get("server"));

			if (server != null) {
				boolean fast = false;

				if (request.getQueryParameters().containsKey("fast")) {
					if (request.getQueryParameters().get("fast").equalsIgnoreCase("true") || request.getQueryParameters().get("fast").equalsIgnoreCase("1")) {
						fast = true;
					}
				}

				if (fast) {
					ProxyServer.getInstance().getPlayers().forEach(p -> p.connect(server));
				} else {
					TournamentSystem.getInstance().getSlowPlayerSender().sendAll(server);
				}

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
			json.put("error", "bad_request");
			json.put("message", "missing parameter: server");
			json.put("http_response_code", 400);
			code = 400;
		}

		return new JSONResponse(json, code);
	}
}