package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.staff;

import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;

@SuppressWarnings("restriction")
public class GetStaffRolesHandler extends APIEndpoint {
	public GetStaffRolesHandler() {
		super(false);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray staffRoles = new JSONArray();

		for (String name : TournamentSystem.getInstance().getStaffRoles()) {
			staffRoles.put(name);
		}

		result.put("success", true);
		result.put("staff_roles", staffRoles);

		return result;
	}
}