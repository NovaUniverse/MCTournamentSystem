package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.staff;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class StaffHandler extends APIEndpoint {
	public StaffHandler() {
		super(true);

		setAllowedMethods(HTTPMethod.GET, HTTPMethod.PUT);

		setMethodBasedPermission(HTTPMethod.PUT, UserPermission.MANAGE_STAFF);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		if (method == HTTPMethod.GET) {
			JSONObject result = new JSONObject();
			JSONArray staffRoles = new JSONArray();
			JSONObject staff = new JSONObject();

			for (String name : TournamentSystem.getInstance().getStaffRoles()) {
				staffRoles.put(name);
			}

			try {
				String sql = "SELECT * FROM staff";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					String uuid = rs.getString("uuid");
					String role = rs.getString("role");

					staff.put(uuid, role);
				}

				rs.close();
				ps.close();
			} catch (Exception e) {
				result.put("success", false);
				result.put("message", e.getClass().getName() + " " + e.getMessage());
				result.put("http_response_code", 500);
				return result;
			}

			result.put("success", true);
			result.put("staff", staff);
			result.put("staff_roles", staffRoles);

			return result;
		} else {
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
				result.put("http_response_code", 400);
				failed = true;
			}

			if (!failed) {
				String sql;
				PreparedStatement ps;

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
}