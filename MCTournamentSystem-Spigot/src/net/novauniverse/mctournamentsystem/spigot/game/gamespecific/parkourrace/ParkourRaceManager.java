package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.parkourrace;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.novauniverse.games.parkourrace.NovaParkourRace;
import net.novauniverse.games.parkourrace.game.ParkourRace;
import net.novauniverse.games.parkourrace.game.data.PlayerData;
import net.novauniverse.games.parkourrace.game.event.ParkourRacePlayerCompleteEvent;
import net.novauniverse.games.parkourrace.game.event.ParkourRacePlayerCompleteLapEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.Callback;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class ParkourRaceManager extends NovaModule implements Listener {
	public static int TIME_LEFT_LINE = 5;
	public static int LAP_LINE = 6;

	private Task task;

	public ParkourRaceManager() {
		super("TournamentSystem.GameSpecific.ParkourRaceManager");
	}

	@Override
	public void onLoad() {
		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);
		TournamentSystem.getInstance().disableEliminationTitleMessage();

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			NovaParkourRace.getInstance().getGame().setupPlayerData(player);
		});

		NovaParkourRace.getInstance().getGame().addTimerDecrementCallback(new Callback() {
			@Override
			public void execute() {
				if (!TournamentSystem.getInstance().isDisableScoreboard()) {
					NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LEFT_LINE, ChatColor.GOLD + "Time left: " + ChatColor.AQUA + TextUtils.secondsToTime(NovaParkourRace.getInstance().getGame().getTimeLeft()));
				}
			}
		});

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					ParkourRace game = (ParkourRace) GameManager.getInstance().getActiveGame();

					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							PlayerData data = game.getPlayerData(player);
							if (data != null) {
								if (!TournamentSystem.getInstance().isDisableScoreboard()) {
									if (data.isCompleted()) {
										NetherBoardScoreboard.getInstance().setPlayerLine(LAP_LINE, player, ChatColor.GREEN + "" + ChatColor.BOLD + "Completed");
									} else {
										NetherBoardScoreboard.getInstance().setPlayerLine(LAP_LINE, player, ChatColor.AQUA + "" + ChatColor.BOLD + "Lap " + data.getLap());
									}
								}
							}
						});
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onParkourRacePlayerCompleteLap(ParkourRacePlayerCompleteLapEvent e) {
		if(ParkourRaceManagerConfig.DisableTournamentSystemScoreSystem) {
			return;
		}
		e.getPlayer().sendMessage(ChatColor.GRAY + "+10 points");
		ScoreManager.getInstance().addPlayerScore(e.getPlayer(), 10, true);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onParkourRacePlayerComplete(ParkourRacePlayerCompleteEvent e) {
		if(ParkourRaceManagerConfig.DisableTournamentSystemScoreSystem) {
			return;
		}
		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if (e.getPlacement() <= winScore.length) {
			int score = winScore[e.getPlacement() - 1];
			e.getPlayer().sendMessage(ChatColor.GRAY + "+" + score + " points");
			ScoreManager.getInstance().addPlayerScore(e.getPlayer(), score, true);
		}
	}
}