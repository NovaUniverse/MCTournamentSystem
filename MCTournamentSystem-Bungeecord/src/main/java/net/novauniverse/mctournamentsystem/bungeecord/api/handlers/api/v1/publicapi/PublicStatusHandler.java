package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.publicapi;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.PlayerData;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.TeamData;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.team.TeamColorProvider;
import net.novauniverse.mctournamentsystem.commons.team.TeamNameProvider;
import net.zeeraa.novacore.bungeecord.utils.ChatColorRGBMapper;

public class PublicStatusHandler extends TournamentEndpoint {
	public PublicStatusHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		/* ===== Servers ===== */
		JSONArray servers = new JSONArray();
		for (String key : ProxyServer.getInstance().getServers().keySet()) {
			ServerInfo serverInfo = ProxyServer.getInstance().getServers().get(key);

			JSONObject server = new JSONObject();

			server.put("name", serverInfo.getName());
			server.put("player_count", serverInfo.getPlayers().size());

			servers.put(server);
		}

		json.put("servers", servers);

		/* ===== Players and Teams ===== */
		List<PlayerData> playerDataList = new ArrayList<PlayerData>();

		try {
			String sql = "SELECT "
					+ "	p.uuid AS uuid,"
					+ "	p.username AS username,"
					+ "	p.kills AS kills,"
					+ "	t.team_number AS team_number,"
					+ "	IFNULL(SUM(ps.amount), 0) AS total_score,"
					+ "	IFNULL(SUM(ts.amount), 0) AS team_score"
					+ " FROM players AS p"
					+ " LEFT JOIN player_score AS ps"
					+ "	ON ps.player_id = p.id"
					+ " LEFT JOIN teams AS t"
					+ "	ON t.team_number = p.team_number"
					+  "LEFT JOIN team_score AS ts"
					+ "	ON ts.team_id = t.id"
					+ " GROUP BY p.id";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				PlayerData playerData = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getInt("kills"), rs.getInt("total_score"), rs.getInt("team_score"), (teamNumber == 0 ? -1 : teamNumber), rs.getString("username"), new JSONObject());

				playerDataList.add(playerData);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		List<TeamData> teamDataList = new ArrayList<TeamData>();
		try {
			String sql = "SELECT"
					+ "	t.team_number AS team_number,"
					+ "	t.kills AS kills,"
					+ "	IFNULL(SUM(s.amount), 0) AS total_score"
					+ " FROM teams AS t"
					+ " LEFT JOIN team_score AS s"
					+ "	ON s.team_id = t.id"
					+ " GROUP BY t.id";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				TeamData td = new TeamData(rs.getInt("team_number"), rs.getInt("total_score"), rs.getInt("kills"));
				teamDataList.add(td);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		JSONArray playerServerData = new JSONArray();
		TournamentSystem.getInstance().getPlayerTelementryManager().getData().values().forEach(d -> playerServerData.put(d.toJSON()));
		json.put("player_server_data", playerServerData);

		JSONArray players = new JSONArray();

		playerDataList.forEach(pd -> {
			JSONObject p = new JSONObject();

			ProxiedPlayer pp = ProxyServer.getInstance().getPlayer(pd.getUuid());

			boolean online = false;
			int ping = -1;
			String serverName = null;

			if (pp != null) {
				if (pp.isConnected()) {
					if (pp.getServer() != null) {
						online = true;
						serverName = pp.getServer().getInfo().getName();
						ping = pp.getPing();
					}
				}
			}

			p.put("online", online);
			p.put("server", serverName);
			p.put("uuid", pd.getUuid());
			p.put("username", pd.getUsername());
			p.put("ping", ping);
			p.put("kills", pd.getKills());
			p.put("score", pd.getScore());
			p.put("team_score", pd.getTeamScore());
			p.put("team_number", pd.getTeamNumber());

			players.put(p);
		});

		JSONArray teams = new JSONArray();
		teamDataList.forEach(td -> {
			JSONObject team = new JSONObject();

			team.put("team_number", td.getTeamNumber());
			team.put("score", td.getScore());
			team.put("kills", td.getKills());
			team.put("color", ChatColorRGBMapper.chatColorToRGBColorData(TeamColorProvider.getTeamColor(td.getTeamNumber())).toJSON());
			team.put("display_name", TeamNameProvider.getDisplayName(td.getTeamNumber()));

			teams.put(team);
		});

		json.put("players", players);
		json.put("teams", teams);

		/* ===== Online players ===== */
		JSONArray onlinePlayers = new JSONArray();

		ProxyServer.getInstance().getPlayers().forEach(player -> {
			JSONObject p = new JSONObject();

			if (player.getServer() == null) {
				return;
			}

			p.put("uuid", player.getUniqueId());
			p.put("name", player.getName());
			p.put("server", player.getServer().getInfo().getName());
			p.put("ping", player.getPing());

			onlinePlayers.put(p);
		});

		json.put("offline_mode", TournamentSystem.getInstance().isOfflineMode());

		json.put("online_players", onlinePlayers);

		json.put("active_server", TournamentSystemCommons.getActiveServer());
		json.put("next_minigame", TournamentSystemCommons.getNextMinigame());
		json.put("dynamic_config_url", TournamentSystem.getInstance().getDynamicConfigUrl());

		return new JSONResponse(json);
	}
}