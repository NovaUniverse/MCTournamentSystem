package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.server;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;

@SuppressWarnings("restriction")
public class StopServersHandler extends APIEndpoint {
	public StopServersHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_SERVERS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("server")) {
			String name = params.get("server");
			ManagedServer server = TournamentSystem.getInstance().getManagedServers().stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);

			if (server != null) {
				if (server.stop()) {
					json.put("success", true);
				} else {
					json.put("success", false);
					if (!server.isRunning()) {
						json.put("error", "server_not_running");
						json.put("message", "Could not stop server " + server.getName() + " since its not running");
					} else {
						json.put("error", "failed");
						json.put("message", "Failed to stop server " + server.getName());
					}
				}
			} else {
				json.put("success", false);
				json.put("error", "server_not_found");
				json.put("message", "could not find server named " + name);
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: server");
		}

		return json;
	}
}