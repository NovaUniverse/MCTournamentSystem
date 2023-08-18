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

		{
			String sql = "SELECT s.server AS server, s.reason AS reason, s.amount AS amount, s.gained_at AS gained_at, p.uuid AS uuid FROM player_score AS s LEFT JOIN players AS p ON p.id = s.player_id";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				String uuid = rs.getString("uuid");
				String server = rs.getString("server");
				String reason = rs.getString("reason");
				int amount = rs.getInt("amount");
				String gainedAt = rs.getString("gained_at");

				JSONObject entry = new JSONObject();

				entry.put("uuid", uuid);
				entry.put("server", server);
				entry.put("reason", reason);
				entry.put("amount", amount);
				entry.put("gained_at", gainedAt);

				players.put(entry);
			}

			rs.close();
			ps.close();
		}

		{
			String sql = "SELECT s.server AS server, s.reason AS reason, s.amount AS amount, s.gained_at AS gained_at, t.team_number AS team_number FROM team_score AS s LEFT JOIN teams AS t ON t.id = s.team_id";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				String server = rs.getString("server");
				String reason = rs.getString("reason");
				int amount = rs.getInt("amount");
				String gainedAt = rs.getString("gained_at");

				JSONObject entry = new JSONObject();

				entry.put("team_number", teamNumber);
				entry.put("server", server);
				entry.put("reason", reason);
				entry.put("amount", amount);
				entry.put("gained_at", gainedAt);

				teams.put(entry);
			}

			rs.close();
			ps.close();
		}

		result.put("players", players);
		result.put("teams", teams);

		return new JSONResponse(result);
	}
}