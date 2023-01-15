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
import net.novauniverse.mctournamentsystem.bungeecord.commands.timeout.TimeoutCommand;
import net.novauniverse.mctournamentsystem.bungeecord.listener.JoinEvents;
import net.novauniverse.mctournamentsystem.bungeecord.listener.OpenModeListeners;
import net.novauniverse.mctournamentsystem.bungeecord.listener.chat.ChatListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.playertelementry.PlayerTelementryManager;
import net.novauniverse.mctournamentsystem.bungeecord.listener.pluginmessages.TSPluginMessageListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.security.Log4JRCEFix;
import net.novauniverse.mctournamentsystem.bungeecord.listener.whitelist.WhitelistListener;
import net.novauniverse.mctournamentsystem.bungeecord.misc.SlowPlayerSender;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfig;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfigManager;
import net.novauniverse.mctournamentsystem.commons.team.TeamOverrides;
import net.novauniverse.mctournamentsystem.commons.utils.LinuxUtils;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.commons.utils.network.api.ip.IPFetcher;

public class TournamentSystem extends NovaPlugin implements Listener {
	private static TournamentSystem instance;

	private WebServer webServer;
	private boolean webserverDevelopmentMode;
	private List<String> staffRoles;
	private List<String> quickMessages;
	private int teamSize;

	private ChatListener chatListener;

	private String commentatorGuestKey;

	private String phpmyadminURL;

	private String distroName;

	private boolean openMode;

	private File globalConfigFolder;

	private PlayerTelementryManager playerTelementryManager;

	private SlowPlayerSender slowPlayerSender;

	private String publicIp;

	private String dynamicConfigURL;

	public String getDynamicConfigUrl() {
		return dynamicConfigURL;
	}

	public void reloadDynamicConfig() throws Exception {
		DynamicConfig config = DynamicConfigManager.getDynamicConfig(dynamicConfigURL);

		TeamOverrides.colorOverrides.clear();
		TeamOverrides.nameOverrides.clear();

		config.getTeamColors().forEach((team, colorName) -> {
			@SuppressWarnings("deprecation") // Should still be fine
			ChatColor color = ChatColor.valueOf(colorName);
			TeamOverrides.colorOverrides.put(team, color);
		});

		config.getTeamNames().forEach((team, name) -> {
			TeamOverrides.nameOverrides.put(team, name);
		});

		config.getTeamBadges().forEach((team, badgeName) -> {
			TeamOverrides.badges.put(team, badgeName);
		});
	}

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

	public boolean isOpenMode() {
		return openMode;
	}

	public String getCommentatorGuestKey() {
		return commentatorGuestKey;
	}

	public String getDistroName() {
		return distroName;
	}

	public File getGlobalConfigFolder() {
		return globalConfigFolder;
	}

	public SlowPlayerSender getSlowPlayerSender() {
		return slowPlayerSender;
	}

	public String getPublicIp() {
		return publicIp;
	}

	@Override
	public void onEnable() {
		TournamentSystem.instance = this;
		staffRoles = new ArrayList<>();
		openMode = false;
		distroName = null;

		publicIp = "Unknown";

		// Init session id
		TournamentSystemCommons.getSessionId();

		saveDefaultConfiguration();

		chatListener = new ChatListener();

		commentatorGuestKey = UUID.randomUUID().toString();

		quickMessages = new ArrayList<>();

		String globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();
		globalConfigFolder = new File(globalConfigPath);

		TeamOverrides.readOverrides(globalConfigFolder);

		File configFile = new File(globalConfigPath + File.separator + "tournamentconfig.json");
		JSONObject config;
		try {
			if (!configFile.exists()) {
				Log.fatal("TournamentSystem", "Config file not found at " + configFile.getAbsolutePath() + ". Start the lobby once to generate a default config file");
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

		TournamentSystemCommons.setTournamentSystemConfigData(config);

		this.phpmyadminURL = config.getString("phpmyadmin_url");
		this.teamSize = config.getInt("team_size");

		if (config.has("dynamic_config_url")) {
			dynamicConfigURL = config.getString("dynamic_config_url");
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

		/* ----- Listeners ----- */

		playerTelementryManager = new PlayerTelementryManager();
		slowPlayerSender = new SlowPlayerSender(this);

		Log.info("Registering listeners");
		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new TSPluginMessageListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, slowPlayerSender);
		ProxyServer.getInstance().getPluginManager().registerListener(this, playerTelementryManager);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinEvents());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new WhitelistListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new Log4JRCEFix());
		ProxyServer.getInstance().getPluginManager().registerListener(this, chatListener);

		/* ----- Commands ----- */
		Log.info("Registering commands");
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new SendHereCommand());
		ProxyServer.getInstance().getPluginManager().registerCommand(this, new TimeoutCommand());

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

		if (config.has("open_mode")) {
			openMode = config.getBoolean("open_mode");
			if (openMode) {
				Log.info("TournamentSystem", "Open mode enabled");
				ProxyServer.getInstance().getPluginManager().registerListener(this, new OpenModeListeners());
			}
		}

		Log.info("Setting up web server");

		JSONObject webConfig = config.getJSONObject("web_ui");
		JSONObject commentatorKeys = config.getJSONObject("commentator_keys");
		JSONArray apiKeys = webConfig.getJSONArray("api_keys");
		JSONArray webUsers = webConfig.getJSONArray("users");

		if (webUsers.length() == 0) {
			Log.warn("TournamentSystem", "No users defined for web server in " + configFile.getAbsolutePath() + ". The web ui wont be accessible unless you are in dev mode (and thats not a good idea for prod env)");
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

		IPFetcher.getIPAsync((ip, err) -> {
			if (err != null) {
				Log.error("TournamentSystem", "Failed to fetch public ip. " + err.getClass().getName() + " " + err.getMessage());
				return;
			}

			Log.info("TournamentSystem", "Public ip is: " + ip);
			publicIp = ip;
		});

		Log.info("Registering channel " + TournamentSystemCommons.DATA_CHANNEL);
		this.getProxy().registerChannel(TournamentSystemCommons.DATA_CHANNEL);
		Log.info("Registering channel " + TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);
		this.getProxy().registerChannel(TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);

		distroName = LinuxUtils.getLinuxDistroPrettyName();
		if (distroName != null) {
			Log.info("TournamentSystem", "Seems like we are running on " + distroName);
		}

		if (dynamicConfigURL != null) {
			Log.info("TournamentSystem", "Trying to read dynamic config...");
			try {
				reloadDynamicConfig();
				Log.success("TournamentSystem", "Dynamic config loaded");
			} catch (Exception e) {
				e.printStackTrace();
				Log.error("TournamentSystem", "Failed to update dynamic config. " + e.getClass().getName() + " " + e.getMessage());
			}
		}
	}

	@Override
	public void onDisable() {
		slowPlayerSender.destroy();

		if (webServer != null) {
			webServer.stop();
		}

		this.getProxy().unregisterChannel(TournamentSystemCommons.DATA_CHANNEL);
		this.getProxy().unregisterChannel(TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);

		ProxyServer.getInstance().getScheduler().cancel(this);
	}
}