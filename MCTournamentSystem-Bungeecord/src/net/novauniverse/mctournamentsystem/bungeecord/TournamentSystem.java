package net.novauniverse.mctournamentsystem.bungeecord;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.novauniverse.mctournamentsystem.bungeecord.api.WebServer;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUserStore;
import net.novauniverse.mctournamentsystem.bungeecord.listener.JoinEvents;
import net.novauniverse.mctournamentsystem.bungeecord.listener.TSPluginMessageListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.WhitelistListener;
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

	public static TournamentSystem getInstance() {
		return instance;
	}

	public boolean isWebserverDevelopmentMode() {
		return webserverDevelopmentMode;
	}

	public List<String> getStaffRoles() {
		return staffRoles;
	}

	@Override
	public void onLoad() {
		TournamentSystem.instance = this;
		staffRoles = new ArrayList<>();
	}

	@Override
	public void onEnable() {
		saveDefaultConfiguration();

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

		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new TSPluginMessageListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinEvents());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new WhitelistListener());

		File wwwAppFile = new File(getDataFolder().getPath() + File.separator + "www_app");

		try {
			FileUtils.forceMkdir(wwwAppFile);
		} catch (Exception e) {
			e.printStackTrace();
		}

		Log.info("Setting up web server");

		JSONObject webConfig = config.getJSONObject("web_ui");
		JSONArray webUsers = webConfig.getJSONArray("users");

		if (webUsers.length() == 0) {
			Log.warn("TournamentSystem", "No users defined for web server in " + configFile.getAbsolutePath() + ". The web ui might not work");
		}

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
	}

	@Override
	public void onDisable() {
		if (webServer != null) {
			webServer.stop();
		}
	}
}