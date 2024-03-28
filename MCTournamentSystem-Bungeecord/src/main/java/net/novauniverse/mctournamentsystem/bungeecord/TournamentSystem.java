package net.novauniverse.mctournamentsystem.bungeecord;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.KeyPair;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.event.EventPriority;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentSystemWebAPI;
import net.novauniverse.mctournamentsystem.bungeecord.authdb.AuthDB;
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
import net.novauniverse.mctournamentsystem.bungeecord.maps.MapScanner;
import net.novauniverse.mctournamentsystem.bungeecord.misc.SlowPlayerSender;
import net.novauniverse.mctournamentsystem.bungeecord.misc.WebStyleMod;
import net.novauniverse.mctournamentsystem.bungeecord.security.RSAGen;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ServerAutoRegisterData;
import net.novauniverse.mctournamentsystem.bungeecord.setup.Setup;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.config.InternetCafeOptions;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfig;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfigManager;
import net.novauniverse.mctournamentsystem.commons.team.TeamOverrides;
import net.novauniverse.mctournamentsystem.commons.utils.LinuxUtils;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;
import net.zeeraa.novacore.bungeecord.novaplugin.NovaPlugin;
import net.zeeraa.novacore.commons.api.novauniverse.NovaUniverseAPI;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.platform.OSPlatform;
import net.zeeraa.novacore.commons.platform.OSPlatformUtils;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.commons.utils.network.api.ip.IPFetcher;

public class TournamentSystem extends NovaPlugin implements Listener {
	private static TournamentSystem instance;

	private TournamentSystemWebAPI webServer;
	private List<String> staffRoles;
	private List<String> quickMessages;
	private int teamSize;
	private ChatListener chatListener;
	private String phpmyadminURL;
	private String distroName;
	private boolean openMode;
	private File globalConfigFolder;
	private PlayerTelementryManager playerTelementryManager;
	private SlowPlayerSender slowPlayerSender;
	private String publicIp;
	private String dynamicConfigURL;
	private InternetCafeOptions internetCafeOptions;
	private List<ManagedServer> managedServers;
	private File serverLogFolder;
	private boolean disableParentPidMonitoring;
	private PingListeners pingListeners;
	private String mojangAPIProxyURL;
	private boolean makeMeSufferEasteregg;
	private String motd;
	private boolean autoAppendAikarFlags;
	private List<String> globalCustomLaunchFlags;
	private boolean offlineMode;
	private boolean logWebServerExceptions;
	private String chatFilterURL;
	private String skinRenderAPIUrl;
	private File mapDataFolder;
	private KeyPair tokenKeyPair;
	private AuthDB authDB;
	private List<WebStyleMod> cssMods;
	private boolean showDeveloperCredits;

	public List<String> getGlobalCustomLaunchFlags() {
		return globalCustomLaunchFlags;
	}

	public String getSkinRenderAPIUrl() {
		return skinRenderAPIUrl;
	}

	public boolean isAutoAppendAikarFlags() {
		return autoAppendAikarFlags;
	}

	public void setAutoAppendAikarFlags(boolean autoAppendAikarFlags) {
		this.autoAppendAikarFlags = autoAppendAikarFlags;
	}

	public boolean isMakeMeSufferEasteregg() {
		return makeMeSufferEasteregg;
	}

	public TournamentSystemWebAPI getWebServer() {
		return webServer;
	}

	public File getServerLogFolder() {
		return serverLogFolder;
	}

	public List<ManagedServer> getManagedServers() {
		return managedServers;
	}

	public String getDynamicConfigUrl() {
		return dynamicConfigURL;
	}

	public String getMojangAPIProxyURL() {
		return mojangAPIProxyURL;
	}

	public AuthDB getAuthDB() {
		return authDB;
	}

