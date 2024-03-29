package net.novauniverse.mctournamentsystem.missilewars.lobby.gamestarter;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.countdown.DefaultGameCountdown;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class DefaultCountdownGameStarter extends GameStarter {
	private Task checkTask;

	public static final int START_TIME = 120;
	private int startTimeOverride = -1;
	private boolean enabled = false;

	@Override
	public String getName() {
		return "DefaultCountdownGameStarter";
	}

	@Override
	public void onEnable() {
		enabled = true;
		if (GameManager.getInstance().getCountdown() instanceof DefaultGameCountdown) {
			((DefaultGameCountdown) GameManager.getInstance().getCountdown()).setStartTime(startTimeOverride != -1 ? startTimeOverride : START_TIME);
		}

		checkTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					if (GameManager.getInstance().getActiveGame().hasStarted()) {
						disable();
						return;
					}
				}

				if (GameManager.getInstance().getCountdown().isCountdownRunning() && !GameManager.getInstance().getActiveGame().canStart()) {
					GameManager.getInstance().getCountdown().cancelCountdown();
				}

				int c = getGroupCount();

				if (c >= 2) {
					if (!GameManager.getInstance().getCountdown().isCountdownRunning()) {
						if (GameManager.getInstance().getActiveGame().canStart()) {
							Log.info(getName(), "Starting countdown since there are 2 or more players / teams online");
							if (!GameManager.getInstance().getCountdown().startCountdown()) {
								Log.warn("DefaultCountdownGameStarter", "GameCountdown#startCountdown() returned false. The server might be in an invalid game state and might require a restart");
							}
						}
					}
				} else {
					if (GameManager.getInstance().getCountdown().isCountdownRunning()) {
						GameManager.getInstance().getCountdown().cancelCountdown();
					}
				}
			}

		}, 5L);
		checkTask.start();
	}

	public void setStartTimeOverride(int startTimeOverride) {
		Log.info("DefaultCountdownGameStarter", ChatColor.AQUA + "startTimeOverride set to " + startTimeOverride);
		this.startTimeOverride = startTimeOverride;
		if (enabled) {
			((DefaultGameCountdown) GameManager.getInstance().getCountdown()).setStartTime(startTimeOverride);
		}
	}

	public int getStartTimeOverride() {
		return startTimeOverride;
	}

	private void disable() {
		enabled = false;
		Task.tryStopTask(checkTask);
	}

	private int getGroupCount() {
		if (GameManager.getInstance().hasGame()) {
			// Special case for missile wars
			if (GameManager.getInstance().getActiveGame().getName().equalsIgnoreCase("missilewars")) {
				return Bukkit.getServer().getOnlinePlayers().size();
			}

			// Special case for manhunt
			if (GameManager.getInstance().getActiveGame().getName().equalsIgnoreCase("manhunt")) {
				return Bukkit.getServer().getOnlinePlayers().size();
			}
		}

		if (GameManager.getInstance().isUseTeams()) {
			int result = 0;
			for (Team team : TeamManager.getTeamManager().getTeams()) {
				if (team.getMembers().size() > 0) {
					result++;
				}
			}

			return result;
		} else {
			return Bukkit.getServer().getOnlinePlayers().size();
		}
	}

	@Override
	public boolean shouldShowCountdown() {
		return GameManager.getInstance().getCountdown().isCountdownRunning();
	}

	@Override
	public long getTimeLeft() {
		return GameManager.getInstance().getCountdown().getTimeLeft();
	}
}