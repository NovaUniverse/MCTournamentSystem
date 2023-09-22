package net.novauniverse.mctournamentsystem.missilewars.lobby.modules;

import org.bukkit.ChatColor;

import net.novauniverse.mctournamentsystem.missilewars.lobby.MissileWarsLobby;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class GameStartScoreboardCountdown extends NovaModule {
	private static GameStartScoreboardCountdown instance;

	private boolean countdownVisible;

	public static final int DEFAULT_COUNTDOWN_LINE = 1;

	private int countdownLine;
	
	public static GameStartScoreboardCountdown getInstance() {
		return instance;
	}

	private Task task;

	public GameStartScoreboardCountdown() {
		super("TournamentSystem.MissileWars.GameStartScoreboardCountdown");
	}

	@Override
	public void onLoad() {
		GameStartScoreboardCountdown.instance = this;
		countdownLine = DEFAULT_COUNTDOWN_LINE;
		countdownVisible = false;

		task = new SimpleTask(MissileWarsLobby.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (MissileWarsLobby.getInstance().getStarter().shouldShowCountdown()) {
					countdownVisible = true;
					NovaScoreboardManager.getInstance().setGlobalLine(countdownLine, new StaticTextLine(ChatColor.GOLD + "Starting in: " + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(MissileWarsLobby.getInstance().getStarter().getTimeLeft())));
				} else {
					if (countdownVisible) {
						NovaScoreboardManager.getInstance().clearGlobalLine(countdownLine);
						countdownVisible = false;
					}
				}
			}
		}, 10L);
	}

	public int getCountdownLine() {
		return countdownLine;
	}

	public void setCountdownLine(int countdownLine) {
		if (countdownVisible) {
			NovaScoreboardManager.getInstance().clearGlobalLine(countdownLine);
		}
		this.countdownLine = countdownLine;
	}

	@Override
	public void onEnable() throws Exception {
		task.start();
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}
}