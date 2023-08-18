package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.score;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class ScoreHandler extends TournamentEndpoint {
	public ScoreHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.DELETE);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (request.getMethod() == HTTPMethod.GET) {
			JSONArray players = new JSONArray();
			JSONArray teams = new JSONArray();

			{
				String sql = "SELECT s.server AS server, s.reason AS reason, s.gained_at AS gained_at, s.id AS id, s.player_id AS player_id, p.username AS username, p.uuid AS uuid FROM player_score AS s LEFT JOIN players AS p ON p.id = s.player_id";
				PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("id");
					String server = rs.getString("server");
					String reason = rs.getString("reason");
					String gainedAt = rs.getString("gained_at");
					int amount = rs.getInt("amount");

					int playerId = rs.getInt("player_id");
					String uuid = rs.getString("uuid");
					String username = rs.getString("username");

					JSONObject score = new JSONObject();
					JSONObject owner = new JSONObject();

					score.put("id", id);
					score.put("server", server);
					score.put("reason", reason);
					score.put("gained_at", gainedAt);
					score.put("amount", amount);

					owner.put("id", playerId);
					owner.put("uuid", uuid);
					owner.put("username", username);

					score.put("player", owner);
					players.put(score);
				}
			}

			{
				String sql = "SELECT s.server AS server, s.reason AS reason, s.gained_at AS gained_at, s.id AS id, s.team_id AS team_id, t.team_number AS team_number FROM team_score AS s LEFT JOIN teams AS t ON t.id = s.team_id";
				PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					int id = rs.getInt("id");
					String server = rs.getString("server");
					String reason = rs.getString("reason");
					String gainedAt = rs.getString("gained_at");
					int amount = rs.getInt("amount");

					int teamId = rs.getInt("team_id");
					int teamNumber = rs.getInt("team_number");

					JSONObject score = new JSONObject();
					JSONObject owner = new JSONObject();

					score.put("id", id);
					score.put("server", server);
					score.put("reason", reason);
					score.put("gained_at", gainedAt);
					score.put("amount", amount);

					owner.put("id", teamId);
					owner.put("team_number", teamNumber);

					score.put("team", owner);
					players.put(score);
				}
			}

			json.put("players", players);
			json.put("teams", teams);
		} else if (request.getMethod() == HTTPMethod.DELETE) {
			Map<String, String> parameters = request.getQueryParameters();

			if (!parameters.containsKey("id")) {
				json.put("message", "Missing parameter: id");
				return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
			}

			int id;
			try {
				id = Integer.parseInt(parameters.get("id"));
			} catch (NumberFormatException parseException) {
				json.put("message", "Invalid parameter value for int id");
				return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
			}

			if (!parameters.containsKey("type")) {
				json.put("message", "Missing parameter: type");
				return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
			}

			TargetType target;
			try {
				target = TargetType.valueOf(parameters.get("type"));
			} catch (IllegalArgumentException argumentException) {
				json.put("message", "Invalid type value. Valid ones are TEAM and PLAYER");
				return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
			}

			String sql;
			if (target == TargetType.PLAYER) {
				sql = "DELETE FROM player_score WHERE id = ?";
			} else {
				sql = "DELETE FROM team_score WHERE id = ?";
			}

			PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
			ps.setInt(1, id);
			ps.executeUpdate();
			ps.close();
			json.put("message", "ok");
		}

		return new JSONResponse(json);
	}
}

enum TargetType {
	TEAM, PLAYER;
}