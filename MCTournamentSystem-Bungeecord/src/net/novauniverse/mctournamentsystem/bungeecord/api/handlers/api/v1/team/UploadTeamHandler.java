package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class UploadTeamHandler extends APIEndpoint {
	public UploadTeamHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.EDIT_TEAMS;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject result = new JSONObject();

		JSONArray teamData = null;

		boolean failed = false;

		try {
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

			teamData = new JSONArray(body);
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			result.put("http_response_code", 400);
			failed = true;
		}

		List<String> keep = new ArrayList<>();

		if (!failed) {
			try {
				for (int i = 0; i < teamData.length(); i++) {
					JSONObject player = teamData.getJSONObject(i);
					
					if(!player.has("metadata")) {
						player.put("metadata", new JSONObject());
					}

					keep.add(player.getString("uuid"));

					String sql;
					PreparedStatement ps;

					sql = "CALL `set_player_team`(?, ?, ?, ?)";

					if (player.getInt("team_number") == 0) {
						Log.error("UploadTeamHandler", "Invalid team number: 0");
						continue;
					}

					ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, player.getString("uuid"));
					ps.setString(2, player.getString("username"));
					ps.setInt(3, player.getInt("team_number"));
					ps.setString(4, player.getJSONObject("metadata").toString());

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
				break;
			}
		}

		return result;
	}
}