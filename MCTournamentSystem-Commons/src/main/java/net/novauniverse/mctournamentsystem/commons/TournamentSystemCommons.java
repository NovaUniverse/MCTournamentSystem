package net.novauniverse.mctournamentsystem.commons;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.UUID;

import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.TournamentRabbitMQManager;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;

public class TournamentSystemCommons {
	public static final String HASTEBIN_BASE_URL = "https://hastebin.novauniverse.net";
	public static final char CHAT_COLOR_CHAR = (char) 0xA7;
	public static final String DATA_CHANNEL = "mcts:controller";
	public static final String PLAYER_TELEMENTRY_CHANNEL = "mcts:ptelementry";

	public static final String IP_REGEX_IPv4 = "\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b";
	public static final String IP_REGEX_IPv6 = "\\b(?:[0-9a-fA-F]{1,4}:){7}[0-9a-fA-F]{1,4}\\b";

	private static JSONObject tournamentSystemConfigData;

	private static TournamentRabbitMQManager rabbitMQManager = null;

	private static UUID sessionId = null;

	private static DBConnection dbConnection;

	public static void setDBConnection(DBConnection dbConnection) {
		TournamentSystemCommons.dbConnection = dbConnection;
	}

	public static DBConnection getDBConnection() {
		return dbConnection;
	}

	public static void setupRabbitMQ() {
		if (rabbitMQManager != null) {
			Log.info("Tournamentsystem", "Existing RabbitMQ connection detected. Closing it before trying to connect again");
			rabbitMQManager.close();
		}

		String connectionString = System.getenv("RABBITMQ_CONNECTION_STRING");
		String host = System.getenv("RABBITMQ_HOST");
		String portStr = System.getenv("RABBITMQ_PORT");

		int port = 5672;

		JSONObject config = tournamentSystemConfigData.optJSONObject("rabbitmq", new JSONObject());

		if (connectionString == null) {
			connectionString = config.optString("connection_string", null);
		}

		if (connectionString == null) {
			if (host == null) {
				host = config.optString("host", "127.0.0.1");
			}

			if (portStr != null) {
				try {
					port = Integer.parseInt(portStr);
				} catch (Exception e) {
					Log.error("TournamentSystem", "Failed to parse port number for rabbitmq: " + portStr + ". Using default: " + port);
				}
			} else {
				port = config.optInt("port", port);
			}
		}

		if (connectionString != null) {
			Log.info("TournamentSystem", "Connecting to RabbitMQ using connection string");
			rabbitMQManager = new TournamentRabbitMQManager(connectionString);
		} else if (host != null) {
			Log.info("TournamentSystem", "Connecting to RabbitMQ " + host + ":" + port);
			rabbitMQManager = new TournamentRabbitMQManager(host, port);
		} else {
			Log.error("TournamentSystem", "No RabbitMQ server configured. Some features will be unavailable");
		}
	}

	public static UUID getSessionId() {
		if (sessionId == null) {
			sessionId = UUID.randomUUID();
			Log.debug("TournamentSystemCommons", "Init sessionId as " + sessionId.toString());
		}
		return sessionId;
	}

	public static TournamentRabbitMQManager getRabbitMQManager() {
		return rabbitMQManager;
	}

	public static boolean hasRabbitMQManager() {
		return rabbitMQManager != null;
	}

	public static DBCredentials tryReadCredentialsFromENV() {
		String driver = System.getenv("DB_DRIVER");
		String host = System.getenv("DB_HOST");
		String portStr = System.getenv("DB_PORT");
		String username = System.getenv("DB_USERNAME");
		String password = System.getenv("DB_PASSWORD");
		String database = System.getenv("DB_DATABASE");

		int port = 3306;

		if (driver == null) {
			driver = "com.mysql.jdbc.Driver";
		}

		if (host == null) {
			Log.warn("TournamentSystem", "Cant read credentials from ENV data since DB_HOST is null");
			return null;
		}

		if (username == null) {
			Log.warn("TournamentSystem", "Cant read credentials from ENV data since DB_USERNAME is null");
			return null;
		}

		if (password == null) {
			Log.warn("TournamentSystem", "Cant read credentials from ENV data since DB_PASSWORD is null");
			return null;
		}

		if (database == null) {
			Log.warn("TournamentSystem", "Cant read credentials from ENV data since DB_DATABASE is null");
			return null;
		}

		if (portStr != null) {
			try {
				port = Integer.parseInt(portStr);
			} catch (Exception e) {
				Log.error("TournamentSystem", "Cant read credentials from ENV data since DB_PORT could not be parsed as an integer");
				return null;
			}
		}

		return new DBCredentials(driver, "jdbc:mysql://" + host + ":" + port, username, password, database);
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
		String sql = "SELECT id FROM tsdata WHERE data_key = ?";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

		ps.setString(1, key);

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			result = true;
		}

		rs.close();
		ps.close();

		return result;
	}

	public static String getConfigValue(String key) throws SQLException {
		String result = null;
		String sql = "SELECT data_value FROM tsdata WHERE data_key = ?";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

		ps.setString(1, key);

		ResultSet rs = ps.executeQuery();
		if (rs.next()) {
			result = rs.getString("data_value");
		}

		rs.close();
		ps.close();

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