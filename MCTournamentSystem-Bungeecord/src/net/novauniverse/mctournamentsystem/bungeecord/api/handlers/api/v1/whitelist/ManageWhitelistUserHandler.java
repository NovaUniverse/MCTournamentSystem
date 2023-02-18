package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ManageWhitelistUserHandler extends APIEndpoint {
	public ManageWhitelistUserHandler() {
		super(false);
		
		setAllowedMethods(HTTPMethod.PUT, HTTPMethod.DELETE, HTTPMethod.GET);
		
		setMethodBasedPermission(HTTPMethod.PUT, UserPermission.MANAGE_WHITELIST);
		setMethodBasedPermission(HTTPMethod.DELETE, UserPermission.MANAGE_WHITELIST);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		if (method == HTTPMethod.GET) {
			JSONArray data = new JSONArray();

			String sql = "SELECT uuid FROM whitelist";

			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				data.put(rs.getString("uuid"));
			}

			rs.close();
			ps.close();

			json.put("success", true);
			json.put("users", data);
		} else {
			if (params.containsKey("uuid")) {
				UUID uuid = UUID.fromString(params.get("uuid"));

				String sql;

				if (method == HTTPMethod.PUT) {
					sql = "REPLACE INTO whitelist (id, uuid) VALUES(null, ?)";
				} else {
					sql = "DELETE FROM whitelist WHERE uuid = ?";
				}

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
		}

		return json;
	}
}