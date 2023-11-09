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

		setAllowedMethods(HTTPMethod.GET, HTTPMethod.PUT, HTTPMethod.DELETE);

		setMethodBasedPermission(HTTPMethod.PUT, AuthPermission.MANAGE_STAFF);
		setMethodBasedPermission(HTTPMethod.DELETE, AuthPermission.MANAGE_STAFF);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		if (request.getMethod() == HTTPMethod.GET) {
			JSONArray staffRoles = new JSONArray();
			JSONArray staff = new JSONArray();

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

					staff.put(data);
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
		} else if (request.getMethod() == HTTPMethod.PUT) {
			JSONObject staffData = null;

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

			if (!(staffData.has("uuid") && staffData.has("role") && staffData.has("username") && staffData.has("offline_mode"))) {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Post body is missing some parameters");
				result.put("http_response_code", 400);
				return new JSONResponse(result, HTTPResponseCode.BAD_REQUEST);
			}

			String sql = "REPLACE INTO staff (id, uuid, role, username, offline_mode) VALUES(null, ?, ?, ?, ?)";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.setString(1, staffData.getString("uuid"));
			ps.setString(2, staffData.getString("role"));
			ps.setString(3, staffData.getString("username"));
			ps.setBoolean(4, staffData.getBoolean("offline_mode"));
			ps.executeUpdate();
			ps.close();
			result.put("success", true);
		} else if (request.getMethod() == HTTPMethod.DELETE) {
			if (!request.getQueryParameters().containsKey("uuid")) {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Missing uuid");
				result.put("http_response_code", 400);
				return new JSONResponse(result, HTTPResponseCode.BAD_REQUEST);
			}

			String sql = "DELETE FROM staff WHERE uuid = ?";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.setString(1, request.getQueryParameters().get("uuid"));
			ps.executeUpdate();
			ps.close();
			result.put("success", true);
		}

		return new JSONResponse(result);
	}
}