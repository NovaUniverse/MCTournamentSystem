package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.parkourrace;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.json.JSONObject;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.parkourrace.NovaParkourRace;
import net.novauniverse.games.parkourrace.game.ParkourRace;
import net.novauniverse.games.parkourrace.game.data.PlayerData;
import net.novauniverse.games.parkourrace.game.event.ParkourRacePlayerCompleteEvent;
import net.novauniverse.games.parkourrace.game.event.ParkourRacePlayerCompleteLapEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.Callback;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.GameStartEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class ParkourRaceManager extends NovaModule implements Listener {
	public static int TIME_LEFT_LINE = 5;
	public static int LAP_LINE = 6;

	public static String LINE_PREFIX = ChatColor.GOLD.toString();
	public static String LAP_PREFIX = ChatColor.AQUA.toString();
	public static String COMPLETED_PREFIX = ChatColor.GREEN.toString();

	public static int LAP_COMPLETED_SCORE = 0;

	private Task task;

	private GameManager gameManager;

	public ParkourRaceManager() {
		super("TournamentSystem.GameSpecific.ParkourRaceManager");
	}

	@Override
	public void onLoad() {
		JSONObject scoreConfig = TournamentSystem.getInstance().getGameSpecificScoreSettings().optJSONObject("parkourrace");
		if (scoreConfig != null) {
			if (scoreConfig.has("lap_completed_score")) {
				LAP_COMPLETED_SCORE = scoreConfig.getInt("lap_completed_score");
				Log.info(getName(), "Setting lap completed score to " + LAP_COMPLETED_SCORE);
			}
		}
		
		gameManager = GameManager.getInstance();

		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);
		TournamentSystem.getInstance().disableEliminationTitleMessage();

		TournamentSystem.getInstance().addRespawnPlayerCallback(player -> {
			NovaParkourRace.getInstance().getGame().setupPlayerData(player);
		});

		NovaParkourRace.getInstance().getGame().addTimerDecrementCallback(new Callback() {
			@Override
			public void execute() {
				if (!TournamentSystem.getInstance().isDisableScoreboard()) {
					updateTimeLeftLine();
				}
			}
		});

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (gameManager.hasGame()) {
					ParkourRace game = (ParkourRace) GameManager.getInstance().getActiveGame();

					if (gameManager.getActiveGame().hasStarted()) {
						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							PlayerData data = game.getPlayerData(player);
							if (data != null) {
								if (!TournamentSystem.getInstance().isDisableScoreboard()) {
									if (data.isCompleted()) {
										NovaScoreboardManager.getInstance().setPlayerLine(player, LAP_LINE, new StaticTextLine(COMPLETED_PREFIX + "Completed"));
									} else {
										NovaScoreboardManager.getInstance().setPlayerLine(player, LAP_LINE, new StaticTextLine(LAP_PREFIX + "Lap " + data.getLap()));
									}
								}
							}
						});
					}
				}
			}
		}, 5L);
	}

	private void updateTimeLeftLine() {
		NovaScoreboardManager.getInstance().setGlobalLine(TIME_LEFT_LINE, new StaticTextLine(LINE_PREFIX + "Time left: " + ChatColor.WHITE + TextUtils.secondsToTime(NovaParkourRace.getInstance().getGame().getTimeLeft())));
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
	public void onGameStart(GameStartEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				NovaScoreboardManager.getInstance().setGlobalLine(TIME_LEFT_LINE, new StaticTextLine(LINE_PREFIX + "Time left: " + ChatColor.WHITE + TextUtils.secondsToTime(NovaParkourRace.getInstance().getGame().getTimeLeft())));
			}
		}.runTaskLater(getPlugin(), 1L);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onParkourRacePlayerCompleteLap(ParkourRacePlayerCompleteLapEvent e) {
		if (ParkourRaceManagerConfig.DisableTournamentSystemScoreSystem) {
			return;
		}
		if (LAP_COMPLETED_SCORE > 0) {
			e.getPlayer().sendMessage(ChatColor.GRAY + "+" + LAP_COMPLETED_SCORE + " points");
			ScoreManager.getInstance().addPlayerScore(e.getPlayer(), LAP_COMPLETED_SCORE, true, "Parkour race lap completion score");
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onParkourRacePlayerComplete(ParkourRacePlayerCompleteEvent e) {
		if (ParkourRaceManagerConfig.DisableTournamentSystemScoreSystem) {
			return;
		}
		int[] winScore = TournamentSystem.getInstance().getWinScore();
		if (e.getPlacement() <= winScore.length) {
			int score = winScore[e.getPlacement() - 1];
			e.getPlayer().sendMessage(ChatColor.GRAY + "+" + score + " points");
			ScoreManager.getInstance().addPlayerScore(e.getPlayer(), score, true, "Parkour race full completion score");
		}
	}
}