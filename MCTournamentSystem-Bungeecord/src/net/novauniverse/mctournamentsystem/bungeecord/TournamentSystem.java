package net.novauniverse.mctournamentsystem.bungeecord;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.novauniverse.mctournamentsystem.bungeecord.api.WebServer;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIKeyStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUserStore;
import net.novauniverse.mctournamentsystem.bungeecord.commands.sendhere.SendHereCommand;
import net.novauniverse.mctournamentsystem.bungeecord.listener.JoinEvents;
import net.novauniverse.mctournamentsystem.bungeecord.listener.TSPluginMessageListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.WhitelistListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.playertelementry.PlayerTelementryManager;
import net.novauniverse.mctournamentsystem.bungeecord.listener.security.Log4JRCEFix;
import net.novauniverse.mctournamentsystem.commons.LCS;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;

public class TournamentSystem extends NovaPlugin implements Listener {
	private static TournamentSystem instance;

	private WebServer webServer;
	private boolean webserverDevelopmentMode;
	private List<String> staffRoles;
	private List<String> quickMessages;
	private int teamSize;

	private String phpmyadminURL;

	private PlayerTelementryManager playerTelementryManager;

	public static TournamentSystem getInstance() {
		return instance;
	}

	public boolean isWebserverDevelopmentMode() {
		return webserverDevelopmentMode;
	}

	public List<String> getStaffRoles() {
		return staffRoles;
	}

	public List<String> getQuickMessages() {
		return quickMessages;
	}

	public PlayerTelementryManager getPlayerTelementryManager() {
		return playerTelementryManager;
	}

	public String getPHPMyAdminURL() {
		return phpmyadminURL;
	}

	public int getTeamSize() {
		return teamSize;
	}

	@Override
	public void onLoad() {
		TournamentSystem.instance = this;
		staffRoles = new ArrayList<>();
	}

