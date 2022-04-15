package net.novauniverse.mctournamentsystem.spigot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.permissions.PermissionDefault;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONArray;
import org.json.JSONObject;

import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.group.Group;
import net.novauniverse.mctournamentsystem.commons.LCS;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.utils.TSFileUtils;
import net.novauniverse.mctournamentsystem.spigot.command.bc.BCCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.cinvsee.CInvseeCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.csp.CSPCommand;
import net.novauniverse.mctournamentsystem.spigot.command.commentator.ctp.CTPCommand;
import net.novauniverse.mctournamentsystem.spigot.command.database.DatabaseCommand;
import net.novauniverse.mctournamentsystem.spigot.command.fly.FlyCommand;
import net.novauniverse.mctournamentsystem.spigot.command.halt.HaltCommand;
import net.novauniverse.mctournamentsystem.spigot.command.misc.WhatIsDogeWorthCommand;
import net.novauniverse.mctournamentsystem.spigot.command.purgecache.PurgeCacheCommand;
import net.novauniverse.mctournamentsystem.spigot.command.reconnect.ReconnectCommand;
import net.novauniverse.mctournamentsystem.spigot.command.respawnplayer.RespawnPlayerCommand;
import net.novauniverse.mctournamentsystem.spigot.command.yborder.YBorderCommand;
import net.novauniverse.mctournamentsystem.spigot.debug.DebugCommands;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.ITournamentSystemPlayerEliminationMessageProvider;
import net.novauniverse.mctournamentsystem.spigot.eliminationmessage.TournamentSystemDefaultPlayerEliminationMessage;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.modules.ezreplacer.EZReplacer;
import net.novauniverse.mctournamentsystem.spigot.modules.head.EdibleHeads;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.novauniverse.mctournamentsystem.spigot.permissions.TournamentPermissions;
import net.novauniverse.mctournamentsystem.spigot.pluginmessages.TSPluginMessageListnener;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.permission.PermissionRegistrator;

public class TournamentSystem extends JavaPlugin implements Listener {
	private static TournamentSystem instance;

	private File sqlFixFile;
	private TournamentSystemTeamManager teamManager;
	private String serverName;
	private String lobbyServer;

	private int[] winScore;

	private boolean addXpLevelOnKill;
	private boolean useExtendedSpawnLocations;
	private boolean celebrationMode;
	private boolean replaceEz;

	private ITournamentSystemPlayerEliminationMessageProvider playerEliminationMessageProvider;

	private String labymodBanner;

	private Map<String, Group> staffGroups;
	private Group defaultGroup;

	private File mapDataFolder;

	public static TournamentSystem getInstance() {
		return instance;
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

	@Override
	public void onLoad() {
		System.out.println("TournamentSystem#onLoad()");
	}

	public ITournamentSystemPlayerEliminationMessageProvider getPlayerEliminationMessageProvider() {
		return playerEliminationMessageProvider;
	}
	
	public void setPlayerEliminationMessageProvider(ITournamentSystemPlayerEliminationMessageProvider playerEliminationMessageProvider) {
		this.playerEliminationMessageProvider = playerEliminationMessageProvider;
	}

	@Override
	public void onEnable() {
		TournamentSystem.instance = this;
		this.staffGroups = new HashMap<>();
		this.labymodBanner = null;

		this.playerEliminationMessageProvider = new TournamentSystemDefaultPlayerEliminationMessage();

		/* ----- Setup files ----- */
		saveDefaultConfig();
		sqlFixFile = new File(this.getDataFolder().getPath() + File.separator + "sql_fix.sql");

		String globalConfigPath = TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(TSFileUtils.getParentSafe(this.getDataFolder())))).getAbsolutePath();
		// new File(new
		// File(getDataFolder().getParentFile().getAbsolutePath()).getParentFile().getAbsolutePath()).getParentFile().getAbsolutePath();

		this.mapDataFolder = new File(globalConfigPath + File.separator + "map_data");

