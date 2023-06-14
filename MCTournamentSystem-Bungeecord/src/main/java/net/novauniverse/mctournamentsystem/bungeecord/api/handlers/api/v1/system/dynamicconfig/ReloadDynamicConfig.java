package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.dynamicconfig;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class ReloadDynamicConfig extends TournamentEndpoint {
	public ReloadDynamicConfig() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		boolean success = true;
		int code = 200;

		if (TournamentSystem.getInstance().getDynamicConfigUrl() == null) {
			success = false;
			json.put("message", "Dynamic config in not enabled on this server");
		} else {
			try {
				TournamentSystem.getInstance().reloadDynamicConfig();
			} catch (Exception e) {
				success = false;
				json.put("message", "An error occured while reloading dynamic config. " + e.getClass().getName() + " " + e.getMessage() + ". " + ExceptionUtils.getMessage(e));
				json.put("http_response_code", 500);
				code = 500;
			}
		}

		json.put("success", success);

		return new JSONResponse(json, code);
	}
}