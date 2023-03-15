package net.novauniverse.mctournamentsystem.bungeecord;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.novauniverse.mctournamentsystem.bungeecord.api.WebServer;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIKeyStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKey;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.commands.sendhere.SendHereCommand;
import net.novauniverse.mctournamentsystem.bungeecord.commands.timeout.TimeoutCommand;
import net.novauniverse.mctournamentsystem.bungeecord.listener.JoinEvents;
import net.novauniverse.mctournamentsystem.bungeecord.listener.OpenModeListeners;
import net.novauniverse.mctournamentsystem.bungeecord.listener.chat.ChatListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.ping.PingListeners;
import net.novauniverse.mctournamentsystem.bungeecord.listener.playertelementry.PlayerTelementryManager;
import net.novauniverse.mctournamentsystem.bungeecord.listener.pluginmessages.TSPluginMessageListener;
import net.novauniverse.mctournamentsystem.bungeecord.listener.security.Log4JRCEFix;
import net.novauniverse.mctournamentsystem.bungeecord.listener.whitelist.WhitelistListener;
import net.novauniverse.mctournamentsystem.bungeecord.misc.CustomTheme;
import net.novauniverse.mctournamentsystem.bungeecord.misc.SlowPlayerSender;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ServerAutoRegisterData;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.config.InternetCafeOptions;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfig;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfigManager;
import net.novauniverse.mctournamentsystem.commons.socketapi.SocketAPIUtil;
import net.novauniverse.mctournamentsystem.commons.team.TeamOverrides;
import net.novauniverse.mctournamentsystem.commons.utils.LinuxUtils;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.platform.OSPlatform;
import net.zeeraa.novacore.commons.platform.OSPlatformUtils;
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

	private CommentatorAuth commentatorGuestKey;

	private String phpmyadminURL;

	private String distroName;

	private boolean openMode;

	private File globalConfigFolder;

	private PlayerTelementryManager playerTelementryManager;

	private SlowPlayerSender slowPlayerSender;

	private String publicIp;

	private String dynamicConfigURL;

	private InternetCafeOptions internetCafeOptions;

	private List<APIUser> apiUsers;

	private List<ManagedServer> managedServers;

	private File serverLogFolder;

	private boolean disableParentPidMonitoring;

	private Map<String, CustomTheme> customAdminUIThemes;

	private PingListeners pingListeners;

	public WebServer getWebServer() {
		return webServer;
	}

	public Map<String, CustomTheme> getCustomAdminUIThemes() {
		return customAdminUIThemes;
	}

	public File getServerLogFolder() {
		return serverLogFolder;
	}

	public List<ManagedServer> getManagedServers() {
		return managedServers;
	}

	public List<APIUser> getApiUsers() {
		return apiUsers;
	}

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

	public static final String formatMOTD(String motd) {
		return motd.replaceAll("\\n", "\n").replaceAll("\\\\n", "\n");
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

	public CommentatorAuth getCommentatorGuestKey() {
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

	public InternetCafeOptions getInternetCafeOptions() {
		return internetCafeOptions;
	}

	public boolean isDisableParentPidMonitoring() {
		return disableParentPidMonitoring;
	}

	public String motd;

	public String getMotd() {
		return motd;
	}

	public void setMotd(String motd) {
		try {
			TournamentSystemCommons.setConfigValue("motd", motd);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "Failed to save MOTD");
		}
		this.motd = motd;
	}

	@Override
	public void onEnable() {
		TournamentSystem.instance = this;
		staffRoles = new ArrayList<>();
		openMode = false;
		distroName = null;
		apiUsers = new ArrayList<>();
		managedServers = new ArrayList<>();

		publicIp = "Unknown";
		motd = "Tournament System";

		customAdminUIThemes = new HashMap<>();

		try {
			int pid = ProcessUtils.getOwnPID();
			Log.info("TournamentSystem", "Own pid: " + pid);
		} catch (Exception e) {
			Log.error("TournamentSystem", "Failed to fetch own PID. " + e.getClass().getName() + " " + e.getMessage());
		}

		// Init session id
		TournamentSystemCommons.getSessionId();

		saveDefaultConfiguration();

		chatListener = new ChatListener();

		commentatorGuestKey = new CommentatorAuth(UUID.randomUUID().toString(), null, "guest");

		quickMessages = new ArrayList<>();

		String globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();
		globalConfigFolder = new File(globalConfigPath);

		serverLogFolder = new File(globalConfigPath + File.separator + "Logs");

		TeamOverrides.readOverrides(globalConfigFolder);

		String configFileOverride = null;
		String logFolderOverride = null;

		File overridesFile = new File(globalConfigPath + File.separator + "overrides.json");
		if (overridesFile.exists()) {
			try {
				JSONObject overrides = JSONFileUtils.readJSONObjectFromFile(overridesFile);
				configFileOverride = overrides.optString("config_file");
				logFolderOverride = overrides.optString("server_log_directory");

				if (logFolderOverride != null) {
					serverLogFolder = new File(logFolderOverride);
				}
			} catch (Exception e) {
				Log.error("TournamentSystem", "Failed to read overrides.json. " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				ProxyServer.getInstance().stop("Failed to enable tournament system: Failed to read overrides.json");
				return;
			}
		}

		serverLogFolder.mkdirs();

		File configFile = new File(configFileOverride == null ? globalConfigPath + File.separator + "tournamentconfig.json" : configFileOverride);
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

		TournamentSystemCommons.setTournamentSystemConfigData(config);

		SocketAPIUtil.setupSocketAPI();

		disableParentPidMonitoring = false;
		if (config.has("disable_parent_pid_monitoring")) {
			disableParentPidMonitoring = config.getBoolean("disable_parent_pid_monitoring");
		}

		this.phpmyadminURL = config.getString("phpmyadmin_url");
		this.teamSize = config.getInt("team_size");

		if (config.has("dynamic_config_url")) {
			dynamicConfigURL = config.getString("dynamic_config_url");
		}

		JSONArray staffRolesJSON = config.getJSONArray("staff_roles");
		for (int i = 0; i < staffRolesJSON.length(); i++) {
			staffRoles.add(staffRolesJSON.getString(i));
		}

		DBCredentials dbCredentials = null;
		if (config.has("database")) {
			if (config.has("mysql")) {
				JSONObject mysqlDatabaseConfig = config.getJSONObject("database").getJSONObject("mysql");

				dbCredentials = new DBCredentials(mysqlDatabaseConfig.getString("driver"), mysqlDatabaseConfig.getString("host"), mysqlDatabaseConfig.getString("username"), mysqlDatabaseConfig.getString("password"), mysqlDatabaseConfig.getString("database"));
			}
		}

		if (dbCredentials == null) {
			Log.info("TournamentSystem", "DB Credentials not provided in json config file. Trying o get credentials from ENV instead");
			dbCredentials = TournamentSystemCommons.tryReadCredentialsFromENV();
		}

		if (dbCredentials == null) {
			Log.fatal("TournamentSystem", "Could not find any database credentials. Either provide them in the tournmanent config file or use env variables to set them");
			ProxyServer.getInstance().stop("Failed to fetch database credentials");
			return;
		}

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

		try {
			String configuredMOTD = TournamentSystemCommons.getConfigValue("motd");
			if (configuredMOTD == null) {
				TournamentSystemCommons.setConfigValue("motd", "Tournament System");
				Log.info("TournamentSystem", "Setting default motd in database");
			} else {
				motd = configuredMOTD;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "Failed to fetch MTOD");
		}

		/* ----- Listeners ----- */
		playerTelementryManager = new PlayerTelementryManager();
		slowPlayerSender = new SlowPlayerSender(this);
		pingListeners = new PingListeners(this);

		Log.info("Registering listeners");
		ProxyServer.getInstance().getPluginManager().registerListener(this, this);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new TSPluginMessageListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, slowPlayerSender);
		ProxyServer.getInstance().getPluginManager().registerListener(this, playerTelementryManager);
		ProxyServer.getInstance().getPluginManager().registerListener(this, new JoinEvents());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new WhitelistListener());
		ProxyServer.getInstance().getPluginManager().registerListener(this, new Log4JRCEFix());
		ProxyServer.getInstance().getPluginManager().registerListener(this, chatListener);
		ProxyServer.getInstance().getPluginManager().registerListener(this, pingListeners);

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
		JSONArray commentatorKeys = config.optJSONArray("commentator_keys");
		JSONArray apiKeys = webConfig.optJSONArray("api_keys");
		JSONArray webUsers = webConfig.getJSONArray("users");
		JSONArray themes = webConfig.optJSONArray("custom_themes");
		JSONArray managedServersJSON = config.optJSONArray("servers");

		if (webUsers.length() == 0) {
			Log.warn("TournamentSystem", "No users defined for web server in " + configFile.getAbsolutePath() + ". The web ui wont be accessible unless you are in dev mode (and thats not a good idea for prod env)");
		}

		if (themes != null) {
			for (int i = 0; i < themes.length(); i++) {
				JSONObject theme = themes.getJSONObject(i);
				String name = theme.getString("name");
				String url = theme.getString("url");
				String baseTheme = theme.optString("base_theme");

				JSONObject serverConsoleTheme = theme.optJSONObject("server_console_theme");

				customAdminUIThemes.put(name, new CustomTheme(name, url, baseTheme, serverConsoleTheme));
			}
		}

		if (commentatorKeys != null) {
			for (int i = 0; i < commentatorKeys.length(); i++) {
				JSONObject commentatorKey = commentatorKeys.getJSONObject(i);

				String key = commentatorKey.getString("key");
				String uuidString = commentatorKey.getString("uuid");
				String identifier = commentatorKey.getString("identifier");
				UUID uuid = null;

				try {
					uuid = UUID.fromString(uuidString);
				} catch (IllegalArgumentException e) {
					Log.error("TournamentSystem", "Invalid UUID " + uuidString + " for commentator key with identifier " + identifier);
					continue;
				}

				APIKeyStore.addCommentatorKey(new CommentatorAuth(key, uuid, identifier));
			}
		}
		for (int i = 0; i < webUsers.length(); i++) {
			JSONObject user = webUsers.getJSONObject(i);

			boolean hidePlayerIPs = user.optBoolean("hide_player_ips", false);

			String username = user.getString("username");
			String password = user.getString("password");
			List<UserPermission> permissions = new ArrayList<UserPermission>();

			JSONArray permissionStrings = user.getJSONArray("permissions");
			for (int j = 0; j < permissionStrings.length(); j++) {
				String permissionName = permissionStrings.getString(j);
				try {
					permissions.add(UserPermission.valueOf(permissionName));
				} catch (Exception e) {
					Log.error("TournamentSystem", "Invalid permission " + permissionName + " for user " + username);
				}
			}

			apiUsers.add(new APIUser(username, password, permissions, hidePlayerIPs));

			Log.info("TournamentSystem", "Added user " + username + " to the web ui users. (hide ip: " + hidePlayerIPs + ")");
		}

		Log.info("TournamentSystem", apiUsers.size() + " user" + (apiUsers.size() == 1 ? "" : "s") + " configured for web ui");

		if (apiKeys != null) {
			for (int i = 0; i < apiKeys.length(); i++) {
				JSONObject apiKey = apiKeys.getJSONObject(i);
				String key = apiKey.getString("key");
				String userName = apiKey.getString("user");

				APIUser user = apiUsers.stream().filter(u -> u.getUsername().equals(userName)).findFirst().orElse(null);
				if (user == null) {
					Log.error("TournamentSystem", "Invalid user " + userName + " configured for api key");
				} else {
					APIKeyStore.addApiKey(new APIKey(key, user));
				}
			}
			Log.info("TournamentSystem", APIKeyStore.getApiKeys().size() + " api keys loaded");
		}

		if (managedServersJSON != null) {
			for (int i = 0; i < managedServersJSON.length(); i++) {
				JSONObject serverData = managedServersJSON.getJSONObject(i);

				String name = serverData.getString("name").trim();

				if (name.length() == 0) {
					Log.error("TournamentSystem", "Invalid empty server name in tournamentconfig.json");
					continue;
				}

				if (managedServers.stream().anyMatch(s -> s.getName().equals(name))) {
					Log.error("TournamentSystem", "Duplicate server name " + name + " for server in tournamentconfig.json");
					continue;
				}

				ServerAutoRegisterData serverAutoRegisterData = null;

				if (serverData.has("auto_register")) {
					JSONObject autoRegisterData = serverData.getJSONObject("auto_register");

					boolean enabled = true;
					if (autoRegisterData.has("enabled")) {
						enabled = autoRegisterData.getBoolean("enabled");
					}

					String host = "127.0.0.1";
					if (autoRegisterData.has("host")) {
						host = autoRegisterData.getString("host");
					}

					int port = autoRegisterData.getInt("port");

					serverAutoRegisterData = new ServerAutoRegisterData(enabled, host, port);
				}

				File workingDirectory = new File(serverData.getString("working_directory"));

				String javaExecutable = serverData.getString("java_executable");
				String jvmArguments = serverData.getString("jvm_arguments");
				String jar = serverData.getString("jar");

				boolean passName = serverData.optBoolean("pass_server_name", true);

				if (!workingDirectory.exists()) {
					Log.error("TournamentSystem", "Cant find working directory " + workingDirectory.getAbsolutePath() + " for server " + name + " in tournamentconfig.json");
					continue;
				}

				boolean autoStart = false;
				if (serverData.has("auto_start")) {
					autoStart = serverData.getBoolean("auto_start");
				}

				managedServers.add(new ManagedServer(name, javaExecutable, jvmArguments, jar, workingDirectory, autoStart, serverAutoRegisterData, passName));
			}
			Log.info("TournamentSystem", managedServers.size() + " servers configured to auto start");
		}

		try {
			int port = webConfig.getInt("port");
			webserverDevelopmentMode = webConfig.getBoolean("development_mode");

			if (webserverDevelopmentMode) {
				Log.warn("TournamentSystem", "Development mode enabled for web server. No autentication will be required to access the web ui and api");
			}

			Log.info("Starting web server on port " + port);
			webServer = new WebServer(port, wwwAppFile);
			Log.success("Web server started");
		} catch (Exception e) {
			Log.fatal("Failed to start web server");
			e.printStackTrace();
			ProxyServer.getInstance().stop("Failed to enable tournament system: Webserver failed to start");
			return;
		}

		if (config.has("internet_cafe_settings")) {
			JSONObject internetCafeSettingsJSON = config.getJSONObject("internet_cafe_settings");

			String ggRock = null;

			if (internetCafeSettingsJSON.has("ggrock_url")) {
				ggRock = internetCafeSettingsJSON.getString("ggrock_url").trim();
				if (ggRock.length() == 0) {
					ggRock = null;
				}
			}

			internetCafeOptions = new InternetCafeOptions(ggRock);
		} else {
			internetCafeOptions = new InternetCafeOptions();
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

		distroName = null;

		if (OSPlatformUtils.getPlatform() == OSPlatform.LINUX) {
			try {
				Log.info("TournamentSystem", "Fetching linux distro name");
				distroName = LinuxUtils.getLinuxDistroPrettyName();
			} catch (Exception e) {
				Log.error("TournamentSystem", "Failed to fetch linux distro name. " + e.getClass().getName() + " " + e.getMessage());
			}
		}
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

		managedServers.stream().filter(ManagedServer::isServerAutoRegisterEnabled).forEach(ManagedServer::register);

		List<ManagedServer> toAutoStart = managedServers.stream().filter(ManagedServer::isAutoStart).collect(Collectors.toList());
		Log.info("TournamentSystem", toAutoStart.size() + " servers configured to auto start");
		toAutoStart.forEach(server -> {
			Log.info("TournamentSystem", "Attempting to auto start server " + server.getName());
			server.start();
		});
	}

	@Override
	public void onDisable() {
		SocketAPIUtil.shutdown();

		managedServers.stream().filter(ManagedServer::isRunning).forEach(ManagedServer::stop);
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		slowPlayerSender.destroy();

		if (webServer != null) {
			webServer.stop();
		}

		this.getProxy().unregisterChannel(TournamentSystemCommons.DATA_CHANNEL);
		this.getProxy().unregisterChannel(TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);

		ProxyServer.getInstance().getScheduler().cancel(this);
	}
}