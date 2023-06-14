package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.staff;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class StaffHandler extends TournamentEndpoint {
	public StaffHandler() {
		super(true);

		setAllowedMethods(HTTPMethod.GET, HTTPMethod.PUT);

		setMethodBasedPermission(HTTPMethod.PUT, AuthPermission.MANAGE_STAFF);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		if (request.getMethod() == HTTPMethod.GET) {
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
					String username = rs.getString("username");
					boolean offlineMode = rs.getBoolean("offline_mode");

					JSONObject data = new JSONObject();

					data.put("uuid", uuid);
					data.put("role", role);
					data.put("username", username);
					data.put("offline_mode", offlineMode);

					staff.put(uuid, data);
				}

				rs.close();
				ps.close();
			} catch (Exception e) {
				throw e;
			}

			result.put("success", true);
			result.put("staff", staff);
			result.put("staff_roles", staffRoles);

			return new JSONResponse(result);
		} else {
			JSONObject result = new JSONObject();
			JSONObject staffData = null;

			boolean failed = false;

			try {
				staffData = new JSONObject(request.getBody());
			} catch (Exception e) {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Missing or invalid json data");
				result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				result.put("http_response_code", 400);
				return new JSONResponse(result, HTTPResponseCode.BAD_REQUEST);
			}

			if (!failed) {
				String sql;
				PreparedStatement ps;

				sql = "TRUNCATE staff";
				ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.executeUpdate();
				ps.close();

				sql = "INSERT INTO staff (uuid, role, username, offline_mode) VALUES (?, ?, ?, ?)";

				for (String key : staffData.keySet()) {
					JSONObject data = staffData.getJSONObject(key);
					ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
					ps.setString(1, data.getString("uuid"));
					ps.setString(2, data.getString("role"));
					ps.setString(3, data.optString("username", null));
					ps.setBoolean(4, data.optBoolean("offline_mode", false));
					ps.executeUpdate();
					ps.close();
				}
			}

			result.put("success", true);

			return new JSONResponse(result);
		}
	}
}