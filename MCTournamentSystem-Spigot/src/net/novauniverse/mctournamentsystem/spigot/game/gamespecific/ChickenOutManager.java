package net.novauniverse.mctournamentsystem.spigot.game.gamespecific;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import net.novauniverse.games.chickenout.game.ChickenOut;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class ChickenOutManager extends NovaModule implements Listener {
	public static final int TIME_LINE = 5;
	public static final int FEATHER_COUNT_LINE = 6;
	public static final int FINAL_FEATHER_COUNT_LINE = 7;

	private Task task;

	public ChickenOutManager() {
		super("TournamentSystem.ChickenOutManager");
	}

	@Override
	public void onLoad() {
		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					ChickenOut game = (ChickenOut) GameManager.getInstance().getActiveGame();

					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						String timeLeftLine = null;
						if (game.isRoundTimerRunning()) {
							timeLeftLine = ChatColor.GOLD + "Level up in: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getRoundTimeLeft());
						} else if (game.isFinalTimerRunning()) {
							timeLeftLine = ChatColor.GOLD + "Game ends in: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getFinalTimeLeft());
						}

						if (timeLeftLine == null) {
							NetherBoardScoreboard.getInstance().clearGlobalLine(TIME_LINE);
						} else {
							NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LINE, timeLeftLine);
						}

						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							if (GameManager.getInstance().getActiveGame().getPlayers().contains(player.getUniqueId())) {
								int feathers = game.getPlayerFeathers(player);
								int finalFeathers = game.getFinalScoreForDisplay(player.getUniqueId());
								String featherCountMessage = ChatColor.GOLD + "Feathers: " + ChatColor.AQUA + feathers;
								String finalFeatherCount = ChatColor.GOLD + "Stashed feathers: " + ChatColor.AQUA + finalFeathers;
								NetherBoardScoreboard.getInstance().setPlayerLine(FEATHER_COUNT_LINE, player, featherCountMessage);
								NetherBoardScoreboard.getInstance().setPlayerLine(FINAL_FEATHER_COUNT_LINE, player, finalFeatherCount);
							} else {
								NetherBoardScoreboard.getInstance().clearPlayerLine(FEATHER_COUNT_LINE, player);
								NetherBoardScoreboard.getInstance().clearPlayerLine(FINAL_FEATHER_COUNT_LINE, player);
							}
						});
					}
				}
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}