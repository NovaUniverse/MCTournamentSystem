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
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;

public class ScoreHandler extends TournamentEndpoint {
	public ScoreHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.DELETE, HTTPMethod.PUT);

		setMethodBasedPermission(HTTPMethod.DELETE, AuthPermission.ALTER_SCORE);
		setMethodBasedPermission(HTTPMethod.PUT, AuthPermission.ALTER_SCORE);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (request.getMethod() == HTTPMethod.DELETE && request.getQueryParameters().containsKey("all")) {
			{
				String sql = "DELETE FROM player_score";
				PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			}

			{
				String sql = "DELETE FROM team_score";
				PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			}

			json.put("message", "ok");
		} else {

			if (request.getMethod() == HTTPMethod.GET) {
				JSONArray players = new JSONArray();
				JSONArray teams = new JSONArray();

				{
					String sql = "SELECT s.amount AS amount, s.server AS server, s.reason AS reason, s.gained_at AS gained_at, s.id AS id, s.player_id AS player_id, p.username AS username, p.uuid AS uuid FROM player_score AS s LEFT JOIN players AS p ON p.id = s.player_id";
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
					String sql = "SELECT s.amount AS amount, s.server AS server, s.reason AS reason, s.gained_at AS gained_at, s.id AS id, s.team_id AS team_id, t.team_number AS team_number FROM team_score AS s LEFT JOIN teams AS t ON t.id = s.team_id";
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
						teams.put(score);
					}
				}

				json.put("players", players);
				json.put("teams", teams);
			} else {
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
					target = TargetType.valueOf(parameters.get("type").toUpperCase());
				} catch (IllegalArgumentException argumentException) {
					json.put("message", "Invalid type value. Valid ones are TEAM and PLAYER");
					return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
				}

				if (request.getMethod() == HTTPMethod.DELETE) {
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
				} else if (request.getMethod() == HTTPMethod.PUT) {
					JSONObject data = null;
					try {
						data = new JSONObject(request.getBody());
					} catch (Exception e) {
						json.put("error", "Failed to parse body as json");
						return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
					}

					String reason = "";
					if (data.has("reason")) {
						reason = data.getString("reason");
					}

					if (!data.has("amount")) {
						json.put("error", "Missing body value: amount");
						return new JSONResponse(json, HTTPResponseCode.BAD_REQUEST);
					}

					int amount = data.getInt("amount");

					boolean found = false;
					if (target == TargetType.PLAYER) {
						String sql = "SELECT id FROM players WHERE id = ? LIMIT 1";
						PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
						ps.setInt(1, id);

						ResultSet rs = ps.executeQuery();

						if (rs.next()) {
							found = true;
						}

						rs.close();
						ps.close();
					} else {
						String sql = "SELECT id FROM teams WHERE id = ? LIMIT 1";
						PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
						ps.setInt(1, id);

						ResultSet rs = ps.executeQuery();

						if (rs.next()) {
							found = true;
						}

						rs.close();
						ps.close();
					}

					if (!found) {
						json.put("error", "Target not found");
						return new JSONResponse(json, HTTPResponseCode.NOT_FOUND);
					}

					String sql;
					if (target == TargetType.PLAYER) {
						sql = "INSERT INTO player_score (player_id, reason, amount) VALUES (?, ?, ?)";
					} else {
						sql = "INSERT INTO team_score (team_id, reason, amount) VALUES (?, ?, ?)";
					}

					PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
					ps.setInt(1, id);
					ps.setString(2, reason);
					ps.setInt(3, amount);
					ps.executeUpdate();
					ps.close();

					json.put("message", "ok");
				}
			}
		}

		return new JSONResponse(json);
	}
}

enum TargetType {
	TEAM, PLAYER;
}