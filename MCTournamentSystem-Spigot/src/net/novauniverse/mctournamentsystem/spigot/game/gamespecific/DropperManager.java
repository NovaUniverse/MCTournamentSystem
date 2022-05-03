package net.novauniverse.mctournamentsystem.spigot.game.gamespecific;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import net.novauniverse.games.dropper.game.Dropper;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class DropperManager extends NovaModule implements Listener {
	public static final int TIME_LEFT_LINE = 5;
	public static final int DEATH_COUNT_LINE = 6;

	private Task task;
	private boolean timeLeftLineShown;

	public DropperManager() {
		super("TournamentSystem.DropperManager");
	}

	@Override
	public void onLoad() {
		timeLeftLineShown = false;

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				boolean didShow = false;
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted() && !GameManager.getInstance().getActiveGame().hasEnded()) {
						long totalSecs = ((Dropper) GameManager.getInstance().getActiveGame()).getTimeLeft();

						if (totalSecs > 0) {
							long hours = totalSecs / 3600;
							long minutes = (totalSecs % 3600) / 60;
							long seconds = totalSecs % 60;

							String timeString = String.format("%02d:%02d:%02d", hours, minutes, seconds);

							NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LEFT_LINE, ChatColor.GOLD + "Time left: " + ChatColor.AQUA + timeString);

							timeLeftLineShown = true;
							didShow = true;
						}
					}
				}

				if (!didShow && timeLeftLineShown) {
					NetherBoardScoreboard.getInstance().clearGlobalLine(TIME_LEFT_LINE);
					timeLeftLineShown = false;
				}

				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							int deaths = ((Dropper) GameManager.getInstance().getActiveGame()).getDeaths(player.getUniqueId());
							NetherBoardScoreboard.getInstance().setPlayerLine(DEATH_COUNT_LINE, player, ChatColor.GOLD + "Deaths: " + ChatColor.AQUA + deaths);
						});
					}
				}
			}
		}, 10L);
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