	public List<WebStyleMod> getCssMods() {
		return cssMods;
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

	public String getChatFilterURL() {
		return chatFilterURL;
	}

	public static final String formatMOTD(String motd) {
		return motd.replaceAll("\\n", "\n").replaceAll("\\\\n", "\n");
	}

	public static TournamentSystem getInstance() {
		return instance;
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

	public String getMotd() {
		return motd;
	}

	public boolean isOfflineMode() {
		return offlineMode;
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

	public boolean isLogWebServerExceptions() {
		return logWebServerExceptions;
	}

	public File getMapDataFolder() {
		return mapDataFolder;
	}

	public KeyPair getTokenKeyPair() {
		return tokenKeyPair;
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onEnable() {
		TournamentSystem.instance = this;
		staffRoles = new ArrayList<>();
		openMode = false;
		distroName = null;
		managedServers = new ArrayList<>();
		globalCustomLaunchFlags = new ArrayList<>();
		makeMeSufferEasteregg = false;
		logWebServerExceptions = false;
		chatFilterURL = null;
		skinRenderAPIUrl = "https://skinrender.novauniverse.net";
		cssMods = new ArrayList<>();

		publicIp = "Unknown";
		motd = "Tournament System";

		offlineMode = !ProxyServer.getInstance().getConfig().isOnlineMode();

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

		quickMessages = new ArrayList<>();

		String globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();
		globalConfigFolder = new File(globalConfigPath);

		serverLogFolder = new File(globalConfigPath + File.separator + "Logs");

		TeamOverrides.readOverrides(globalConfigFolder);

		String configFileOverride = null;
		String logFolderOverride = null;
		String webConfigOverride = null;
		String mapDataFolderOverride = null;
		String authDBOverride = null;
		String certOverride = null;

		showDeveloperCredits = new File(getDataFolder() + File.separator + "show_credits").exists();

		mapDataFolder = new File(globalConfigPath + File.separator + "map_data");

		File overridesFile = new File(globalConfigPath + File.separator + "overrides.json");
		if (overridesFile.exists()) {
			try {
				JSONObject overrides = JSONFileUtils.readJSONObjectFromFile(overridesFile);
				configFileOverride = overrides.optString("config_file");
				webConfigOverride = overrides.optString("web_config_file");
				logFolderOverride = overrides.optString("server_log_directory");
				mapDataFolderOverride = overrides.optString("map_files");
				authDBOverride = overrides.optString("authdb_file");
				certOverride = overrides.optString("cert_file");

				if (logFolderOverride != null) {
					serverLogFolder = new File(logFolderOverride);
				}

				if (mapDataFolderOverride != null) {
					mapDataFolder = new File(mapDataFolderOverride);
				}
			} catch (Exception e) {
				Log.error("TournamentSystem", "Failed to read overrides.json. " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				ProxyServer.getInstance().stop("Failed to enable tournament system: Failed to read overrides.json");
				return;
			}
		}

		File authDBFile = new File(authDBOverride == null ? globalConfigFolder.getAbsolutePath() + File.separator + "auth_db.json" : authDBOverride);
		try {
			this.authDB = new AuthDB(authDBFile);
		} catch (Exception e) {
			e.printStackTrace();
			Log.fatal("TournamentSystem", "Failed to set up AuthDB. " + e.getClass().getName() + " " + e.getMessage());
			ProxyServer.getInstance().stop("Failed to setup AuthDB");
			return;
		}

		File keyFile = new File(certOverride == null ? globalConfigFolder.getAbsolutePath() + File.separator + "keypair.ser" : certOverride);
		if (!keyFile.exists()) {
			Log.info("TournamentSystem", "Key pair not found at " + keyFile.getAbsolutePath() + ". Generating a new one...");
			try {
				KeyPair pair = RSAGen.generateRSAKeyPair();

				FileOutputStream fileOut = new FileOutputStream(keyFile.getAbsolutePath());
				ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
				objectOut.writeObject(pair);
				objectOut.close();
				fileOut.close();

				this.tokenKeyPair = pair;

				Log.info("TournamentSystem", "New key pair generated");
			} catch (Exception e) {
				e.printStackTrace();
				Log.fatal("TournamentSystem", "Failed to save new key pair. " + e.getClass().getName() + " " + e.getMessage());
				ProxyServer.getInstance().stop("Failed to setup token key pair");
				return;
			}
		} else {
			try {
				Log.info("TournamentSystem", "Loading key pair from " + keyFile.getAbsolutePath());
				FileInputStream fileIn = new FileInputStream(keyFile.getAbsolutePath());
				ObjectInputStream objectIn = new ObjectInputStream(fileIn);
				KeyPair loadedKeyPair = (KeyPair) objectIn.readObject();
				objectIn.close();
				fileIn.close();
				this.tokenKeyPair = loadedKeyPair;
			} catch (Exception e) {
				e.printStackTrace();
				Log.fatal("TournamentSystem", "Failed to read key pair. " + e.getClass().getName() + " " + e.getMessage());
				ProxyServer.getInstance().stop("Failed to setup token key pair");
				return;
			}
		}

		serverLogFolder.mkdirs();

		File webConfigFile = new File(webConfigOverride == null ? globalConfigPath + File.separator + "web_config.json" : webConfigOverride);
		JSONObject webConfig;
		try {
			if (!webConfigFile.exists()) {
				Log.fatal("TournamentSystem", "Web config file not found at " + webConfigFile.getAbsolutePath());
				ProxyServer.getInstance().stop("Failed to enable tournament system: No web config file found");
				return;
			}

			webConfig = JSONFileUtils.readJSONObjectFromFile(webConfigFile);
		} catch (Exception e) {
			Log.fatal("TournamentSystem", "Failed to parse the web config file at " + webConfigFile.getAbsolutePath());
			e.printStackTrace();
			ProxyServer.getInstance().stop("Failed to enable tournament system: Failed to read web config file");
			return;
		}

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

		TournamentSystemCommons.setupRabbitMQ();

		autoAppendAikarFlags = config.optBoolean("auto_append_aikar_flags", false);

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

		String mojangAPIUrl = config.optString("mojang_api", "https://mojangapi.novauniverse.net");
		this.mojangAPIProxyURL = mojangAPIUrl;
		if (mojangAPIUrl.startsWith("/")) {
			mojangAPIUrl = "http://127.0.0.1" + mojangAPIUrl;
		}
		Log.info("TournamentSystem", "Mojang api url: " + mojangAPIUrl);
		NovaUniverseAPI.setMojangAPIProxyBaseURL(mojangAPIUrl);

		DBCredentials dbCredentials = null;
		if (config.has("database")) {
			if (config.getJSONObject("database").has("mysql")) {
				JSONObject mysqlDatabaseConfig = config.getJSONObject("database").getJSONObject("mysql");

				dbCredentials = new DBCredentials(mysqlDatabaseConfig.getString("driver"), mysqlDatabaseConfig.getString("host"), mysqlDatabaseConfig.getString("username"), mysqlDatabaseConfig.getString("password"), mysqlDatabaseConfig.getString("database"));
			}
		}

		if (dbCredentials == null) {
			Log.info("TournamentSystem", "DB Credentials not provided in json config file. Trying to get credentials from ENV instead");
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

		Setup.run();
		String initialConfigURL = System.getenv("INITIAL_SETUP_URL");
		if (initialConfigURL != null) {
			if (initialConfigURL.trim().length() > 0) {
				try {
					Setup.importFromURL(initialConfigURL);
				} catch (IOException e) {
					Log.error("TournamentSystem", "An error occured while trying to load initial config from url");
					e.printStackTrace();
				}
			}
		}

		try {
			String configuredMOTD = TournamentSystemCommons.getConfigValue("motd");
			if (configuredMOTD == null) {
				String motdEnv = System.getenv("TOURNAMENT_MOTD");
				if (motdEnv != null) {
					if (motdEnv.trim().length() == 0) {
						motdEnv = null;
					}
				}

				String toUse = motdEnv == null ? "Tournament System" : motdEnv;

				TournamentSystemCommons.setConfigValue("motd", toUse);
				motd = toUse;
				Log.info("TournamentSystem", "Setting default motd in database as: " + toUse);
			} else {
				motd = configuredMOTD;
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "Failed to fetch MTOD");
		}

		if (config.has("global_custom_launch_flags")) {
			JSONArray flags = config.getJSONArray("global_custom_launch_flags");
			for (int i = 0; i < flags.length(); i++) {
				globalCustomLaunchFlags.add(flags.getString(i));
			}
			Log.info("TournamentSystem", globalCustomLaunchFlags.size() + " global custom launch flags will be used");
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

		if (config.has("chat_filter")) {
			JSONObject cfc = config.getJSONObject("chat_filter");
			if (cfc.optBoolean("enabled", false)) {
				chatFilterURL = cfc.optString("url", mojangAPIUrl);
			}
		}

		Log.info("Setting up web server");

		JSONObject webUISettings = config.getJSONObject("web_ui");
		JSONArray managedServersJSON = config.optJSONArray("servers");
		JSONArray cssMods = webConfig.optJSONArray("css_mods");

		if (cssMods != null) {
			for (int i = 0; i < cssMods.length(); i++) {
				JSONObject cssMod = cssMods.getJSONObject(i);

				String name = cssMod.getString("name");
				String css = cssMod.getString("css");

				this.cssMods.add(new WebStyleMod(name, css));
			}
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
			int port = webUISettings.getInt("port");

			Log.info("Starting web server on port " + port);
			webServer = new TournamentSystemWebAPI(port, wwwAppFile);
			Log.success("Web server started");
		} catch (Exception e) {
			Log.fatal("Failed to start web server");
			e.printStackTrace();
			ProxyServer.getInstance().stop("Failed to enable tournament system: Webserver failed to start");
			return;
		}

		if (config.has("skin_render_api_url")) {
			skinRenderAPIUrl = config.getString("skin_render_api_url");
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
			try {
				server.start();
			} catch (Exception e) {
			}
		});

		logWebServerExceptions = webConfig.optBoolean("log_exceptions", false);
		if (logWebServerExceptions) {
			Log.info("TournamentSystem", "Exceptions in web server will be logged");
		}

		makeMeSufferEasteregg = webConfig.optBoolean("hey_what_if_we_made_the_logs_way_worse_to_read_like_for_real_give_me_brain_damage_pls", false);
		if (makeMeSufferEasteregg) {
			Log.info(TextUtils.englishToUWU("Hello World"));
		}

		MapScanner.fixMapUUID();
		MapScanner.updateMapDatabase();
	}

	@Override
	public void onDisable() {
		if (TournamentSystemCommons.hasRabbitMQManager()) {
			TournamentSystemCommons.getRabbitMQManager().close();
		}

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

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onPostLogin(PostLoginEvent e) {
		if (showDeveloperCredits) {
			ProxyServer.getInstance().getScheduler().schedule(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					TextComponent disclaimerText = new TextComponent("Tournament system developed by NovaUniverse. Check out our discord server at https://novauniverse.net");
					disclaimerText.setColor(ChatColor.GREEN);
					e.getPlayer().sendMessage(disclaimerText);
				}
			}, 500, TimeUnit.MILLISECONDS);
		}
	}
}