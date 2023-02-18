package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.send;

import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

public class SendPlayerHandler extends APIEndpoint {
	public SendPlayerHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.SEND_PLAYERS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("player")) {
			if (params.containsKey("server")) {
				ProxiedPlayer player = ProxyServer.getInstance().getPlayer(UUID.fromString(params.get("player")));

				if (player != null) {
					ServerInfo server = ProxyServer.getInstance().getServerInfo(params.get("server"));

					if (server != null) {
						player.connect(server);
						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "server_not_found");
						json.put("message", "could not find server with that name");
						json.put("http_response_code", 404);
					}
				} else {
					json.put("success", false);
					json.put("error", "player_not_found");
					json.put("message", "could not find player with that uuid");
					json.put("http_response_code", 404);
				}
			} else {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "missing parameter: server");
				json.put("http_response_code", 400);
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: player");
			json.put("http_response_code", 400);
		}

		return json;
	}
}