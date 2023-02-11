package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.staff;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class GetStaffHandler extends APIEndpoint {
	public GetStaffHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
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
			return result;
		}

		result.put("success", true);
		result.put("staff", staff);
		result.put("staff_roles", staffRoles);

		return result;
	}
}