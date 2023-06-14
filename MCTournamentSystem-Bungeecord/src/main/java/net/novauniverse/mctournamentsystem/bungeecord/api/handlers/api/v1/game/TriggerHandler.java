package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import java.util.UUID;

import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class TriggerHandler extends TournamentEndpoint {
	public TriggerHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.MANAGE_TRIGGERS;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getQueryParameters().containsKey("triggerId")) {
			if (request.getQueryParameters().containsKey("sessionId")) {
				if (ProxyServer.getInstance().getOnlineCount() == 0) {
					json.put("success", false);
					json.put("error", "no_players");
					json.put("message", "No players online to use for plugin message channel. Try again when there are players online");
					json.put("http_response_code", 409);
				} else {
					String name = request.getQueryParameters().get("triggerId");
					UUID sessionId = null;
					try {
						sessionId = UUID.fromString(request.getQueryParameters().get("sessionId"));
					} catch (IllegalArgumentException e) {
					}
					UUID requestUUID = UUID.randomUUID();

					if (sessionId != null) {
						final String sessionIdString = sessionId.toString();
						ProxyServer.getInstance().getPlayers().forEach(player -> {
							ByteArrayDataOutput out = ByteStreams.newDataOutput();

							out.writeUTF("trigger");
							out.writeUTF(requestUUID.toString());
							out.writeUTF(name);
							out.writeUTF(sessionIdString);

							player.getServer().getInfo().sendData(TournamentSystemCommons.DATA_CHANNEL, out.toByteArray());
						});
						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "bad request");
						json.put("message", "sessionId is not a valid uuid");
						json.put("http_response_code", 400);
						code = 400;
					}
				}
			} else {
				json.put("success", false);
				json.put("error", "bad request");
				json.put("message", "Missing or invalid parameter: sessionId");
				json.put("http_response_code", 400);
				code = 400;
			}
		} else {
			json.put("success", false);
			json.put("error", "bad request");
			json.put("message", "Missing or invalid parameter: triggerId");
			json.put("http_response_code", 400);
			code = 400;
		}
		
		return new JSONResponse(json, code);
	}
}