package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.staff;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class SetStaffHandler extends APIEndpoint {
	public SetStaffHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_STAFF;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject result = new JSONObject();

		JSONObject staffData = null;

		boolean failed = false;

		try {
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

			staffData = new JSONObject(body);
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			failed = true;
		}

		String sql;
		PreparedStatement ps;

		if (!failed) {
			sql = "TRUNCATE staff";
			ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.executeUpdate();
			ps.close();

			sql = "INSERT INTO staff (uuid, role) VALUES (?, ?)";

			for (String key : staffData.keySet()) {
				UUID uuid = UUID.fromString(key);
				String role = staffData.getString(key);

				ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.setString(1, uuid.toString());
				ps.setString(2, role);
				ps.executeUpdate();
				ps.close();
			}
		}

		result.put("success", true);

		return result;
	}
}