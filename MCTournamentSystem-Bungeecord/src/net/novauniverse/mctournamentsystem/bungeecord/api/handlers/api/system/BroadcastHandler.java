package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class BroadcastHandler extends APIEndpoint {
	public BroadcastHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.BROADCAST_MESSAGE;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("message")) {
			String message = URLDecoder.decode(params.get("message"), StandardCharsets.UTF_8.name());

			ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes(TournamentSystemCommons.CHAT_COLOR_CHAR, message)));

			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: message");
		}

		return json;
	}
}