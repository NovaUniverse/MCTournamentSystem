package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class UploadTeamHandler extends TournamentEndpoint {
	public UploadTeamHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.EDIT_TEAMS;
	}

	private void createPlayerIfNotExists(UUID uuid, String username) throws SQLException {
		String sql;
		PreparedStatement ps;
		ResultSet rs;

		boolean found = false;

		sql = "SELECT id FROM players WHERE uuid = ?";
		ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
		ps.setString(1, uuid.toString());
		rs = ps.executeQuery();
		if (rs.next()) {
			found = true;
		}
		rs.close();
		ps.close();

		if (!found) {
			sql = "INSERT INTO players (uuid, username) VALUES (?, ?)";
			ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ps.setString(1, uuid.toString());
			ps.setString(2, username);
			ps.executeUpdate();
			ps.close();
		}
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray teamData = null;
		boolean failed = false;
		int code = 200;

		try {
			teamData = new JSONArray(request.getBody());
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			result.put("http_response_code", 400);
			code = 400;
			failed = true;
		}

		List<String> keep = new ArrayList<>();

		if (!failed) {
			try {
				for (int i = 0; i < teamData.length(); i++) {
					JSONObject player = teamData.getJSONObject(i);

					if (!player.has("metadata")) {
						player.put("metadata", new JSONObject());
					}

					keep.add(player.getString("uuid"));

					UUID uuid = UUID.fromString(player.getString("uuid"));
					String username = player.getString("username");

					createPlayerIfNotExists(uuid, username);

					String sql;
					PreparedStatement ps;

					sql = "UPDATE players SET username = ?, team_number = ?, metadata = ? WHERE uuid = ?";

					if (player.getInt("team_number") == 0) {
						Log.error("UploadTeamHandler", "Invalid team number: 0");
						continue;
					}

					ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, player.getString("username"));
					ps.setInt(2, player.getInt("team_number"));
					ps.setString(3, player.getJSONObject("metadata").toString());
					ps.setString(4, uuid.toString());

					// System.out.println("p1: " + player.getString("uuid") + " p2: " +
					// player.getString("username") + " p3: " + player.getInt("team_number"));

					ps.executeUpdate();
					ps.close();
				}

				result.put("success", true);
			} catch (Exception e) {
				failed = true;
				result.put("success", false);
				result.put("error", "failed");
				result.put("message", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				result.put("http_response_code", 500);
				code = 500;
			}
		}

		List<String> toRemove = new ArrayList<>();

		if (!failed) {
			try {
				String sql = "SELECT uuid FROM players";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					boolean shouldKeep = false;
					String uuidString = rs.getString("uuid");
					for (String k : keep) {
						if (k.equalsIgnoreCase(uuidString)) {
							shouldKeep = true;
							break;
						}
					}

					if (!shouldKeep) {
						toRemove.add(uuidString);
					}
				}

				rs.close();
				ps.close();
			} catch (SQLException e) {
				failed = true;
				result.put("success", false);
				result.put("error", "failed");
				result.put("message", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				result.put("http_response_code", 500);
				code = 500;
			}
		}

		for (String uuid : toRemove) {
			try {
				String sql = "DELETE FROM players WHERE uuid = ?";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, uuid);

				ps.executeUpdate();

				ps.close();
			} catch (Exception e) {
				failed = true;
				result.put("success", false);
				result.put("error", "failed");
				result.put("message", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				result.put("http_response_code", 500);
				code = 500;
				break;
			}
		}

		return new JSONResponse(result, code);
	}
}