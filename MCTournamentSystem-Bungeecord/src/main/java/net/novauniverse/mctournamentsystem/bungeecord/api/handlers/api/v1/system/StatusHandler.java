package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system;

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
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserAuth;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.PlayerData;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.TeamData;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.config.InternetCafeOptions;
import net.novauniverse.mctournamentsystem.commons.team.TeamColorProvider;
import net.novauniverse.mctournamentsystem.commons.team.TeamNameProvider;
import net.novauniverse.mctournamentsystem.commons.winner.LockedWinnerManagement;
import net.zeeraa.novacore.bungeecord.utils.ChatColorRGBMapper;
import net.zeeraa.novacore.commons.jarresourcereader.JARResourceReader;

public class StatusHandler extends TournamentEndpoint {
	public StatusHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (authentication instanceof UserAuth) {
			JSONObject loggedInUser = new JSONObject();
			UserAuth auth = (UserAuth) authentication;
			loggedInUser.put("username", auth.getUser().getUsername());
			loggedInUser.put("permissions", auth.getUser().getPermissionsAsJSON());
			loggedInUser.put("type", auth.getType().name());
			loggedInUser.put("can_manage_accounts", auth.getUser().isAllowManagingAccounts());
			json.put("user", loggedInUser);
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
			String sql = JARResourceReader.readFileFromJARAsString(getClass(), "/sql/api/v1/status/get_player_data.sql");
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				JSONObject metadata = new JSONObject();
				if (rs.getString("metadata") != null) {
					metadata = new JSONObject(rs.getString("metadata"));
				}
				PlayerData playerData = new PlayerData(rs.getInt("id"), UUID.fromString(rs.getString("uuid")), rs.getInt("kills"), rs.getInt("player_score"), rs.getInt("team_score"), (teamNumber == 0 ? -1 : teamNumber), rs.getString("username"), metadata);

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
			String sql = JARResourceReader.readFileFromJARAsString(getClass(), "/sql/api/v1/status/get_team_data.sql");
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				TeamData td = new TeamData(rs.getInt("id"), rs.getInt("team_number"), rs.getInt("total_score"), rs.getInt("kills"));
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

			p.put("id", pd.getId());
			p.put("online", online);
			p.put("server", serverName);
			p.put("uuid", pd.getUuid());
			p.put("username", pd.getUsername());
			p.put("ping", ping);
			p.put("kills", pd.getKills());
			p.put("score", pd.getScore());
			p.put("team_score", pd.getTeamScore());
			p.put("team_number", pd.getTeamNumber());
			p.put("metadata", pd.getMetadata());

			players.put(p);
		});

		JSONArray teams = new JSONArray();
		teamDataList.forEach(td -> {
			JSONObject team = new JSONObject();

			team.put("id", td.getTeamId());
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

		json.put("online_players", onlinePlayers);

		/* ===== Whitelist ===== */
		JSONArray whitelist = new JSONArray();
		try {
			String sql = "SELECT uuid, username, offline_mode FROM whitelist";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject entry = new JSONObject();

				entry.put("uuid", rs.getString("uuid"));
				entry.put("username", rs.getString("username"));
				entry.put("offline_mode", rs.getBoolean("offline_mode"));

				whitelist.put(entry);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		json.put("whitelist", whitelist);
		
		
		/* ===== Locked winner ===== */;
		int lockedWinnerTeamNumber = LockedWinnerManagement.getLockedWinner();

		/* ===== Internet cafe settings ===== */
		InternetCafeOptions internetCafeOptions = TournamentSystem.getInstance().getInternetCafeOptions();

		JSONObject internetCafeSettings = new JSONObject();
		JSONObject ggrock = new JSONObject();

		ggrock.put("enabled", internetCafeOptions.hasGGRockURL());
		ggrock.put("url", internetCafeOptions.getGGRockURL());

		internetCafeSettings.put("ggrock", ggrock);

		json.put("internet_cafe_settings", internetCafeSettings);

		/* ===== System ===== */
		JSONObject system = new JSONObject();

		system.put("offline_mode", TournamentSystem.getInstance().isOfflineMode());

		system.put("tournament_name", TournamentSystemCommons.getTournamentName());
		system.put("scoreboard_url", TournamentSystemCommons.getScoreboardURL());

		system.put("proxy_software", ProxyServer.getInstance().getName());
		system.put("proxy_software_version", ProxyServer.getInstance().getVersion());

		system.put("total_memory", Runtime.getRuntime().totalMemory());
		system.put("free_memory", Runtime.getRuntime().freeMemory());
		system.put("cores", Runtime.getRuntime().availableProcessors());

		system.put("team_size", TournamentSystem.getInstance().getTeamSize());

		system.put("os_name", System.getProperty("os.name"));
		system.put("linux_distro", TournamentSystem.getInstance().getDistroName());

		system.put("public_ip", TournamentSystem.getInstance().getPublicIp());
		system.put("dynamic_config_url", TournamentSystem.getInstance().getDynamicConfigUrl());

		system.put("motd", TournamentSystem.getInstance().getMotd());

		json.put("system", system);

		json.put("active_server", TournamentSystemCommons.getActiveServer());
		json.put("next_minigame", TournamentSystemCommons.getNextMinigame());
		
		json.put("locked_winner", lockedWinnerTeamNumber);

		return new JSONResponse(json);
	}
}