package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.hive;

import org.bukkit.ChatColor;
import org.bukkit.event.Listener;
import net.novauniverse.games.hive.game.Hive;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.game.GameSetup;
import net.novauniverse.mctournamentsystem.spigot.modules.head.PlayerHeadDrop;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class HiveManager extends NovaModule implements Listener {
	public static final int TIME_LINE = 5;

	private Task task;

	public HiveManager() {
		super("TournamentSystem.GameSpecific.HiveManager");
	}

	@Override
	public void onLoad() {
		GameSetup.disableEliminationMessages();
		GameManager.getInstance().setPlayerEliminationMessage(new HiveEliminationMessages());
		TournamentSystem.getInstance().setBuiltInScoreSystemDisabled(true);

		ModuleManager.disable(PlayerHeadDrop.class);

		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					Hive game = (Hive) GameManager.getInstance().getActiveGame();

					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						NetherBoardScoreboard.getInstance().setGlobalLine(TIME_LINE, ChatColor.GOLD + "Time left: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getTimeLeft()));
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