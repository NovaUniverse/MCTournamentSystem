package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ExportSnapshotHandler extends TournamentEndpoint {
	public ExportSnapshotHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
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
			throw e;
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
			throw e;
		}

		JSONObject data = new JSONObject();

		data.put("players", players);
		data.put("teams", teams);

		result.put("success", true);
		result.put("data", data);

		return new JSONResponse(result);
	}
}