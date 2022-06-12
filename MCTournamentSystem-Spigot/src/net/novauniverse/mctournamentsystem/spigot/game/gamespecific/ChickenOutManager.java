package net.novauniverse.mctournamentsystem.spigot.game.gamespecific;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import net.novauniverse.games.chickenout.game.ChickenOut;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class ChickenOutManager extends NovaModule implements Listener {
	public static final int FEATHER_COUNT_LINE = 5;
	public static final int FINAL_FEATHER_COUNT_LINE = 6;

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
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						Bukkit.getServer().getOnlinePlayers().forEach(player -> {
							if (GameManager.getInstance().getActiveGame().getPlayers().contains(player.getUniqueId())) {
								int feathers = ((ChickenOut) GameManager.getInstance().getActiveGame()).getPlayerFeathers(player);
								int finalFeathers = ((ChickenOut) GameManager.getInstance().getActiveGame()).getFinalScoreForDisplay(player.getUniqueId());
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