package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.snapshot;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.exception.ExceptionUtils;
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
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class ImportSnapshotHandler extends TournamentEndpoint {
	public ImportSnapshotHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.IMPORT_SCORE_SNAPSHOT;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONObject data = null;
		try {
			data = new JSONObject(request.getBody());
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			result.put("http_response_code", 400);
			return new JSONResponse(result, HTTPResponseCode.BAD_REQUEST);
		}

		TournamentSystemCommons.getDBConnection().getConnection().setAutoCommit(false);
		try {
			List<PlayerIdMapping> playerIds = new ArrayList<>();
			List<TeamIdMapping> teamIds = new ArrayList<>();

			{
				String sql = "SELECT id, uuid FROM players";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					int playerId = rs.getInt("id");
					UUID uuid = UUID.fromString(rs.getString("uuid"));

					playerIds.add(new PlayerIdMapping(uuid, playerId));
				}

				rs.close();
				ps.close();
			}

			{
				String sql = "SELECT id, team_number FROM teams";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();
				while (rs.next()) {
					int teamId = rs.getInt("id");
					int teamNumber = rs.getInt("team_number");

					teamIds.add(new TeamIdMapping(teamId, teamNumber));
				}

				rs.close();
				ps.close();
			}

			{
				String sql = "DELETE FROM player_score";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			}

			{
				String sql = "DELETE FROM team_score";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ps.executeUpdate();
				ps.close();
			}

			JSONArray players = data.getJSONArray("players");
			JSONArray teams = data.getJSONArray("teams");

			for (int i = 0; i < players.length(); i++) {
				JSONObject player = players.getJSONObject(i);

				String uuidString = player.getString("uuid");

				playerIds.stream().filter(p -> p.getUuid().toString().equalsIgnoreCase(uuidString)).findFirst().ifPresent(pid -> {
					try {
						String server = player.getString("server");
						String reason = player.getString("reason");
						int amount = player.getInt("amount");
						String gainedAt = player.getString("gained_at");

						String sql = "INSERT INTO player_score (server, reason, amount, gained_at, player_id) VALUES (?, ?, ?, ?, ?)";
						PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

						ps.setString(1, server);
						ps.setString(2, reason);
						ps.setInt(3, amount);
						ps.setString(4, gainedAt);
						ps.setInt(5, pid.getPlayerId());

						ps.executeUpdate();
						ps.close();
					} catch (Exception ex) {
						ex.printStackTrace();
						Log.error("ImportSnapshotHandler", "An error occured while importing the snapshot. " + ex.getClass().getName() + " " + ex.getMessage());
					}
				});
			}

			for (int i = 0; i < teams.length(); i++) {
				JSONObject team = teams.getJSONObject(i);

				int teamNumber = team.getInt("team_number");

				teamIds.stream().filter(t -> t.getTeamNumber() == teamNumber).findFirst().ifPresent(tid -> {
					try {
						String server = team.getString("server");
						String reason = team.getString("reason");
						int amount = team.getInt("amount");
						String gainedAt = team.getString("gained_at");

						String sql = "INSERT INTO team_score (server, reason, amount, gained_at, team_id) VALUES (?, ?, ?, ?, ?)";
						PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

						ps.setString(1, server);
						ps.setString(2, reason);
						ps.setInt(3, amount);
						ps.setString(4, gainedAt);
						ps.setInt(5, tid.getTeamId());

						ps.executeUpdate();
						ps.close();
					} catch (Exception ex) {
						ex.printStackTrace();
						Log.error("ImportSnapshotHandler", "An error occured while importing the snapshot. " + ex.getClass().getName() + " " + ex.getMessage());
					}
				});
			}
		} catch (Exception e) {
			TournamentSystemCommons.getDBConnection().getConnection().rollback();
			TournamentSystemCommons.getDBConnection().getConnection().setAutoCommit(true);
			throw e;
		}
		TournamentSystemCommons.getDBConnection().getConnection().commit();
		TournamentSystemCommons.getDBConnection().getConnection().setAutoCommit(true);

		result.put("success", true);

		return new JSONResponse(result, HTTPResponseCode.OK);
	}
}

class TeamIdMapping {
	private final int teamId;
	private final int teamNumber;

	public TeamIdMapping(int teamId, int teamNumber) {
		this.teamId = teamId;
		this.teamNumber = teamNumber;
	}

	public int getTeamId() {
		return teamId;
	}

	public int getTeamNumber() {
		return teamNumber;
	}
}

class PlayerIdMapping {
	private final UUID uuid;
	private final int playerId;

	public PlayerIdMapping(UUID uuid, int playerId) {
		this.uuid = uuid;
		this.playerId = playerId;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getPlayerId() {
		return playerId;
	}
}