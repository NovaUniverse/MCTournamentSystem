package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system.dynamicconfig;

import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;

@SuppressWarnings("restriction")
public class ReloadDynamicConfig extends APIEndpoint {
	public ReloadDynamicConfig() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		boolean success = true;

		if (TournamentSystem.getInstance().getDynamicConfigUrl() == null) {
			success = false;
			json.put("error", "Dynamic config in not enabled on this server");
		} else {
			try {
				TournamentSystem.getInstance().reloadDynamicConfig();
			} catch (Exception e) {
				success = false;
				json.put("error", "An error occured while reloading dynamic config. " + e.getClass().getName() + " " + e.getMessage() + ". " + ExceptionUtils.getMessage(e));
			}
		}

		json.put("success", success);

		return json;
	}
}