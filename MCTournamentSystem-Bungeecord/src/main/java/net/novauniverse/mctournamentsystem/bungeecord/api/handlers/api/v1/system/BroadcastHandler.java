package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class BroadcastHandler extends TournamentEndpoint {
	public BroadcastHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.BROADCAST_MESSAGE;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		try {
			String message = request.getBody();
			ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes(TournamentSystemCommons.CHAT_COLOR_CHAR, message)));
			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "Missing or message");
			json.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			json.put("http_response_code", 400);
			return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
		}
		return new JSONResponse(json);
	}
}