		File configFile = new File(globalConfigPath + File.separator + "tournamentconfig.json");
		JSONObject config;
		try {
			if (!configFile.exists()) {
				Log.fatal("TournamentSystem", "Config file not found at " + configFile.getAbsolutePath());
				Bukkit.getPluginManager().disablePlugin(this);
				return;
			}

			config = JSONFileUtils.readJSONObjectFromFile(configFile);
		} catch (Exception e) {
			Log.fatal("TournamentSystem", "Failed to parse the config file at " + configFile.getAbsolutePath());
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

		/* ----- Depends ----- */
		ModuleManager.require(NetherBoardScoreboard.class);
		ModuleManager.require(MultiverseManager.class);

		/* ----- Scoreboard ----- */
		String tournamentName = TournamentSystemCommons.getTournamentName();
		String scoreboardUrl = TournamentSystemCommons.getScoreboardURL();

		if (tournamentName.equalsIgnoreCase(ChatColor.stripColor(tournamentName))) {
			tournamentName = ChatColor.AQUA + "" + ChatColor.BOLD + TournamentSystemCommons.getTournamentName();
		}

		Log.info(getName(), "Tournament name: " + tournamentName);
		Log.info(getName(), "Scoreboard url: " + scoreboardUrl);

		NetherBoardScoreboard.getInstance().setLineCount(15);
		NetherBoardScoreboard.getInstance().setDefaultTitle(tournamentName == null ? "NULL" : tournamentName);
		NetherBoardScoreboard.getInstance().setGlobalLine(14, scoreboardUrl == null ? "" : ChatColor.YELLOW + scoreboardUrl);

		/* ----- Team system ----- */
		// TODO: add team size settings
		int teamCount = config.getInt("team_size");

		teamManager = new TournamentSystemTeamManager(teamCount);
		NovaCore.getInstance().setTeamManager(teamManager);

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
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, TournamentSystemCommons.PLAYER_TELEMENTRY_CHANNEL);

		this.getServer().getMessenger().registerOutgoingPluginChannel(this, TournamentSystemCommons.DATA_CHANNEL);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, TournamentSystemCommons.DATA_CHANNEL, new TSPluginMessageListnener());

		/* ----- Commands ----- */
		CommandRegistry.registerCommand(new DatabaseCommand());
		CommandRegistry.registerCommand(new FlyCommand());
		CommandRegistry.registerCommand(new HaltCommand());
		CommandRegistry.registerCommand(new PurgeCacheCommand());
		CommandRegistry.registerCommand(new ReconnectCommand());
		CommandRegistry.registerCommand(new YBorderCommand());
		CommandRegistry.registerCommand(new WhatIsDogeWorthCommand());
		CommandRegistry.registerCommand(new CSPCommand());
		CommandRegistry.registerCommand(new CTPCommand());
		CommandRegistry.registerCommand(new CInvseeCommand());
		CommandRegistry.registerCommand(new BCCommand());
		CommandRegistry.registerCommand(new RespawnPlayerCommand());

		/* ----- Permissions ----- */
		PermissionRegistrator.registerPermission(TournamentPermissions.COMMENTATOR_PERMISSION, "Commantator access", PermissionDefault.FALSE);

		/* ----- Misc ----- */
		if (replaceEz) {
			ModuleManager.enable(EZReplacer.class);
		}

		new BukkitRunnable() {
			@Override
			public void run() {
				/* ----- Run after load ----- */
				try {
					if (!LCS.connectivityCheck()) {
						Log.fatal("Could not connect to the license servers. Please join our discord server https://discord.gg/4gZSVJ7 and open a ticket about this and we will try to resolve it asap");
						Bukkit.getServer().shutdown();
						return;
					}

					File licenseFile = new File(globalConfigPath + File.separator + "license_key.txt");
					boolean success = LCS.check(licenseFile);
					if (!success) {
						if (!LCS.isValid()) {
							Log.error("Server will shutdown due to a missing or invalid liscense key");
						} else if (LCS.isExpired()) {
							Log.error("Server will shutdown due to a expired liscense key");
						}

						Bukkit.getServer().shutdown();
					}
				} catch (Exception e) {
					Log.fatal("License validation failure");
					Bukkit.getServer().shutdown();
				}

				new BukkitRunnable() {
					@Override
					public void run() {
						if (LCS.isDemo() || !LCS.isValid()) {
							Bukkit.getServer().getOnlinePlayers().forEach(p -> p.sendMessage(ChatColor.RED + "This server is running a demo version of TournamentSystem by NovaUniverse. To get a license open a ticket in our discord server https://discord.gg/4gZSVJ7"));
						}
					}
				}.runTaskTimer(instance, 20 * 60, 20 * 60);
			}
		}.runTask(this);

		/* ----- Game support ----- */
		if (getConfig().getBoolean("game_enabled")) {
			if (NovaCore.isNovaGameEngineEnabled()) {
				GameSetup.init(this);
			}
		}

		// Register debug commands
		new DebugCommands();
	}

	@Override
	public void onDisable() {
		Bukkit.getServer().getScheduler().cancelTasks(this);
		HandlerList.unregisterAll((Plugin) this);
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

	/* ----- Send annoying messages to the player ----- */
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent e) {
		if (LCS.isDemo()) {
			e.getPlayer().sendMessage(ChatColor.RED + "This server is running a demo version of TournamentSystem by NovaUniverse. To get a license open a ticket in our discord server https://discord.gg/4gZSVJ7");
		}
	}

	@EventHandler
	public void onPlayerTeleport(PlayerTeleportEvent e) {
		if (LCS.isDemo()) {
			e.getPlayer().sendMessage(ChatColor.RED + "This server is running a demo version of TournamentSystem by NovaUniverse. To get a license open a ticket in our discord server https://discord.gg/4gZSVJ7");
		}
	}
}