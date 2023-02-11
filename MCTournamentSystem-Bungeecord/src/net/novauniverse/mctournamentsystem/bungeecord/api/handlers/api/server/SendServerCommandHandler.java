package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.server;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.zeeraa.novacore.commons.log.Log;

public class SendServerCommandHandler extends APIEndpoint {
	public SendServerCommandHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.REMOTE_EXECUTE_SERVER_COMMAND;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		String input = null;
		try {
			InputStreamReader isr = new InputStreamReader(exchange.getRequestBody(), "utf-8");
			BufferedReader br = new BufferedReader(isr);

			int b;
			StringBuilder buf = new StringBuilder();
			while ((b = br.read()) != -1) {
				buf.append((char) b);
			}

			br.close();
			isr.close();
			input = buf.toString();
		} catch (Exception e) {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "failed to parse message body. " + e.getClass().getName() + " " + e.getMessage());
		}

		if (input != null) {
			if (params.containsKey("server")) {
				String name = params.get("server");
				ManagedServer server = TournamentSystem.getInstance().getManagedServers().stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);

				if (server != null) {
					Log.info("API", authentication.getUser().getUsername() + " is running \"" + input + "\" on server " + server.getName());
					if (server.sendCommand(input)) {
						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "server_not_online");
						json.put("message", "The server is not online");
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
		}

		return json;
	}
}