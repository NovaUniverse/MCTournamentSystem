package net.novauniverse.mctournamentsystem.spigot;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.command.database.DatabaseCommand;
import net.novauniverse.mctournamentsystem.spigot.command.fly.FlyCommand;
import net.novauniverse.mctournamentsystem.spigot.command.halt.HaltCommand;
import net.novauniverse.mctournamentsystem.spigot.command.purgecache.PurgeCacheCommand;
import net.novauniverse.mctournamentsystem.spigot.command.reconnect.ReconnectCommand;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.pluginmessages.TSPluginMessageListnener;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeamManager;
import net.zeeraa.novacore.commons.database.DBConnection;
import net.zeeraa.novacore.commons.database.DBCredentials;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.language.LanguageReader;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.multiverse.MultiverseManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;

public class TournamentSystem extends JavaPlugin implements Listener {
	private static TournamentSystem instance;

	private File sqlFixFile;
	private TournamentSystemTeamManager teamManager;
	private String serverName;
	private String lobbyServer;
	private int[] winScore;

	private File worldFolder;
	private File gameLobbyFolder;

	public static TournamentSystem getInstance() {
		return instance;
	}

	public File getSqlFixFile() {
		return sqlFixFile;
	}

	public TournamentSystemTeamManager getTeamManager() {
		return teamManager;
	}

	@Override
	public void onLoad() {
		TournamentSystem.instance = this;
	}

	@Override
	public void onEnable() {
		/* ----- Setup files ----- */
		saveDefaultConfig();
		sqlFixFile = new File(this.getDataFolder().getPath() + File.separator + "sql_fix.sql");
		worldFolder = new File(this.getDataFolder().getPath() + File.separator + "worlds");
		gameLobbyFolder = new File(this.getDataFolder().getPath() + File.separator + "game_lobby");

		// Try to create the files and folders and load the worlds
		try {
			FileUtils.forceMkdir(worldFolder);
			FileUtils.forceMkdir(gameLobbyFolder);
			FileUtils.touch(sqlFixFile);
		} catch (IOException e1) {
			e1.printStackTrace();
			Log.fatal("TournamentCore", "Failed to setup data directory");
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/* ----- Database ----- */
		// Connect to the database
		DBCredentials dbCredentials = new DBCredentials(getConfig().getString("mysql.driver"), getConfig().getString("mysql.host"), getConfig().getString("mysql.username"), getConfig().getString("mysql.password"), getConfig().getString("mysql.database"));

		try {
			DBConnection connection = new DBConnection();
			connection.connect(dbCredentials);

			TournamentSystemCommons.setDBConnection(connection);

			connection.startKeepAliveTask();
		} catch (ClassNotFoundException | SQLException e) {
			Log.fatal("TournamentCore", "Failed to connect to the database");
			e.printStackTrace();
			Bukkit.getPluginManager().disablePlugin(this);
			return;
		}

		/* ----- Language ----- */
		// Language files
		Log.info("TournamentCore", "Loading language files...");
		try {
			LanguageReader.readFromJar(this.getClass(), "/lang/en-us.json");
		} catch (Exception e) {
			e.printStackTrace();
		}

		/* ----- Config ----- */
		serverName = getConfig().getString("server_name");
		lobbyServer = getConfig().getString("lobby_server");

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
			tournamentName = ChatColor.AQUA + " " + ChatColor.BOLD + TournamentSystemCommons.getTournamentName();
		}

		Log.info(getName(), "Tournament name: " + tournamentName);
		Log.info(getName(), "Scoreboard url: " + scoreboardUrl);

		NetherBoardScoreboard.getInstance().setLineCount(15);
		NetherBoardScoreboard.getInstance().setDefaultTitle(tournamentName == null ? "NULL" : tournamentName);
		NetherBoardScoreboard.getInstance().setGlobalLine(14, scoreboardUrl == null ? "" : ChatColor.YELLOW + scoreboardUrl);

		/* ----- Team system ----- */
		int teamCount = 12; // TODO: add team size settings

		teamManager = new TournamentSystemTeamManager(teamCount);
		NovaCore.getInstance().setTeamManager(teamManager);

		/* ----- Modules ----- */
		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.spigot.score");
		ModuleManager.scanForModules(this, "net.novauniverse.mctournamentsystem.spigot.modules");
		
		/* ----- Events ----- */
		Bukkit.getServer().getPluginManager().registerEvents(this, this);

		/* ----- Plugin Channels ----- */
		this.getServer().getMessenger().registerOutgoingPluginChannel(this, TournamentSystemCommons.DATA_CHANNEL);
		this.getServer().getMessenger().registerIncomingPluginChannel(this, TournamentSystemCommons.DATA_CHANNEL, new TSPluginMessageListnener());

		/* ----- Commands ----- */
		CommandRegistry.registerCommand(new DatabaseCommand());
		CommandRegistry.registerCommand(new FlyCommand());
		CommandRegistry.registerCommand(new HaltCommand());
		CommandRegistry.registerCommand(new PurgeCacheCommand());
		CommandRegistry.registerCommand(new ReconnectCommand());
		
		/* ----- Misc ----- */
		new BukkitRunnable() {
			@Override
			public void run() {
				/* ----- Run after load ----- */
			}
		}.runTask(this);

		/* ----- Game support ----- */
		if (getConfig().getBoolean("game_enabled")) {
			if (!NovaCore.isNovaGameEngineEnabled()) {
				GameSetup.init(this);
			}
		}
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

	public File getGameLobbyFolder() {
		return gameLobbyFolder;
	}

	public File getWorldsFolder() {
		return worldFolder;
	}
}
