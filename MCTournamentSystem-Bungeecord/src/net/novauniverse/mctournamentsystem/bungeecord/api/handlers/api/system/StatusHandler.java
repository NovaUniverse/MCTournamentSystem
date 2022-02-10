package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.PlayerData;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.TeamData;
import net.novauniverse.mctournamentsystem.commons.LCS;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")

public class StatusHandler extends APIEndpoint {
	public StatusHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (accessToken != null) {
			json.put("user", accessToken.getUser().getUsername());
		}

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
			String sql = "SELECT p.uuid AS uuid, p.username AS username, p.score AS player_score, p.kills AS kills, t.team_number AS team_number, t.score AS team_score FROM players AS p LEFT JOIN teams AS t ON t.team_number = p.team_number";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				PlayerData playerData = new PlayerData(UUID.fromString(rs.getString("uuid")), rs.getInt("kills"), rs.getInt("player_score"), rs.getInt("team_score"), (teamNumber == 0 ? -1 : teamNumber), rs.getString("username"));

				playerDataList.add(playerData);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		List<TeamData> teamDataList = new ArrayList<TeamData>();
		try {
			String sql = "SELECT * FROM teams";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				TeamData td = new TeamData(rs.getInt("team_number"), rs.getInt("score"));
				teamDataList.add(td);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONArray players = new JSONArray();

		for (PlayerData pd : playerDataList) {
			JSONObject p = new JSONObject();
			// im not going to use a short name for this one
			ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(pd.getUuid());

			boolean online = false;
			int ping = -1;
			String serverName = null;

			if (proxiedPlayer != null) {
				if (proxiedPlayer.isConnected()) {
					if (proxiedPlayer.getServer() != null) {
						online = true;
						serverName = proxiedPlayer.getServer().getInfo().getName();
						ping = proxiedPlayer.getPing();
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
		}

		JSONArray teams = new JSONArray();
		for (TeamData td : teamDataList) {
			JSONObject team = new JSONObject();

			team.put("team_number", td.getTeamNumber());
			team.put("score", td.getScore());

			teams.put(team);
		}

		json.put("players", players);
		json.put("teams", teams);

		/* ===== Online players ===== */
		JSONArray onlinePlayers = new JSONArray();

		for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
			JSONObject p = new JSONObject();

			if (player.getServer() == null) {
				continue;
			}

			p.put("uuid", player.getUniqueId());
			p.put("name", player.getName());
			p.put("server", player.getServer().getInfo().getName());
			p.put("ping", player.getPing());

			onlinePlayers.put(p);
		}

		json.put("online_players", onlinePlayers);

		/* ===== Whitelist ===== */
		JSONArray whitelist = new JSONArray();
		try {
			String sql = "SELECT uuid FROM whitelist";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				whitelist.put(rs.getString("uuid"));
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		json.put("whitelist", whitelist);
		
		/* ===== License ===== */
		JSONObject license = new JSONObject();
		
		license.put("is_valid", LCS.isValid());
		license.put("is_demo", LCS.isDemo());
		license.put("is_expired", LCS.isExpired());
		license.put("expires_at", LCS.getExpiresAt());
		license.put("owner", LCS.getLicensedTo());
		
		json.put("license", license);
		
		/* ===== System ===== */
		JSONObject system = new JSONObject();

		system.put("tournament_name", TournamentSystemCommons.getTournamentName());
		system.put("scoreboard_url", TournamentSystemCommons.getScoreboardURL());

		system.put("proxy_software", ProxyServer.getInstance().getName());
		system.put("proxy_software_version", ProxyServer.getInstance().getVersion());

		system.put("total_memory", Runtime.getRuntime().totalMemory());
		system.put("free_memory", Runtime.getRuntime().freeMemory());
		system.put("cores", Runtime.getRuntime().availableProcessors());

		system.put("os_name", System.getProperty("os.name"));

		json.put("system", system);

		return json;
	}
}