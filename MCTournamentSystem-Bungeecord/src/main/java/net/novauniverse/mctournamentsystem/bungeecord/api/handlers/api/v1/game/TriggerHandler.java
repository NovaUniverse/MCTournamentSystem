package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class TriggerHandler extends APIEndpoint {
	public TriggerHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return false;
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_TRIGGERS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("triggerId")) {
			if (params.containsKey("sessionId")) {
				if (ProxyServer.getInstance().getOnlineCount() == 0) {
					json.put("success", false);
					json.put("error", "no_players");
					json.put("message", "No players online to use for plugin message channel. Try again when there are players online");
					json.put("http_response_code", 409);
				} else {
					String name = params.get("triggerId");
					UUID sessionId = null;
					try {
						sessionId = UUID.fromString(params.get("sessionId"));
					} catch (IllegalArgumentException e) {
					}
					UUID requestUUID = UUID.randomUUID();

					if (sessionId != null) {
						ProxyServer.getInstance().getPlayers().forEach(player -> {
							ByteArrayDataOutput out = ByteStreams.newDataOutput();

							out.writeUTF("trigger");
							out.writeUTF(requestUUID.toString());
							out.writeUTF(name);

							player.getServer().getInfo().sendData(TournamentSystemCommons.DATA_CHANNEL, out.toByteArray());
						});
						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "bad request");
						json.put("message", "sessionId is not a valid uuid");
						json.put("http_response_code", 400);
					}
				}
			} else {
				json.put("success", false);
				json.put("error", "bad request");
				json.put("message", "Missing or invalid parameter: sessionId");
				json.put("http_response_code", 400);
			}
		} else {
			json.put("success", false);
			json.put("error", "bad request");
			json.put("message", "Missing or invalid parameter: triggerId");
			json.put("http_response_code", 400);
		}

		return json;
	}
}