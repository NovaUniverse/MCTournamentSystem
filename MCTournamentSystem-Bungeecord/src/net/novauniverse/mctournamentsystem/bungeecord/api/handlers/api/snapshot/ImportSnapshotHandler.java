package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.snapshot;

import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Map;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class ImportSnapshotHandler extends APIEndpoint {
	public ImportSnapshotHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.IMPORT_SCORE_SNAPSHOT;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject result = new JSONObject();

		JSONObject data = null;

		try {
			String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);

			data = new JSONObject(body);
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			return result;
		}

		try {
			JSONArray players = data.getJSONArray("players");
			JSONArray teams = data.getJSONArray("teams");

			result.put("success", true);

			for (int i = 0; i < players.length(); i++) {
				JSONObject player = players.getJSONObject(i);
				try {
					String sql = "UPDATE players SET score = ?, kills = ? WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setInt(1, player.getInt("score"));
					ps.setInt(2, player.getInt("kills"));
					ps.setString(3, player.getString("uuid"));

					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					Log.error("ImportSnapshotHandler", "An error occured while importing the snapshot. " + e.getClass().getName() + " " + e.getMessage());
				}
			}

			for (int i = 0; i < teams.length(); i++) {
				JSONObject team = teams.getJSONObject(i);

				try {
					String sql = "UPDATE teams SET score = ? WHERE team_number = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setInt(1, team.getInt("score"));
					ps.setInt(2, team.getInt("team_number"));

					ps.executeUpdate();
				} catch (SQLException e) {
					e.printStackTrace();
					Log.error("ImportSnapshotHandler", "An error occured while importing the snapshot. " + e.getClass().getName() + " " + e.getMessage());
				}
			}

			result.put("success", true);
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", "Failed to import snapshot. " + e.getClass().getName() + " " + e.getMessage());
		}
		return result;
	}
}