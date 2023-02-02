package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

@SuppressWarnings("restriction")
public class SendPlayersHandler extends APIEndpoint {
	public SendPlayersHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.SEND_PLAYERS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("server")) {

			ServerInfo server = ProxyServer.getInstance().getServerInfo(params.get("server"));

			if (server != null) {
				boolean fast = false;
				
				if(params.containsKey("fast")) {
					if(params.get("fast").equalsIgnoreCase("true") || params.get("fast").equalsIgnoreCase("1")) {
						fast = true;
					}
				}
				
				if(fast) {
					ProxyServer.getInstance().getPlayers().forEach(p -> p.connect(server));
				} else {
					TournamentSystem.getInstance().getSlowPlayerSender().sendAll(server);
				}

				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_not_found");
				json.put("message", "could not find server with that name");
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: server");
		}

		return json;
	}
}