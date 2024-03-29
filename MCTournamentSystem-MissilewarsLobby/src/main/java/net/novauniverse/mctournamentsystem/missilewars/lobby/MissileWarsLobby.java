package net.novauniverse.mctournamentsystem.missilewars.lobby;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.games.missilewars.game.team.MissileWarsTeam;
import net.novauniverse.games.missilewars.game.team.TeamColor;
import net.novauniverse.mctournamentsystem.commons.utils.processes.ProcessUtils;
import net.novauniverse.mctournamentsystem.missilewars.lobby.command.hub.HubCommand;
import net.novauniverse.mctournamentsystem.missilewars.lobby.gamestarter.DefaultCountdownGameStarter;
import net.novauniverse.mctournamentsystem.missilewars.lobby.gamestarter.GameStarter;
import net.novauniverse.mctournamentsystem.missilewars.lobby.modules.GameStartScoreboardCountdown;
import net.novauniverse.mctournamentsystem.missilewars.lobby.modules.MissileWarsHandler;
import net.novauniverse.mctournamentsystem.missilewars.lobby.modules.MissilewarsScoreboard;
import net.zeeraa.novacore.commons.async.AsyncManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.command.CommandRegistry;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameEndReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameEndEvent;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.title.StaticScoreboardTitle;
import net.zeeraa.novacore.spigot.novaplugin.NovaPlugin;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;
import net.zeeraa.novacore.spigot.utils.BungeecordUtils;

public class MissileWarsLobby extends NovaPlugin implements Listener {
	private static MissileWarsLobby instance;
	private GameStarter starter;

	private int parentProcessID;

	public static MissileWarsLobby getInstance() {
		return instance;
	}

	public GameStarter getStarter() {
		return starter;
	}

	private Task checkTask;
	private Task parentPIDMonitoringTask;
	private int stopTimer;

	@Override
	public void onEnable() {
		MissileWarsLobby.instance = this;

		stopTimer = 30;
		parentPIDMonitoringTask = null;

		String parentProcessIdStr = System.getProperty("tournamentServerParentProcessID");
		if (parentProcessIdStr == null) {
			Log.info("MissileWarsLobby", "Did not detect -DtournamentServerParentProcessID flag. We wont monitor for parent process");
		} else {
			Log.info("MissileWarsLobby", "Detected -DtournamentServerParentProcessID flag");
			try {
				parentProcessID = Integer.parseInt(parentProcessIdStr);
				Log.info("MissileWarsLobby", "Monitoring process " + parentProcessID + " for automatic shutdown");
				parentPIDMonitoringTask = new SimpleTask(this, () -> {
					if (!ProcessUtils.isProcessRunning(parentProcessID)) {
						Task.tryStopTask(parentPIDMonitoringTask);
						Log.warn("MissileWarsLobby", "Parent process " + parentProcessID + " no longer found. Shutting down");
						AsyncManager.runSync(() -> {
							Bukkit.getServer().shutdown();
						});
					}
				}, 20 * 5);
				Task.tryStartTask(parentPIDMonitoringTask);
			} catch (Exception e) {
				Log.fatal("MissileWarsLobby", "-DtournamentServerParentProcessID flag was set to non numeric value");
				Bukkit.getServer().shutdown();
			}
		}

		checkTask = new SimpleTask(this, new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().isRunning()) {
						boolean hasGreen = false;
						boolean hasRed = false;

						for (Player p : Bukkit.getServer().getOnlinePlayers()) {
							Team team = TeamManager.getTeamManager().getPlayerTeam(p);
							if (team != null) {
								if (team instanceof MissileWarsTeam) {
									MissileWarsTeam mt = (MissileWarsTeam) team;

									if (mt.getColor() == TeamColor.GREEN) {
										hasGreen = true;
									}

									if (mt.getColor() == TeamColor.RED) {
										hasRed = true;
									}
								}
							}
						}

						if (hasRed && hasGreen) {
							stopTimer = 30;
						} else {
							stopTimer--;
							if (stopTimer == 0) {
								// end game

								if (hasRed && !hasGreen) {
									Bukkit.broadcastMessage(ChatColor.GOLD + "Not enough players to continue. Winning team: " + ChatColor.RED + "Red");
								} else if (hasGreen && !hasRed) {
									Bukkit.broadcastMessage(ChatColor.GOLD + "Not enough players to continue. Winning team: " + ChatColor.GREEN + "Green");
								} else {
									Bukkit.broadcastMessage(ChatColor.GOLD + "Not enough players to continue");
								}

								GameManager.getInstance().getActiveGame().endGame(GameEndReason.SERVER_ENDED_GAME);
							}
						}
					}
				}
			}
		}, 20L);

		this.starter = new DefaultCountdownGameStarter();

		this.registerEvents(this);

		CommandRegistry.registerCommand(new HubCommand(this));

		requireModule(NovaScoreboardManager.class);
		NovaScoreboardManager.getInstance().setLineCount(7);
		NovaScoreboardManager.getInstance().setDefaultTitle(new StaticScoreboardTitle(ChatColor.AQUA + "" + ChatColor.BOLD + "MissileWars"));
		NovaScoreboardManager.getInstance().setGlobalLine(3, new StaticTextLine(ChatColor.GOLD + "Use " + ChatColor.AQUA + "/hub " + ChatColor.GOLD + "to get back"));
		NovaScoreboardManager.getInstance().setGlobalLine(4, new StaticTextLine(ChatColor.GOLD + "to the tournament"));

		ModuleManager.loadModule(this, MissilewarsScoreboard.class, true);
		ModuleManager.loadModule(this, GameStartScoreboardCountdown.class, true);
		ModuleManager.loadModule(this, MissileWarsHandler.class, true);

		new BukkitRunnable() {
			@Override
			public void run() {
				starter.onEnable();
			}
		}.runTaskLater(this, 1L);

		Task.tryStartTask(checkTask);
	}

	@Override
	public void onDisable() {
		this.cancelTasks(this);
		Task.tryStopTask(checkTask);
		Task.tryStopTask(parentPIDMonitoringTask);
		Bukkit.getScheduler().cancelTasks(this);
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().sendMessage(ChatColor.GOLD + "" + ChatColor.BOLD + "Welcome to missilewars. In this minigame you will have to use the items you get to destroy your opponents shield. Use " + ChatColor.AQUA + ChatColor.BOLD + "/hub" + ChatColor.GOLD + ChatColor.BOLD + " to get back to the tournament lobby");
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onGameEnd(GameEndEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Bukkit.broadcastMessage(ChatColor.AQUA + "Sending you to the lobby in 15 seconds");
				new BukkitRunnable() {
					@Override
					public void run() {
						Bukkit.getServer().getOnlinePlayers().forEach(p -> {
							BungeecordUtils.sendToServer(p, "lobby");
						});
						new BukkitRunnable() {
							@Override
							public void run() {
								Bukkit.getServer().getOnlinePlayers().forEach(p -> {
									p.kickPlayer(ChatColor.RED + "Failed to send you to the lobby. Please reconnect");
								});

								Bukkit.getServer().shutdown();
							}
						}.runTaskLater(getInstance(), 60L);
					}
				}.runTaskLater(getInstance(), 15 * 20);
			}
		}.runTaskLater(getInstance(), 20L);
	}
}