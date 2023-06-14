package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;

public class QuickMessageHandler extends TournamentEndpoint {
	public QuickMessageHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.BROADCAST_MESSAGE;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, net.novauniverse.apilib.http.auth.Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getQueryParameters().containsKey("message_id")) {
			try {
				int messageId = Integer.parseInt(request.getQueryParameters().get("message_id"));

				if (messageId >= TournamentSystem.getInstance().getQuickMessages().size() || messageId < 0) {
					json.put("success", false);
					json.put("error", "not_found");
					json.put("message", "message with id " + messageId + " was not found");
					json.put("http_response_code", 404);
					code = 404;
				} else {
					String message = TournamentSystem.getInstance().getQuickMessages().get(messageId);
					ProxyServer.getInstance().broadcast(new TextComponent(message));
					json.put("success", true);
				}
			} catch (NumberFormatException nfe) {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "invalid message id");
				json.put("http_response_code", 400);
				code = 400;
			}
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: message_id");
			json.put("http_response_code", 400);
			code = 400;
		}

		return new JSONResponse(json, code);
	}
}