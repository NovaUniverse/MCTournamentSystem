package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class SecurityCheckHandler extends APIEndpoint {
	public SecurityCheckHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return false;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		json.put("default_login_warning", TournamentSystem.getInstance().getApiUsers().stream().filter(u -> u.getUsername().equalsIgnoreCase("admin") && u.getPassword().equalsIgnoreCase("admin")).findFirst().isPresent());
		json.put("dev_mode", TournamentSystem.getInstance().isWebserverDevelopmentMode());

		return json;
	}
}