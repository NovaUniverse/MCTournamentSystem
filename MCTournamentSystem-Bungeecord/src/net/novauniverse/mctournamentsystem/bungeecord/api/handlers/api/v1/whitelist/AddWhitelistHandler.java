package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class AddWhitelistHandler extends APIEndpoint {
	public AddWhitelistHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_WHITELIST;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("uuid")) {
			UUID uuid = UUID.fromString(params.get("uuid"));

			String sql = "REPLACE INTO whitelist (id, uuid) VALUES(null, ?)";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, uuid.toString());

			ps.executeUpdate();
			ps.close();

			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: uuid");
			json.put("http_response_code", 400);
		}

		return json;
	}
}