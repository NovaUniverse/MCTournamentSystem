package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class GetServersHandler extends TournamentEndpoint {
	public GetServersHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
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
		
		return new JSONResponse(json);
	}
}