	@Override
	public void onEnable() {
		saveDefaultConfiguration();

		quickMessages = new ArrayList<>();

		String globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();

		File configFile = new File(globalConfigPath + File.separator + "tournamentconfig.json");
		JSONObject config;
		try {
			if (!configFile.exists()) {
				Log.fatal("TournamentSystem", "Config file not found at " + configFile.getAbsolutePath());
				ProxyServer.getInstance().stop("Failed to enable tournament system: No config file found");
				return;
			}

			config = JSONFileUtils.readJSONObjectFromFile(configFile);
		} catch (Exception e) {
			Log.fatal("TournamentSystem", "Failed to parse the config file at " + configFile.getAbsolutePath());
			e.printStackTrace();
			ProxyServer.getInstance().stop("Failed to enable tournament system: Failed to read config file");
			return;
		}

		// Read configuration values
		this.phpmyadminURL = config.getString("phpmyadmin_url");
		this.teamSize = config.getInt("team_size");

		JSONArray staffRolesJSON = config.getJSONArray("staff_roles");
		for (int i = 0; i < staffRolesJSON.length(); i++) {
			staffRoles.add(staffRolesJSON.getString(i));
		}

		JSONObject mysqlDatabaseConfig = config.getJSONObject("database").getJSONObject("mysql");

		DBCredentials dbCredentials = new DBCredentials(mysqlDatabaseConfig.getString("driver"), mysqlDatabaseConfig.getString("host"), mysqlDatabaseConfig.getString("username"), mysqlDatabaseConfig.getString("password"), mysqlDatabaseConfig.getString("database"));

		try {
			DBConnection dbConnection;
			dbConnection = new DBConnection();
			dbConnection.connect(dbCredentials);
			dbConnection.startKeepAliveTask();

			TournamentSystemCommons.setDBConnection(dbConnection);
		} catch (ClassNotFoundException | SQLException e) {
			Log.fatal("TournamentSystem", "Failed to connect to the database");
			e.printStackTrace();
			ProxyServer.getInstance().stop("Failed to enable tournament system: Database error");
			return;
		}

		playerTelementryManager = new PlayerTelementryManager();

		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new TSPluginMessageListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, playerTelementryManager);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinEvents());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new WhitelistListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new Log4JRCEFix());

		ProxyServer.getInstance().getPluginManager().registerCommand(this, new SendHereCommand());

		File wwwAppFile = new File(getDataFolder().getPath() + File.separator + "www_app");

		try {
			FileUtils.forceMkdir(wwwAppFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		JSONArray quickMessagesJson = config.getJSONArray("quick_messages");
		for (int i = 0; i < quickMessagesJson.length(); i++) {
			quickMessages.add(ChatColor.translateAlternateColorCodes(TournamentSystemCommons.CHAT_COLOR_CHAR, quickMessagesJson.getString(i)));
		}

		Log.info("Setting up web server");

		JSONObject webConfig = config.getJSONObject("web_ui");
		JSONObject commentatorKeys = config.getJSONObject("commentator_keys");
		JSONArray apiKeys = webConfig.getJSONArray("api_keys");
		JSONArray webUsers = webConfig.getJSONArray("users");

		if (webUsers.length() == 0) {
			Log.warn("TournamentSystem", "No users defined for web server in " + configFile.getAbsolutePath() + ". The web ui might not work");
		}

		commentatorKeys.keySet().forEach(key -> {
			APIKeyStore.addCommentatorKey(key, UUID.fromString(commentatorKeys.getString(key)));
		});

		for (int i = 0; i < apiKeys.length(); i++) {
			APIKeyStore.addApiKey(apiKeys.getString(i));
		}
		Log.info("TournamentSystem", APIKeyStore.getApiKeys().size() + " api keys loaded");

		for (int i = 0; i < webUsers.length(); i++) {
			JSONObject user = webUsers.getJSONObject(i);
			String username = user.getString("username");

			APIUserStore.addUser(new APIUser(username, user.getString("password")));

			Log.info("TournamentSystem", "Added user " + username + " to the web ui users");
		}

		Log.info("TournamentSystem", APIUserStore.getUsers().size() + " user" + (APIUserStore.getUsers().size() == 1 ? "" : "s") + " configured for web ui");

		try {
			int port = webConfig.getInt("port");
			webserverDevelopmentMode = webConfig.getBoolean("development_mode");

			if (webserverDevelopmentMode) {
				Log.warn("TournamentSystem", "Development mode enabled for web server. No autentication will be required to access the web ui and api");
			}

			Log.info("Starting web server on port " + port);
			webServer = new WebServer(port, wwwAppFile.getPath());
			Log.success("Web server started");
		} catch (Exception e) {
			Log.fatal("Failed to start web server");
			e.printStackTrace();
			ProxyServer.getInstance().stop("Failed to enable tournament system: Webserver failed to start");
			return;
		}

		Log.info("Registering channel " + TournamentSystemCommons.DATA_CHANNEL);
		this.getProxy().registerChannel(TournamentSystemCommons.DATA_CHANNEL);
		Log.info("Registering channel " + TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);
		this.getProxy().registerChannel(TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);

		if (!LCS.connectivityCheck()) {
			Log.fatal("Could not connect to the license servers. Please join our discord server https://discord.gg/4gZSVJ7 and open a ticket about this and we will try to resolve it asap");
			ProxyServer.getInstance().stop("Could not connect to the license servers");
			return;
		}

		if (!LCS.connectivityCheck()) {
			Log.fatal("Cant connect to the license servers");
			ProxyServer.getInstance().stop("Cant connect to the license servers");
			return;
		}

		try {
			File licenseFile = new File(globalConfigPath + File.separator + "license_key.txt");
			boolean success = LCS.check(licenseFile);
			if (!success) {
				if (!LCS.isValid()) {
					Log.error("Missing or invalid liscense key");
				} else if (LCS.isExpired()) {
					Log.error("Expired liscense key");
				}
			} else {
				Log.info("Licensed to: " + LCS.getLicensedTo());
			}
		} catch (Exception e) {
			Log.fatal("License validation failure");
			ProxyServer.getInstance().stop("License validation failure");
		}
	}

	@Override
	public void onDisable() {
		if (webServer != null) {
			webServer.stop();
		}
	}
}