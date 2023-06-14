package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.server;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.IPVisibilitySettings;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.utils.TextUtils;

public class GetServersLogsHandler extends TournamentEndpoint {
	public GetServersLogsHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.MANAGE_SERVERS;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getQueryParameters().containsKey("server")) {
			String name = request.getQueryParameters().get("server");
			ManagedServer server = TournamentSystem.getInstance().getManagedServers().stream().filter(s -> s.getName().equals(name)).findFirst().orElse(null);

			if (server != null) {
				JSONArray logs = new JSONArray();

				boolean hideIPs = false;
				if (authentication instanceof IPVisibilitySettings) {
					hideIPs = ((IPVisibilitySettings) authentication).isHidePlayerIPs();
				}

				if (hideIPs) {
					server.getLogFileLines().forEach(line -> {
						logs.put(line.replaceAll(TournamentSystemCommons.IP_REGEX_IPv4, "[IPv4 HIDDEN]").replaceAll(TournamentSystemCommons.IP_REGEX_IPv6, "[IPv6 HIDDEN]"));
					});
				} else {
					server.getLogFileLines().forEach(line -> {
						logs.put(line);
					});
				}

				json.put("success", true);
				json.put("server_running", server.isRunning());
				json.put("session_id", server.getLastSessionId());
				json.put("log_data", logs);
				json.put("is_hiding_ip", hideIPs);

				if (TournamentSystem.getInstance().isMakeMeSufferEasteregg()) {
					JSONArray logs2ElectricBoogaloo = new JSONArray();
					for (int i = 0; i < logs.length(); i++) {
						logs2ElectricBoogaloo.put(TextUtils.englishToUWU(logs.getString(i)));
					}
					json.put("log_data", logs2ElectricBoogaloo);
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

		return new JSONResponse(json, code);
	}
}