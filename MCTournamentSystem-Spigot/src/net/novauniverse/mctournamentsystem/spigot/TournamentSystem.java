package net.novauniverse.mctournamentsystem.spigot;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.team.TeamOverrides;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.novauniverse.mctournamentsystem.spigot.command.bc.BCCommand;
import net.novauniverse.mctournamentsystem.spigot.command.chatfilter.ChatfilterCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.csp.CSPCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.ctp.CTPCommand;
import net.novauniverse.mctournamentsystem.spigot.command.copylocation.CopyLocationCommand;
import net.novauniverse.mctournamentsystem.spigot.command.database.DatabaseCommand;
import net.novauniverse.mctournamentsystem.spigot.command.database.socials.implementation.DiscordCommand;
import net.novauniverse.mctournamentsystem.spigot.command.database.socials.implementation.PatreonCommand;
import net.novauniverse.mctournamentsystem.spigot.command.database.socials.implementation.YoutubeCommand;
import net.novauniverse.mctournamentsystem.spigot.command.fly.FlyCommand;
import net.novauniverse.mctournamentsystem.spigot.command.halt.HaltCommand;
import net.novauniverse.mctournamentsystem.spigot.command.purgecache.PurgeCacheCommand;
import net.novauniverse.mctournamentsystem.spigot.command.reconnect.ReconnectCommand;
import net.novauniverse.mctournamentsystem.spigot.command.respawnplayer.RespawnPlayerCommand;
import net.novauniverse.mctournamentsystem.spigot.command.yborder.YBorderCommand;
import net.novauniverse.mctournamentsystem.spigot.debug.DebugCommands;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.ITournamentSystemPlayerEliminationMessageProvider;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.TournamentSystemDefaultPlayerEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.game.gamespecific.misc.GameEndSoundtrackManager;
import net.novauniverse.mctournamentsystem.spigot.game.util.DefaultPlayerEliminatedTitleProvider;
import net.novauniverse.mctournamentsystem.spigot.game.util.PlayerEliminatedTitleProvider;
import net.novauniverse.mctournamentsystem.spigot.misc.CustomItemTest;
import net.novauniverse.mctournamentsystem.spigot.modules.chatfilter.ChatFilter;
import net.novauniverse.mctournamentsystem.spigot.modules.ezreplacer.EZReplacer;
import net.novauniverse.mctournamentsystem.spigot.modules.head.EdibleHeads;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.permissions.TournamentPermissions;
import net.novauniverse.mctournamentsystem.spigot.placeholderapi.PlaceholderAPIExpansion;
import net.novauniverse.mctournamentsystem.spigot.pluginmessages.TSPluginMessageListnener;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreListener;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.novauniverse.mctournamentsystem.spigot.utils.TSItemsAdderUtils;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.customitems.CustomItemManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.permission.PermissionRegistrator;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TournamentSystem extends JavaPlugin implements Listener {
	private static TournamentSystem instance;

	private TournamentSystemTeamManager teamManager;
	private String serverName;
	private String lobbyServer;

	private boolean builtInScoreSystemDisabled;

	private int[] winScore;

	private int dropperCompleteLevelScore;

	private boolean addXpLevelOnKill;
	private boolean useExtendedSpawnLocations;
	private boolean celebrationMode;
	private boolean replaceEz;
	private boolean noTeamsMode;
	private boolean eliminationTitleMessageEnabled;

	private double chickenOutFeatherScoreMultiplier;

	private List<Consumer<Player>> respawnPlayerCallbacks;

	private ScoreListener scoreListener;

	private ITournamentSystemPlayerEliminationMessageProvider playerEliminationMessageProvider;

	private TournamentSystemDefaultPlayerEliminationMessage defaultPlayerEliminationMessage;

	private String labymodBanner;

	private Map<String, Group> staffGroups;
	private Group defaultGroup;

	private File sqlFixFile;
	private File mapDataFolder;
	private File nbsFolder;
	private File globalDataFolder;

	private boolean forceShowTeamNameInLeaderboard;
	private boolean makeTeamNamesBold;

	private String cachedTournamentName;
	private String cachedTournamentLink;

	private String globalConfigPath;

	private String resourcePackUrl;

	private PlayerEliminatedTitleProvider playerEliminatedTitleProvider;

	private boolean showSensitiveTelementryData;

	private Song gameEndMusic;

	private Task timerSeconds;

	private TSPluginMessageListnener pluginMessageListener;

	private boolean disableScoreboard;

	private PlaceholderAPIExpansion placeholderAPIExpansion;

	private boolean enableBehindYourTailcompass;
	private boolean behindYourTailParticles;

	private boolean useItemsAdder;

	public static TournamentSystem getInstance() {
		return instance;
	}

	@Nullable
	public PlayerEliminatedTitleProvider getPlayerEliminatedTitleProvider() {
		return playerEliminatedTitleProvider;
	}

	public void setPlayerEliminatedTitleProvider(@Nullable PlayerEliminatedTitleProvider playerEliminatedTitleProvider) {
		this.playerEliminatedTitleProvider = playerEliminatedTitleProvider;
	}

	public File getSqlFixFile() {
		return sqlFixFile;
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

	public String getLabymodBanner() {
		return labymodBanner;
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

	public int getDropperCompleteLevelScore() {
		return dropperCompleteLevelScore;
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

	public double getChickenOutFeatherScoreMultiplier() {
		return chickenOutFeatherScoreMultiplier;
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

	public String getGlobalConfigPath() {
		return globalConfigPath;
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

	public String readResourceFromJARAsString(String filename) throws IOException {
		InputStream is = getClass().getResourceAsStream(filename);
		InputStreamReader isr = new InputStreamReader(is);
		BufferedReader br = new BufferedReader(isr);
		StringBuffer sb = new StringBuffer();
		String line;
		while ((line = br.readLine()) != null) {
			sb.append(line);
		}
		br.close();
		isr.close();
		is.close();
		return sb.toString();
	}

	@Override
	public void onEnable() {
		// Init session id
		TournamentSystemCommons.getSessionId();

		TournamentSystem.instance = this;
		this.staffGroups = new HashMap<>();
		this.labymodBanner = null;

		this.noTeamsMode = false;

		this.respawnPlayerCallbacks = new ArrayList<>();

		this.defaultPlayerEliminationMessage = new TournamentSystemDefaultPlayerEliminationMessage();
		this.playerEliminationMessageProvider = this.getDefaultPlayerEliminationMessage();

		this.playerEliminatedTitleProvider = new DefaultPlayerEliminatedTitleProvider();

		this.scoreListener = null;

		this.builtInScoreSystemDisabled = false;
		this.eliminationTitleMessageEnabled = true;

		this.chickenOutFeatherScoreMultiplier = 0;

		this.forceShowTeamNameInLeaderboard = false;
		this.makeTeamNamesBold = false;

		this.resourcePackUrl = null;

		this.showSensitiveTelementryData = false;

		this.disableScoreboard = false;

		this.placeholderAPIExpansion = null;

		this.enableBehindYourTailcompass = false;
		this.behindYourTailParticles = false;

		this.useItemsAdder = getConfig().getBoolean("enable_items_adder");

		/* ----- Setup files ----- */
		saveDefaultConfig();
		sqlFixFile = new File(this.getDataFolder().getPath() + File.separator + "sql_fix.sql");

		globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();

		this.nbsFolder = new File(TournamentSystem.getInstance().getGlobalConfigPath() + File.separator + "nbs");
		this.nbsFolder.mkdirs();

		this.globalDataFolder = new File(globalConfigPath);
		this.mapDataFolder = new File(globalConfigPath + File.separator + "map_data");

		TeamOverrides.readOverrides(globalDataFolder);

		File configFile = new File(globalConfigPath + File.separator + "tournamentconfig.json");
		JSONObject config;
		try {
			if (!configFile.exists()) {
				Log.warn("TournamentSystem", "Config file " + configFile.getAbsolutePath() + " does not exits. Attempting to create it...");

				String defaultConfig = readResourceFromJARAsString("/default_config.json");
				JSONFileUtils.saveJson(configFile, new JSONObject(defaultConfig), 4);
			}

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

		// Try to create the files and folders and load the worlds
		try {
			FileUtils.touch(sqlFixFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.fatal("TournamentSystem", "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/* ----- Database ----- */
		// Connect to the database
		JSONObject mysqlDatabaseConfig = config.getJSONObject("database").getJSONObject("mysql");

		DBCredentials dbCredentials = new DBCredentials(mysqlDatabaseConfig.getString("driver"), mysqlDatabaseConfig.getString("host"), mysqlDatabaseConfig.getString("username"), mysqlDatabaseConfig.getString("password"), mysqlDatabaseConfig.getString("database"));

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
		useExtendedSpawnLocations = config.getBoolean("extended_spawn_locations");
		celebrationMode = config.getBoolean("celebration_mode");
		replaceEz = config.getBoolean("replace_ez");

		chickenOutFeatherScoreMultiplier = getConfig().getDouble("chicken_out_feather_score_multiplier");

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

		JSONObject labymodBanner = config.getJSONObject("labymod_banner");
		if (labymodBanner.getBoolean("enabled")) {
			this.labymodBanner = labymodBanner.getString("url");
		}

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

		dropperCompleteLevelScore = getConfig().getInt("dropper_level_complete_score");

		/* ----- Depends ----- */
		ModuleManager.require(NetherBoardScoreboard.class);
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

		if (tournamentName.equalsIgnoreCase(ChatColor.stripColor(tournamentName))) {
			tournamentName = ChatColor.AQUA + "" + ChatColor.BOLD + TournamentSystemCommons.getTournamentName();
		}

		Log.info(getName(), "Tournament name: " + tournamentName);
		Log.info(getName(), "Scoreboard url: " + scoreboardUrl);

		this.cachedTournamentLink = scoreboardUrl;
		this.cachedTournamentName = tournamentName;

		NetherBoardScoreboard.getInstance().setLineCount(15);
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

			if (filter.has("enabled")) {
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

		// Register debug commands
		new DebugCommands();

		// Final stuff
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.getServer().getWorlds().forEach(world -> world.setAutoSave(false));
			}
		}.runTaskLater(this, 1L);

		Task.tryStartTask(timerSeconds);
	}

	public void updateScoreboard() {
		if (!disableScoreboard) {
			String tournamentName = cachedTournamentName == null ? "NULL" : cachedTournamentName;
			String tournamentLink = cachedTournamentLink == null ? "" : ChatColor.YELLOW + cachedTournamentLink;

			if (useItemsAdder) {
				tournamentName = TSItemsAdderUtils.addFontImages(tournamentName);
				tournamentLink = TSItemsAdderUtils.addFontImages(tournamentLink);
			}

			NetherBoardScoreboard.getInstance().setDefaultTitle(tournamentName);
			NetherBoardScoreboard.getInstance().setGlobalLine(14, tournamentLink);
		}
	}

	@Override
	public void onDisable() {
		Task.tryStopTask(timerSeconds);
		Bukkit.getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);

		if (placeholderAPIExpansion != null) {
			if (placeholderAPIExpansion.isRegistered()) {
				placeholderAPIExpansion.unregister();
			}
		}
	}
}