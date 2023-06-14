package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class SecurityCheckHandler extends TournamentEndpoint {
	public SecurityCheckHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return false;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		json.put("default_login_warning", TournamentSystem.getInstance().getApiUsers().stream().filter(u -> u.getUsername().equalsIgnoreCase("admin") && u.getPassword().equalsIgnoreCase("admin")).findFirst().isPresent());
		json.put("dev_mode", TournamentSystem.getInstance().isWebserverDevelopmentMode());

		return new JSONResponse(json);
	}
}