package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class BroadcastHandler extends APIEndpoint {
	public BroadcastHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.BROADCAST_MESSAGE;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		try {
			String message = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
			ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes(TournamentSystemCommons.CHAT_COLOR_CHAR, message)));
			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "Missing or message");
			json.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			json.put("http_response_code", 400);
		}

		return json;
	}
}