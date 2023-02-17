package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ExportSnapshotHandler extends APIEndpoint {
	public ExportSnapshotHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray players = new JSONArray();
		JSONArray teams = new JSONArray();

		try {
			String sql = "SELECT * FROM players";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String uuid = rs.getString("uuid");
				int score = rs.getInt("score");
				int kills = rs.getInt("kills");

				JSONObject json = new JSONObject();
				json.put("uuid", uuid);
				json.put("score", score);
				json.put("kills", kills);

				players.put(json);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getClass().getName() + " " + e.getMessage());
			result.put("http_response_code", 500);
			return result;
		}

		try {
			String sql = "SELECT * FROM teams";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				int score = rs.getInt("score");

				JSONObject json = new JSONObject();
				json.put("team_number", teamNumber);
				json.put("score", score);

				teams.put(json);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			result.put("success", false);
			result.put("message", e.getClass().getName() + " " + e.getMessage());
			result.put("http_response_code", 500);
			return result;
		}

		JSONObject data = new JSONObject();

		data.put("players", players);
		data.put("teams", teams);

		result.put("success", true);
		result.put("data", data);

		return result;
	}
}