package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class UpploadTeamHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
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
			failed = true;
		}

		List<String> keep = new ArrayList<>();

		if (!failed) {
			try {
				for (int i = 0; i < teamData.length(); i++) {
					JSONObject player = teamData.getJSONObject(i);

					keep.add(player.getString("uuid"));

					String sql;
					PreparedStatement ps;

					sql = "CALL `set_player_team`(?, ?, ?)";

					if (player.getInt("team_number") == 0) {
						Log.error("UpploadTeamHandler", "Invalid team number: 0");
						continue;
					}

					ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, player.getString("uuid"));
					ps.setString(2, player.getString("username"));
					ps.setInt(3, player.getInt("team_number"));

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
				break;
			}
		}

		String response = result.toString(4);
		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}