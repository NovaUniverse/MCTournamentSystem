package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class GetServersHandler extends APIEndpoint {
	public GetServersHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		JSONArray servers = new JSONArray();

		TournamentSystem.getInstance().getManagedServers().forEach(s -> {
			JSONObject server = new JSONObject();

			Exception exception = s.getLastException();

			server.put("name", s.getName());
			server.put("exit_code", s.getExitCode());
			server.put("java_runtime", s.getJavaExecutable());
			server.put("jvm_arguments", s.getJvmArguments());
			server.put("jar", s.getJar());
			server.put("is_running", s.isRunning());
			server.put("auto_start", s.isAutoStart());
			server.put("last_state_report", s.getLastStateReport());
			if (exception == null) {
				server.put("last_exception", "");
			} else {
				server.put("last_exception", exception.getClass().getName() + " " + exception.getMessage());
			}

			servers.put(server);
		});

		json.put("success", true);
		json.put("servers", servers);

		return json;
	}
}