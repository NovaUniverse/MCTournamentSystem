package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;

@SuppressWarnings("restriction")
public class SendPlayersHandler extends APIEndpoint {
	public SendPlayersHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("server")) {

			ServerInfo server = ProxyServer.getInstance().getServerInfo(params.get("server"));

			if (server != null) {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					player.connect(server);
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