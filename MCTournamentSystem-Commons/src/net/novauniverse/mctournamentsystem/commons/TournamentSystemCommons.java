package net.novauniverse.mctournamentsystem.commons;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.log.Log;

public class TournamentSystemCommons {
	public static final String HASTEBIN_BASE_URL = "https://hastebin.novauniverse.net";
	public static final char CHAT_COLOR_CHAR = (char) 0xA7;
	public static final String DATA_CHANNEL = "mcts:controller";
	public static final String PLAYER_TELEMENTRY_CHANNEL = "mcts:ptelementry";

	private static JSONObject tournamentSystemConfigData;

	private static UUID sessionId = null;

	public static UUID getSessionId() {
		if (sessionId == null) {
			sessionId = UUID.randomUUID();
			Log.debug("TournamentSystemCommons", "Init sessionId as " + sessionId.toString());
		}
		return sessionId;
	}

	private static DBConnection dbConnection;

	public static void setDBConnection(DBConnection dbConnection) {
		TournamentSystemCommons.dbConnection = dbConnection;
	}

	public static DBConnection getDBConnection() {
		return dbConnection;
	}

	public static String getTournamentName() {
		try {
			String name = TournamentSystemCommons.getConfigValue("tournament_name");
			if (name != null) {
				return ChatColor.translateAlternateColorCodes(TournamentSystemCommons.CHAT_COLOR_CHAR, name);
			}
		} catch (Exception e) {
			Log.error("TournamentSystemCommons", "Failed to get tournament name. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static boolean setTournamentName(String tournamentName) {
		try {
			TournamentSystemCommons.setConfigValue("tournament_name", tournamentName);
			return true;
		} catch (SQLException e) {
			Log.error("TournamentSystemCommons", "Failed to set tournament name. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static String getScoreboardURL() {
		try {
			return TournamentSystemCommons.getConfigValue("scoreboard_url");
		} catch (SQLException e) {
			Log.error("TournamentSystemCommons", "Failed to get scoreboard url. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static boolean setScoreboardURL(String tournamentName) {
		try {
			TournamentSystemCommons.setConfigValue("scoreboard_url", tournamentName);
			return true;
		} catch (SQLException e) {
			Log.error("TournamentSystemCommons", "Failed to set scoreboard url. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static boolean hasConfigValue(String key) throws SQLException {
		boolean result = false;
		try {
			String sql = "SELECT id FROM tsdata WHERE data_key = ?";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, key);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = true;
			}

			rs.close();
			ps.close();
		} catch (Exception ee) {
			Log.error("TournamentSystemCommons", "Failed to check if config value exist. " + ee.getClass().getName() + " " + ee.getMessage());
			ee.printStackTrace();
		}

		return result;
	}

	public static String getConfigValue(String key) throws SQLException {
		String result = null;
		try {
			String sql = "SELECT data_value FROM tsdata WHERE data_key = ?";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.setString(1, key);

			ResultSet rs = ps.executeQuery();
			if (rs.next()) {
				result = rs.getString("data_value");
			}

			rs.close();
			ps.close();
		} catch (Exception ee) {
			Log.error("TournamentSystemCommons", "Failed to fetch config value. " + ee.getClass().getName() + " " + ee.getMessage());
			ee.printStackTrace();
		}

		return result;
	}

	public static void setConfigValue(String key, String value) throws SQLException {
		String sql = null;

		if (TournamentSystemCommons.hasConfigValue(key)) {
			sql = "UPDATE tsdata SET data_value = ? WHERE data_key = ?";
		} else {
			sql = "INSERT INTO tsdata (data_value, data_key) VALUES (?, ?)";
		}

		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

		ps.setString(1, value);
		ps.setString(2, key);

		ps.executeUpdate();

		ps.close();
	}

	public static String getActiveServer() {
		String name = null;
		try {
			name = TournamentSystemCommons.getConfigValue("active_server");
		} catch (SQLException e) {
			Log.error("TournamentSystemCommons", "Failed to fetch active server. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return name;
	}

	public static boolean setActiveServer(String serverName) {
		try {
			TournamentSystemCommons.setConfigValue("active_server", serverName);
			return true;
		} catch (Exception e) {
			Log.error("TournamentSystemCommons", "Failed to set reconnect server. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static String getNextMinigame() {
		String name = null;
		try {
			name = TournamentSystemCommons.getConfigValue("next_minigame");
			if (name != null) {
				if (name.length() == 0) {
					name = null;
				}
			}
		} catch (SQLException e) {
			Log.error("TournamentSystemCommons", "Failed to fetch next minigame server. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return name;
	}

	public static boolean clearNextMinigame() {
		return TournamentSystemCommons.setNextMinigame(null);
	}

	public static boolean setNextMinigame(String name) {
		try {
			TournamentSystemCommons.setConfigValue("next_minigame", name);
			return true;
		} catch (Exception e) {
			Log.error("TournamentSystemCommons", "Failed to set next minigame. " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	public static JSONObject getTournamentSystemConfigData() {
		return tournamentSystemConfigData;
	}

	public static void setTournamentSystemConfigData(JSONObject tournamentSystemConfigData) {
		JSONObject copy = new JSONObject(tournamentSystemConfigData.toString());
		TournamentSystemCommons.tournamentSystemConfigData = copy;
	}
}