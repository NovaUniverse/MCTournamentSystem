package net.novauniverse.mctournamentsystem.commons;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.commons.database.DBConnection;

public class TournamentSystemCommons {
	public static final String DATA_CHANNEL = "tsys:tsdata";

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
				return ChatColor.translateAlternateColorCodes('§', name);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean setTournamentName(String tournamentName) {
		try {
			TournamentSystemCommons.setConfigValue("tournament_name", tournamentName);
			return true;
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return false;
	}

	public static String getScoreboardURL() {
		try {
			return TournamentSystemCommons.getConfigValue("scoreboard_url");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	public static boolean setScoreboardURL(String tournamentName) {
		try {
			TournamentSystemCommons.setConfigValue("scoreboard_url", tournamentName);
			return true;
		} catch (SQLException e) {
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
			e.printStackTrace();
		}
		return name;
	}

	public static boolean setActiveServer(String serverName) {
		try {
			TournamentSystemCommons.setConfigValue("active_server", serverName);
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}
}