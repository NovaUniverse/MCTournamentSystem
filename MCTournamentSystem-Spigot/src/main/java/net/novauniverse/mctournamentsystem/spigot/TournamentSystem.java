package net.novauniverse.mctournamentsystem.spigot;

import java.io.File;
import java.io.OutputStream;
import java.lang.management.ManagementFactory;
import java.lang.reflect.InvocationTargetException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.bstats.bukkit.Metrics;
import org.bstats.charts.SimplePie;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import com.xxmicloxx.NoteBlockAPI.model.Song;
import com.xxmicloxx.NoteBlockAPI.utils.NBSDecoder;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.api.TournamentSystemAPI;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfig;
import net.novauniverse.mctournamentsystem.commons.dynamicconfig.DynamicConfigManager;
import net.novauniverse.mctournamentsystem.commons.rabbitmq.RabbitMQStrings;
import net.novauniverse.mctournamentsystem.commons.team.TeamOverrides;
import net.novauniverse.mctournamentsystem.commons.utils.ResourceUtils;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;
import net.novauniverse.mctournamentsystem.spigot.command.bc.BCCommand;
import net.novauniverse.mctournamentsystem.spigot.command.chatfilter.ChatfilterCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.csp.CSPCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.ctp.CTPCommand;
import net.novauniverse.mctournamentsystem.spigot.command.copylocation.CopyLocationCommand;
import net.novauniverse.mctournamentsystem.spigot.command.database.DatabaseCommand;
import net.novauniverse.mctournamentsystem.spigot.command.enablefinalgame.EnableFinalGameCommand;
import net.novauniverse.mctournamentsystem.spigot.command.fly.FlyCommand;
import net.novauniverse.mctournamentsystem.spigot.command.halt.HaltCommand;
import net.novauniverse.mctournamentsystem.spigot.command.killstatusreporting.KillStatusReportingCommand;
import net.novauniverse.mctournamentsystem.spigot.command.managedserver.ManagedServerCommand;
import net.novauniverse.mctournamentsystem.spigot.command.purgecache.PurgeCacheCommand;
import net.novauniverse.mctournamentsystem.spigot.command.reconnect.ReconnectCommand;
import net.novauniverse.mctournamentsystem.spigot.command.reloaddynamicconfig.ReloadDynamicConfigCommand;
import net.novauniverse.mctournamentsystem.spigot.command.respawnplayer.RespawnPlayerCommand;
import net.novauniverse.mctournamentsystem.spigot.command.socials.implementation.DiscordCommand;
import net.novauniverse.mctournamentsystem.spigot.command.socials.implementation.PatreonCommand;
import net.novauniverse.mctournamentsystem.spigot.command.socials.implementation.YoutubeCommand;
import net.novauniverse.mctournamentsystem.spigot.command.yborder.YBorderCommand;
import net.novauniverse.mctournamentsystem.spigot.cosmetics.CosmeticsIntegrations;
import net.novauniverse.mctournamentsystem.spigot.debug.DebugCommands;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.ITournamentSystemPlayerEliminationMessageProvider;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.TournamentSystemDefaultPlayerEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.misc.GameEndSoundtrackManager;
import net.novauniverse.mctournamentsystem.spigot.game.util.DefaultPlayerEliminatedTitleProvider;
import net.novauniverse.mctournamentsystem.spigot.game.util.PlayerEliminatedTitleProvider;
import net.novauniverse.mctournamentsystem.spigot.labymod.LabyModIntegration;
import net.novauniverse.mctournamentsystem.spigot.misc.CustomItemTest;
import net.novauniverse.mctournamentsystem.spigot.modules.chatfilter.ChatFilter;
import net.novauniverse.mctournamentsystem.spigot.modules.ezreplacer.EZReplacer;
import net.novauniverse.mctournamentsystem.spigot.modules.head.EdibleHeads;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.modules.winnermanagement.WinnerManager;
import net.novauniverse.mctournamentsystem.spigot.permissions.TournamentPermissions;
import net.novauniverse.mctournamentsystem.spigot.placeholderapi.PlaceholderAPIExpansion;
import net.novauniverse.mctournamentsystem.spigot.pluginmessages.TSPluginMessageListnener;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreListener;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentTeamManagerSettings;
import net.novauniverse.mctournamentsystem.spigot.utils.TSItemsAdderUtils;
import net.zeeraa.novacore.commons.api.novauniverse.NovaUniverseAPI;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.platform.OSPlatform;
import net.zeeraa.novacore.commons.platform.OSPlatformUtils;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.gameengine.NovaCoreGameEngine;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.title.StaticScoreboardTitle;
import net.zeeraa.novacore.spigot.permission.PermissionRegistrator;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class TournamentSystem extends JavaPlugin implements Listener {
	public static final int STATUS_REPORTING_TIMEOUT = 10 * 1000;
	public static final int INTERNAL_API_TIMEOUT = 10 * 1000;

	private static TournamentSystem instance;

	private TournamentSystemTeamManager teamManager;
	private String serverName;
	private String lobbyServer;

	private boolean builtInScoreSystemDisabled;

	private int[] winScore;

	private boolean addXpLevelOnKill;
	private boolean useExtendedSpawnLocations;
	private boolean celebrationMode;
	private boolean replaceEz;
	private boolean noTeamsMode;
	private boolean eliminationTitleMessageEnabled;

	private List<Consumer<Player>> respawnPlayerCallbacks;

	private ScoreListener scoreListener;

	private ITournamentSystemPlayerEliminationMessageProvider playerEliminationMessageProvider;

	private TournamentSystemDefaultPlayerEliminationMessage defaultPlayerEliminationMessage;

	private Map<String, Group> staffGroups;
	private Group defaultGroup;

	private File mapDataFolder;
	private File nbsFolder;
	private File globalDataFolder;

	private boolean forceShowTeamNameInLeaderboard;
	private boolean makeTeamNamesBold;

	private String cachedTournamentName;
	private String cachedTournamentLink;

	private String globalDataDirectory;

	private String resourcePackUrl;

	private PlayerEliminatedTitleProvider playerEliminatedTitleProvider;

	private boolean showSensitiveTelementryData;

	private Song gameEndMusic;

	private Task timerSeconds;
	private Task statusReportingTask;

	private TSPluginMessageListnener pluginMessageListener;

	private boolean disableScoreboard;

	private PlaceholderAPIExpansion placeholderAPIExpansion;

	private boolean enableBehindYourTailcompass;
	private boolean behindYourTailParticles;

	private boolean useItemsAdder;

	private String dynamicConfigURL;

	private int parentProcessID;

	private boolean disableParentPidMonitoring;

	private String stateReportingToken;
	private String internalAPIAccessToken;

	private String adminUIUrl;

	private JSONObject gameSpecificScoreSettings;

	private Metrics metrics;

	private String loadedGameName;

	private boolean hideTeamNameNextToPlayerIGN;

	private String iaNoTeamIcon;

	private TournamentSystemAPI api;

	private List<String> configuredManagedServers;

	private boolean isFinalGame;

	public static TournamentSystem getInstance() {
		return instance;
	}

	public Metrics getMetrics() {
		return metrics;
	}

	public boolean isDisableParentPidMonitoring() {
		return disableParentPidMonitoring;
	}

	public JSONObject getGameSpecificScoreSettings() {
		return gameSpecificScoreSettings;
	}

	@Nullable
	public PlayerEliminatedTitleProvider getPlayerEliminatedTitleProvider() {
		return playerEliminatedTitleProvider;
	}

	public void setPlayerEliminatedTitleProvider(@Nullable PlayerEliminatedTitleProvider playerEliminatedTitleProvider) {
		this.playerEliminatedTitleProvider = playerEliminatedTitleProvider;
	}

	public TournamentSystemTeamManager getTeamManager() {
		return teamManager;
	}

	public boolean isAddXpLevelOnKill() {
		return addXpLevelOnKill;
	}

	public Map<String, Group> getStaffGroups() {
		return staffGroups;
	}

	public Group getDefaultGroup() {
		return defaultGroup;
	}

	public File getMapDataFolder() {
		return mapDataFolder;
	}

	public boolean isUseExtendedSpawnLocations() {
		return useExtendedSpawnLocations;
	}

	public boolean isCelebrationMode() {
		return celebrationMode;
	}

	public boolean isReplaceEz() {
		return replaceEz;
	}

	public ITournamentSystemPlayerEliminationMessageProvider getPlayerEliminationMessageProvider() {
		return playerEliminationMessageProvider;
	}

	public void setPlayerEliminationMessageProvider(ITournamentSystemPlayerEliminationMessageProvider playerEliminationMessageProvider) {
		this.playerEliminationMessageProvider = playerEliminationMessageProvider;
	}

	public boolean isNoTeamsMode() {
		return noTeamsMode;
	}

	public void addRespawnPlayerCallback(Consumer<Player> consumer) {
		this.respawnPlayerCallbacks.add(consumer);
	}

	public void onRespawnPlayerCommand(Player player) {
		respawnPlayerCallbacks.forEach(c -> c.accept(player));
	}

	public void setScoreListener(ScoreListener scoreListener) {
		this.scoreListener = scoreListener;
	}

	public ScoreListener getScoreListener() {
		return scoreListener;
	}

	public boolean hasScoreListener() {
		return scoreListener != null;
	}

	public String getCachedTournamentLink() {
		return cachedTournamentLink;
	}

	public String getCachedTournamentName() {
		return cachedTournamentName;
	}

	public void setBuiltInScoreSystemDisabled(boolean builtInScoreSystemDisabled) {
		this.builtInScoreSystemDisabled = builtInScoreSystemDisabled;
	}

	public boolean isBuiltInScoreSystemDisabled() {
		return builtInScoreSystemDisabled;
	}

	public boolean isForceShowTeamNameInLeaderboard() {
		return forceShowTeamNameInLeaderboard;
	}

	public TournamentSystemDefaultPlayerEliminationMessage getDefaultPlayerEliminationMessage() {
		return defaultPlayerEliminationMessage;
	}

	public boolean isEliminationTitleMessageEnabled() {
		return eliminationTitleMessageEnabled;
	}

	public void disableEliminationTitleMessage() {
		eliminationTitleMessageEnabled = false;
	}

	public boolean isMakeTeamNamesBold() {
		return makeTeamNamesBold;
	}

	public String getResourcePackUrl() {
		return resourcePackUrl;
	}

	public String getGlobalDataDirectory() {
		return globalDataDirectory;
	}

	public File getNBSFolder() {
		return nbsFolder;
	}

	public File getNBSFile(String name) {
		return new File(getNBSFolder().getAbsolutePath() + File.separator + name);
	}

	public String getServerName() {
		return serverName;
	}

	public String getLobbyServer() {
		return lobbyServer;
	}

	public int[] getWinScore() {
		return winScore;
	}

	public File getGlobalDataFolder() {
		return globalDataFolder;
	}

	public boolean isShowSensitiveTelementryData() {
		return showSensitiveTelementryData;
	}

	public Song getGameEndMusic() {
		return gameEndMusic;
	}

	public boolean isDisableScoreboard() {
		return disableScoreboard;
	}

	public void setDisableScoreboard(boolean disableScoreboard) {
		this.disableScoreboard = disableScoreboard;
	}

	public boolean isEnableBehindYourTailcompass() {
		return enableBehindYourTailcompass;
	}

	public boolean isBehindYourTailParticles() {
		return behindYourTailParticles;
	}

	public boolean isUseItemsAdder() {
		return useItemsAdder;
	}

	public void setUseItemsAdder(boolean useItemsAdder) {
		this.useItemsAdder = useItemsAdder;
	}

	public String getDynamicConfigURL() {
		return dynamicConfigURL;
	}

	public TournamentTeamManagerSettings getTeamManagerSettings() {
		return teamManager.getSettings();
	}

	public String getLoadedGameName() {
		return loadedGameName;
	}

	public void setLoadedGameName(String loadedGameName) {
		this.loadedGameName = loadedGameName;
	}

	public boolean isHideTeamNameNextToPlayerIGN() {
		return hideTeamNameNextToPlayerIGN;
	}

	public String getInternalAPIAccessToken() {
		return internalAPIAccessToken;
	}

	@Nullable
	public TournamentSystemAPI getApi() {
		return api;
	}

	public boolean hasApi() {
		return api != null;
	}

	public List<String> getConfiguredManagedServers() {
		return configuredManagedServers;
	}

	public boolean reloadDynamicConfig() {
		if (dynamicConfigURL == null) {
			return false;
		}

		try {
			DynamicConfig config = DynamicConfigManager.getDynamicConfig(dynamicConfigURL);

			TeamOverrides.colorOverrides.clear();
			TeamOverrides.nameOverrides.clear();
			TeamOverrides.badges.clear();

			config.getTeamColors().forEach((team, colorName) -> {
				ChatColor color = ChatColor.valueOf(colorName);
				TeamOverrides.colorOverrides.put(team, color);
			});

			config.getTeamNames().forEach((team, name) -> {
				TeamOverrides.nameOverrides.put(team, name);
			});

			config.getTeamBadges().forEach((team, badgeName) -> {
				TeamOverrides.badges.put(team, badgeName);
			});

			new BukkitRunnable() {
				@Override
				public void run() {
					Log.info("TournamentSystem", "Updating team badges");
					TeamManager.getTeamManager().getTeams().forEach(t -> ((TournamentSystemTeam) t).updateBadge());
				}
			}.runTaskLater(this, 5L);
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "Failed to update dynamic config. Cause: " + e.getClass().getName() + " " + e.getMessage());
		}

		return true;
	}

	public String getIANoTeamIcon() {
		return iaNoTeamIcon;
	}

	public boolean isFinalGame() {
		return isFinalGame;
	}

	@Override
	public void onEnable() {
		// Init session id
		TournamentSystemCommons.getSessionId();

		TournamentSystem.instance = this;
		this.configuredManagedServers = new ArrayList<>();
		this.staffGroups = new HashMap<>();

		this.noTeamsMode = false;

		this.respawnPlayerCallbacks = new ArrayList<>();

		this.defaultPlayerEliminationMessage = new TournamentSystemDefaultPlayerEliminationMessage();
		this.playerEliminationMessageProvider = this.getDefaultPlayerEliminationMessage();

		this.playerEliminatedTitleProvider = new DefaultPlayerEliminatedTitleProvider();

		this.scoreListener = null;

		this.builtInScoreSystemDisabled = false;
		this.eliminationTitleMessageEnabled = true;

		this.forceShowTeamNameInLeaderboard = false;
		this.makeTeamNamesBold = false;

		this.resourcePackUrl = null;

		this.showSensitiveTelementryData = false;

		this.disableScoreboard = false;

		this.placeholderAPIExpansion = null;

		this.enableBehindYourTailcompass = false;
		this.behindYourTailParticles = false;

		this.dynamicConfigURL = null;

		this.useItemsAdder = getConfig().getBoolean("enable_items_adder");

		this.loadedGameName = null;

		this.api = null;

		statusReportingTask = null;
		parentProcessID = -1;

		metrics = new Metrics(this, 17833);
		metrics.addCustomChart(new SimplePie("minigame_used", () -> {
			return loadedGameName == null ? "None" : loadedGameName;
		}));

		adminUIUrl = "http://127.0.0.1";
		String tournamentAdminUIPort = System.getProperty("tournamentAdminUIPort");
		if (tournamentAdminUIPort != null) {
			try {
				int port = Integer.parseInt(tournamentAdminUIPort);
				adminUIUrl = getAdminUIUrl() + ":" + port;
			} catch (Exception e) {
				Log.error("TournamentSystem", "Failed to parse arg -DtournamentAdminUIPort. " + e.getClass().getName() + " " + e.getMessage());
			}
		}

		/* ----- Setup files ----- */
		saveDefaultConfig();

		this.isFinalGame = getConfig().getBoolean("is_final_game");

		globalDataDirectory = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();

		this.nbsFolder = new File(TournamentSystem.getInstance().getGlobalDataDirectory() + File.separator + "nbs");

		this.globalDataFolder = new File(globalDataDirectory);
		this.mapDataFolder = new File(globalDataDirectory + File.separator + "map_data");

		TeamOverrides.readOverrides(globalDataFolder);

		String configFileOverride = null;
		String mapDataFolderOverride = null;
		String nbsFolderOverride = null;

		File overridesFile = new File(globalDataDirectory + File.separator + "overrides.json");
		if (overridesFile.exists()) {
			try {
				JSONObject overrides = JSONFileUtils.readJSONObjectFromFile(overridesFile);
				configFileOverride = overrides.optString("config_file");
				mapDataFolderOverride = overrides.optString("map_files");
				nbsFolderOverride = overrides.optString("nbs_folder");

				if (mapDataFolderOverride != null) {
					mapDataFolder = new File(mapDataFolderOverride);
				}

				if (nbsFolderOverride != null) {
					nbsFolder = new File(nbsFolderOverride);
				}
			} catch (Exception e) {
				Log.error("TournamentSystem", "Failed to read overrides.json. " + e.getClass().getName() + " " + e.getMessage());
				e.printStackTrace();
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}
		}

		this.mapDataFolder.mkdirs();
		this.nbsFolder.mkdirs();

		File configFile = new File(configFileOverride == null ? globalDataDirectory + File.separator + "tournamentconfig.json" : configFileOverride);
		JSONObject config;
		try {
			if (!configFile.exists()) {
				Log.fatal("TournamentSystem", "Config file not found at " + configFile.getAbsolutePath());
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			config = JSONFileUtils.readJSONObjectFromFile(configFile);
		} catch (Exception e) {
			Log.fatal("TournamentSystem", "Failed to parse/save the config file at " + configFile.getAbsolutePath());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		File scoreConfigFile = new File(globalDataDirectory + File.separator + "GameSpecificScoreSettings.json");
		try {
			if (!scoreConfigFile.exists()) {
				Log.info("TournamentSystem", "Creating score settings file at " + scoreConfigFile.getAbsolutePath());

				JSONObject defaultConfig = new JSONObject(ResourceUtils.readResourceFromJARAsString("/GameSpecificScoreSettings.json", this));

				JSONFileUtils.saveJson(scoreConfigFile, defaultConfig, 4);
			}
			gameSpecificScoreSettings = JSONFileUtils.readJSONObjectFromFile(scoreConfigFile);
		} catch (Exception e) {
			Log.fatal("TournamentSystem", "Failed to parse/save the GameSpecificScoreSettings.json at " + configFile.getAbsolutePath());
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		String mojangAPIUrl = config.optString("mojang_api", "https://mojangapi.novauniverse.net");
		if (mojangAPIUrl.startsWith("/")) {
			mojangAPIUrl = "http://127.0.0.1" + mojangAPIUrl;
		}
		Log.info("TournamentSystem", "Mojang api url: " + mojangAPIUrl);
		NovaUniverseAPI.setMojangAPIProxyBaseURL(mojangAPIUrl);

		TournamentSystemCommons.setTournamentSystemConfigData(config);

		TournamentSystemCommons.setupRabbitMQ();

		disableParentPidMonitoring = false;
		if (config.has("disable_parent_pid_monitoring")) {
			disableParentPidMonitoring = config.getBoolean("disable_parent_pid_monitoring");
		}

		if (!disableParentPidMonitoring) {
			if (OSPlatformUtils.getPlatform() == OSPlatform.UNKNOWN) {
				Log.fatal("TournamentSystem", "Unsupported os");
				Bukkit.getServer().shutdown();
				return;
			}

			String parentProcessIdStr = System.getProperty("tournamentServerParentProcessID");
			if (parentProcessIdStr == null) {
				Log.info("TournamentSystem", "Did not detect -DtournamentServerParentProcessID flag. We wont monitor for parent process");
			} else {
				Log.info("TournamentSystem", "Detected -DtournamentServerParentProcessID flag");
				try {
					parentProcessID = Integer.parseInt(parentProcessIdStr);
					Log.info("TournamentSystem", "Monitoring process " + parentProcessID + " for automatic shutdown");
					new BukkitRunnable() {
						@Override
						public void run() {
							if (!ProcessUtils.isProcessRunning(parentProcessID)) {
								cancel();
								Log.warn("TournamentSystem", "Parent process " + parentProcessID + " no longer found. Shutting down");
								AsyncManager.runSync(() -> {
									Bukkit.getServer().shutdown();
								});
							}
						}
					}.runTaskTimerAsynchronously(this, 20 * 5, 20 * 5);
				} catch (Exception e) {
					Log.fatal("tournamentSystem", "-DtournamentServerParentProcessID flag was set to non numeric value");
					Bukkit.getServer().shutdown();
				}
			}
		}

		// Staff permission groups
		JSONArray staffRolesJSON = config.getJSONArray("staff_roles");
		for (int i = 0; i < staffRolesJSON.length(); i++) {
			String name = staffRolesJSON.getString(i);
			Group group = LuckPermsProvider.get().getGroupManager().getGroup(name);

			if (group == null) {
				Log.error(getName(), "Could not find luckperms group " + name + ". Please add it to luckperms or remove it from staff_roles in tournamentconfig");
				continue;
			}

			staffGroups.put(name, group);
		}

		defaultGroup = LuckPermsProvider.get().getGroupManager().getGroup("default");
		if (defaultGroup == null) {
			Log.error(getName(), "Could not find luckperms group default. Please add it to luckperms");
		}

		/* ----- Database ----- */
		// Connect to the database
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
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		try {
			DBConnection connection = new DBConnection();
			connection.connect(dbCredentials);

			TournamentSystemCommons.setDBConnection(connection);

			connection.startKeepAliveTask();
		} catch (ClassNotFoundException | SQLException e) {
			Log.fatal("TournamentSystem", "Failed to connect to the database");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/* ----- Language ----- */
		// Language files
		Log.info("TournamentSystem", "Loading language files...");
		try {
			LanguageReader.readFromJar(this.getClass(), "/lang/en-us.json");
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* ----- Config ----- */
		if (config.has("servers")) {
			JSONArray serversJSON = config.getJSONArray("servers");
			for (int i = 0; i < serversJSON.length(); i++) {
				JSONObject serverData = serversJSON.getJSONObject(i);
				if (serverData.has("name")) {
					configuredManagedServers.add(serverData.getString("name"));
				}
			}
		}

		useExtendedSpawnLocations = config.getBoolean("extended_spawn_locations");
		celebrationMode = config.getBoolean("celebration_mode");
		replaceEz = config.getBoolean("replace_ez");

		if (config.has("admin_ui_url")) {
			adminUIUrl = config.getString("admin_ui_url");
		}

		if (config.has("dynamic_config_url")) {
			dynamicConfigURL = config.getString("dynamic_config_url");
		}

		if (config.has("behind_your_tail")) {
			JSONObject behindYourTail = config.getJSONObject("behind_your_tail");

			if (behindYourTail.has("compass")) {
				enableBehindYourTailcompass = behindYourTail.getBoolean("compass");
			}

			if (behindYourTail.has("particles")) {
				behindYourTailParticles = behindYourTail.getBoolean("particles");
			}
		}

		JSONObject webSettings = config.getJSONObject("web_ui");
		if (webSettings.has("show_sensitive_telementry_data")) {
			showSensitiveTelementryData = webSettings.getBoolean("show_sensitive_telementry_data");
		}

		if (config.has("no_teams")) {
			noTeamsMode = config.getBoolean("no_teams");
			if (noTeamsMode) {
				Log.info("TournamentSystem", "No teams mode enabled");
			}
		}

		if (config.has("force_show_team_name_in_leaderboard")) {
			forceShowTeamNameInLeaderboard = config.getBoolean("force_show_team_name_in_leaderboard");
		}

		if (config.has("make_team_names_bold")) {
			makeTeamNamesBold = config.getBoolean("make_team_names_bold");
		}

		if (config.has("resource_pack")) {
			resourcePackUrl = config.getString("resource_pack");
		}

		hideTeamNameNextToPlayerIGN = config.optBoolean("hide_team_name_next_to_ign");
		iaNoTeamIcon = config.optString("ia_no_team_icon", null);

		lobbyServer = config.getString("lobby_server");

		serverName = getConfig().getString("server_name");
		addXpLevelOnKill = getConfig().getBoolean("add_xp_level_on_kill");

		// Setup win score
		String winScoreString = "Win score: ";

		@SuppressWarnings("unchecked")
		List<Integer> winScoreList = (List<Integer>) getConfig().getList("win_score");

		winScore = new int[winScoreList.size()];
		for (int i = 0; i < winScoreList.size(); i++) {
			winScore[i] = winScoreList.get(i);
			winScoreString += winScore[i] + (i < (winScore.length - 1) ? ", " : " ");
		}

		Log.info("TournamentSystem", "Win score: " + winScoreString);

		if (NovaCore.isNovaGameEngineEnabled()) {
			Log.info("TournamentSystem", "Setting requested game data directory to " + mapDataFolder.getAbsolutePath());
			NovaCoreGameEngine.getInstance().setRequestedGameDataDirectory(mapDataFolder);
		}

		/* ----- Depends ----- */
		ModuleManager.require(NovaScoreboardManager.class);
		ModuleManager.require(MultiverseManager.class);
		ModuleManager.require(CustomItemManager.class);

		/* ----- Custom items ----- */
		try {
			CustomItemManager.getInstance().addCustomItem(CustomItemTest.class);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | NoSuchMethodException | SecurityException e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "Failed to register custom items");
		}

		/* ----- Scoreboard ----- */
		String tournamentName = TournamentSystemCommons.getTournamentName();
		String scoreboardUrl = TournamentSystemCommons.getScoreboardURL();

		if (tournamentName == null) {
			tournamentName = "";
		}

		if (scoreboardUrl == null) {
			scoreboardUrl = "";
		}

		if (tournamentName.equalsIgnoreCase(ChatColor.stripColor(tournamentName))) {
			tournamentName = ChatColor.AQUA + "" + ChatColor.BOLD + TournamentSystemCommons.getTournamentName();
		}

		Log.info(getName(), "Tournament name: " + tournamentName);
		Log.info(getName(), "Scoreboard url: " + scoreboardUrl);

		this.cachedTournamentLink = scoreboardUrl;
		this.cachedTournamentName = tournamentName;

		NovaScoreboardManager.getInstance().setLineCount(15);
		updateScoreboard();

		/* ----- Team system ----- */
		// TODO: add team size settings
		int teamCount = config.getInt("team_size");

		if (!noTeamsMode) {
			teamManager = new TournamentSystemTeamManager(teamCount);
			NovaCore.getInstance().setTeamManager(teamManager);
		}

		/* ----- Modules ----- */
		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.spigot.score");
		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.spigot.modules");

		if (getConfig().getBoolean("enable_head_drops")) {
			ModuleManager.enable(PlayerHeadDrop.class);
		}

		if (getConfig().getBoolean("enable_edible_heads")) {
			ModuleManager.enable(EdibleHeads.class);
		}

		/* ----- Events ----- */
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		/* ----- Plugin Channels ----- */
		this.pluginMessageListener = new TSPluginMessageListnener();

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, TournamentSystemCommons.DATA_CHANNEL);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, TournamentSystemCommons.DATA_CHANNEL, this.pluginMessageListener);

		/* ----- PlaceholderAPI ----- */
		placeholderAPIExpansion = new PlaceholderAPIExpansion();
		placeholderAPIExpansion.register();

		/* ----- Commands ----- */
		CommandRegistry.registerCommand(new DatabaseCommand());
		CommandRegistry.registerCommand(new FlyCommand());
		CommandRegistry.registerCommand(new HaltCommand());
		CommandRegistry.registerCommand(new PurgeCacheCommand());
		CommandRegistry.registerCommand(new ReconnectCommand());
		CommandRegistry.registerCommand(new YBorderCommand());
		CommandRegistry.registerCommand(new CSPCommand());
		CommandRegistry.registerCommand(new CTPCommand());
		CommandRegistry.registerCommand(new BCCommand());
		CommandRegistry.registerCommand(new RespawnPlayerCommand());
		CommandRegistry.registerCommand(new CopyLocationCommand());
		CommandRegistry.registerCommand(new ChatfilterCommand());
		CommandRegistry.registerCommand(new ReloadDynamicConfigCommand());
		CommandRegistry.registerCommand(new KillStatusReportingCommand());
		CommandRegistry.registerCommand(new ManagedServerCommand());
		CommandRegistry.registerCommand(new EnableFinalGameCommand());

		if (config.has("socials")) {
			JSONObject socials = config.getJSONObject("socials");

			if (socials.has("discord")) {
				CommandRegistry.registerCommand(new DiscordCommand(this, socials.getString("discord")));
			}

			if (socials.has("patreon")) {
				CommandRegistry.registerCommand(new PatreonCommand(this, socials.getString("patreon")));
			}

			if (socials.has("youtube")) {
				CommandRegistry.registerCommand(new YoutubeCommand(this, socials.getString("youtube")));
			}
		}

		/* ----- Permissions ----- */
		PermissionRegistrator.registerPermission(TournamentPermissions.COMMENTATOR_PERMISSION, "Commentator access", PermissionDefault.FALSE);
		PermissionRegistrator.registerPermission(ChatFilter.NOTIFY_PERMISSION, "Get notifications about players trying to use bad words in chat", PermissionDefault.OP);

		/* ----- Music ----- */
		if (config.has("music")) {
			JSONObject musicConfig = config.getJSONObject("music");

			if (NovaCore.isNovaGameEngineEnabled()) {
				if (musicConfig.has("game_end_nbs")) {
					String endNBSFile = musicConfig.getString("game_end_nbs");
					if (endNBSFile.length() > 0) {
						Log.info(getName(), "Loading game end soundtrack " + endNBSFile);
						gameEndMusic = NBSDecoder.parse(getNBSFile(endNBSFile));
						ModuleManager.loadModule(this, GameEndSoundtrackManager.class, true);
					}
				}
			}
		}

		/* ----- Chat Filter ----- */
		if (config.has("chat_filter")) {
			JSONObject filter = config.getJSONObject("chat_filter");

			if (filter.has("url")) {
				String filterUrl = filter.getString("url");
				if (filterUrl.length() > 0) {
					((ChatFilter) ModuleManager.getModule(ChatFilter.class)).setFilterUrl(filterUrl);
				}
			}

			if (filter.has("filtered_commands")) {
				JSONArray filteredCommands = filter.getJSONArray("filtered_commands");
				for (int i = 0; i < filteredCommands.length(); i++) {
					((ChatFilter) ModuleManager.getModule(ChatFilter.class)).getFilteredCommands().add(filteredCommands.getString(i));
				}
			}

			if (filter.optBoolean("enabled", false)) {
				ModuleManager.enable(ChatFilter.class);
			}
		}

		/* ----- Misc ----- */
		if (replaceEz) {
			ModuleManager.enable(EZReplacer.class);
		}

		/* ----- Game support ----- */
		if (getConfig().getBoolean("game_enabled")) {
			if (NovaCore.isNovaGameEngineEnabled()) {
				GameSetup.init(this);
			}
		}

		/* ----- Timers ----- */
		this.timerSeconds = new SimpleTask(this, () -> {
			pluginMessageListener.tickSecond();
		}, 20L);

		statusReportingTask = new SimpleTask(this, () -> reportServerStateAsync(), 20 * 20);

		/* ---- Final launch parameters ----- */
		stateReportingToken = null;
		String tsStateReportingTokenParam = System.getProperty("tournamentStatusReportingKey");
		if (tsStateReportingTokenParam != null) {
			Log.info("TournamentSystem", "Found state reporting token");
			stateReportingToken = tsStateReportingTokenParam;
		}

		internalAPIAccessToken = null;
		String tsInternalAPIAccessToken = System.getProperty("tournamentInternalAPIAccessKey");
		if (tsInternalAPIAccessToken != null) {
			Log.info("TournamentSystem", "Found internal api access token");
			internalAPIAccessToken = tsInternalAPIAccessToken;
		}

		String tsServerNameParam = System.getProperty("tournamentServerNetworkName");
		if (tsServerNameParam != null) {
			Log.info("TournamentSystem", "Using server name " + tsServerNameParam + " from -DtournamentServerNetworkName flag");
			serverName = tsServerNameParam;
		}

		if (internalAPIAccessToken != null) {
			Log.info("TournamentSystem", "Setting up internal API instance");
			api = new TournamentSystemAPI(adminUIUrl, tsInternalAPIAccessToken, INTERNAL_API_TIMEOUT);
		}

		// Register debug commands
		new DebugCommands();

		// Final stuff
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().getWorlds().forEach(world -> world.setAutoSave(false));
				reportServerStateAsync();
				Log.debug("TournamentSystem", "Is final game: " + isFinalGame);
			}
		}.runTaskLater(this, 1L);

		Task.tryStartTask(timerSeconds);
		Task.tryStartTask(statusReportingTask);

		if (Bukkit.getServer().getPluginManager().getPlugin("LabyApi") != null) {
			ModuleManager.loadModule(this, LabyModIntegration.class, true);
		}

		try {
			if (Bukkit.getServer().getPluginManager().getPlugin("CosmeticSystem") != null) {
				if (config.optBoolean("disable_cosmetic_system", false) || ("" + System.getenv("DISABLE_COSMETIC_SYSTEM")).equalsIgnoreCase("true")) {
					Log.info("TournamentSystem", "Disabling cosmetic system since disable_cosmetic_system is set to true or the env variable ");
					Bukkit.getServer().getPluginManager().disablePlugin(Bukkit.getServer().getPluginManager().getPlugin("CosmeticSystem"));
				} else {
					if (Bukkit.getServer().getPluginManager().getPlugin("CosmeticSystem").isEnabled()) {
						Log.info("TournamentSystem", "Enabling cosmetic system integration");
						ModuleManager.loadModule(this, CosmeticsIntegrations.class, true);
					} else {
						Log.info("TournamentSystem", "Skip enabling cosmetic system integration since the plugin is not enabled");
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			Log.error("TournamentSystem", "An error occured when trying to set up costmetic system support");
		}

		if (dynamicConfigURL != null) {
			Log.info("TournamentSystem", "Trying to read dynamic config...");
			if (reloadDynamicConfig()) {
				Log.success("TournamentSystem", "Dynamic config loaded");
			} else {
				Log.error("TournamentSystem", "Failed to update dynamic config");
			}
		}

		if (TournamentSystemCommons.hasRabbitMQManager()) {
			Log.info("TournamentSystem", "Registering RabbitMQ listeners");
			TournamentSystemCommons.getRabbitMQManager().addMessageReceiver(RabbitMQStrings.START_GAME, (data) -> {
				if (NovaCore.isNovaGameEngineEnabled()) {
					if (GameManager.getInstance().isEnabled()) {
						if (GameManager.getInstance().hasGame()) {
							if (!GameManager.getInstance().getCountdown().hasCountdownStarted() && !GameManager.getInstance().getCountdown().hasCountdownFinished()) {
								Log.info("RabbitMQ", "Starting game countdown");
								GameManager.getInstance().getCountdown().startCountdown();
								Log.info("RabbitMQ", "Setting reconnect server");
								TournamentSystemCommons.setActiveServer(TournamentSystem.getInstance().getServerName());
							}
						}
					}
				}
			});

			TournamentSystemCommons.getRabbitMQManager().addMessageReceiver(RabbitMQStrings.LOCKED_WINNER_CHANGED, (data) -> {
				ModuleManager.getModule(WinnerManager.class).pollState();
			});
		} else {
			Log.warn("TournamentSystem", "RabbitMQ not available");
		}
	}

	public void killStatusReporting() {
		Task.tryStopTask(statusReportingTask);
	}

	public void reportServerStateAsync() {
		if (stateReportingToken == null) {
			return;
		}

		final JSONObject json = new JSONObject();

		JSONObject software = new JSONObject();
		JSONObject bukkit = new JSONObject();
		JSONObject java = new JSONObject();

		bukkit.put("name", Bukkit.getName());
		bukkit.put("version", Bukkit.getVersion());
		bukkit.put("bukkit_version", Bukkit.getBukkitVersion());

		java.put("version", System.getProperty("java.version"));
		java.put("name", ManagementFactory.getRuntimeMXBean().getName());
		java.put("vm_name", ManagementFactory.getRuntimeMXBean().getVmName());
		java.put("vm_vendor", ManagementFactory.getRuntimeMXBean().getVmVendor());
		java.put("vm_version", ManagementFactory.getRuntimeMXBean().getVmVersion());

		software.put("bukkit", bukkit);
		software.put("java", java);

		JSONArray plugins = new JSONArray();
		Arrays.asList(Bukkit.getServer().getPluginManager().getPlugins()).forEach(plugin -> {
			JSONObject data = new JSONObject();
			data.put("name", plugin.getName());
			data.put("version", plugin.getDescription().getVersion());
			data.put("authors", String.join(",", plugin.getDescription().getAuthors()));
			data.put("enabled", plugin.isEnabled());
			plugins.put(data);
		});

		JSONArray modules = new JSONArray();
		ModuleManager.getModules().forEach((className, module) -> {
			JSONObject data = new JSONObject();
			data.put("class_name", className);
			data.put("name", module.getName());
			data.put("enabled", module.isEnabled());
			modules.put(data);
		});

		json.put("plugins", plugins);
		json.put("modules", modules);
		json.put("software", software);
		json.put("port", Bukkit.getServer().getPort());

		AsyncManager.runAsync(() -> {
			try {
				URL url = new URL(adminUIUrl + "/api/internal/server/state_reporting");
				HttpURLConnection connection = (HttpURLConnection) url.openConnection();
				connection.setDoOutput(true);
				connection.setConnectTimeout(STATUS_REPORTING_TIMEOUT);
				connection.setReadTimeout(STATUS_REPORTING_TIMEOUT);
				connection.setInstanceFollowRedirects(true);
				connection.setRequestMethod("POST");
				connection.setRequestProperty("Content-Type", "application/json");
				connection.setRequestProperty("charset", "utf-8");
				connection.setRequestProperty("Authorization", "Bearer " + stateReportingToken);
				connection.connect();

				OutputStream os = connection.getOutputStream();
				byte[] input = json.toString().getBytes("utf-8");
				os.write(input, 0, input.length);
				os.flush();
				os.close();

				int httpResult = connection.getResponseCode();
				if (httpResult != HttpURLConnection.HTTP_OK) {
					Log.warn("TournamentSystem", "Status reporting failed with status code " + httpResult + ". If this error occurs multiple times you can use the /killstatusreporting command to disable this feature");
				}
			} catch (Exception e) {
				e.printStackTrace();
				Log.warn("TournamentSystem", "Status reporting failed. " + e.getClass().getName() + " " + e.getMessage() + ". If this error occurs multiple times you can use the /killstatusreporting command to disable this feature");
			}
		});
	}

	public void updateScoreboard() {
		if (!disableScoreboard) {
			String tournamentName = cachedTournamentName == null ? "NULL" : cachedTournamentName;
			String tournamentLink = cachedTournamentLink == null ? "" : ChatColor.YELLOW + cachedTournamentLink;

			if (useItemsAdder) {
				tournamentName = TSItemsAdderUtils.addFontImages(tournamentName);
				tournamentLink = TSItemsAdderUtils.addFontImages(tournamentLink);
			}

			NovaScoreboardManager.getInstance().setDefaultTitle(new StaticScoreboardTitle(tournamentName));
			NovaScoreboardManager.getInstance().setGlobalLine(14, new StaticTextLine(tournamentLink));
		}
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(timerSeconds);
		Task.tryStopTask(statusReportingTask);
		Bukkit.getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);

		TournamentSystemCommons.getRabbitMQManager().close();

		if (placeholderAPIExpansion != null) {
			if (placeholderAPIExpansion.isRegistered()) {
				placeholderAPIExpansion.unregister();
			}
		}
	}

	public String getAdminUIUrl() {
		return adminUIUrl;
	}
}