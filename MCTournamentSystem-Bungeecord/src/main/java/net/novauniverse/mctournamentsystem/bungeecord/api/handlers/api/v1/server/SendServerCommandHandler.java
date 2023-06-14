package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.TournamentSystemAuth;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.zeeraa.novacore.commons.log.Log;

public class SendServerCommandHandler extends TournamentEndpoint {
	public SendServerCommandHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.REMOTE_EXECUTE_SERVER_COMMAND;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		String input = request.getBody();

		if (input != null) {
			if (request.getQueryParameters().containsKey("server")) {
				String name = request.getQueryParameters().get("server");
				ManagedServer server = TournamentSystem.getInstance().getManagedServers().stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);

				if (server != null) {
					Log.info("API", ((TournamentSystemAuth) authentication).getDescriptiveUserName() + " is running \"" + input + "\" on server " + server.getName());
					if (server.sendCommand(input)) {
						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "server_not_online");
						json.put("message", "The server is not online");
						json.put("http_response_code", 418);
						code = 418;
					}
				} else {
					json.put("success", false);
					json.put("error", "server_not_found");
					json.put("message", "could not find server named " + name);
					json.put("http_response_code", 404);
					code = 404;
				}
			} else {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "missing parameter: server");
				json.put("http_response_code", 400);
				code = 400;
			}
		}

		return new JSONResponse(json, code);
	}
}