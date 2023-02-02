package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

@SuppressWarnings("restriction")
public class QuickMessageHandler extends APIEndpoint {
	public QuickMessageHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.BROADCAST_MESSAGE;
	}
	
	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("message_id")) {
			try {
				int messageId = Integer.parseInt(params.get("message_id"));

				if (messageId >= TournamentSystem.getInstance().getQuickMessages().size() || messageId < 0) {
					json.put("success", false);
					json.put("error", "bad_request");
					json.put("message", "invalid message id");
				} else {
					String message = TournamentSystem.getInstance().getQuickMessages().get(messageId);
					ProxyServer.getInstance().broadcast(new TextComponent(message));
					json.put("success", true);
				}
			} catch (NumberFormatException nfe) {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "invalid message id");
			}
			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: message_id");
		}

		return json;
	}
}