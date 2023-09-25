package net.novauniverse.mctournamentsystem.missilewars.lobby.modules;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.Listener;

import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.RepeatingGameTrigger;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class MissileWarsHandler extends NovaModule implements Listener {
	private Task updateTask;

	public static final int TEAM_LINE = 0;
	public static final int LOOT_COUNTDOWN_LINE = 1;
	private boolean lootCountdownShown;

	public MissileWarsHandler() {
		super("TournamentSystem.MissileWars.MissileWarsHandler");
	}

	@Override
	public void onLoad() {
		lootCountdownShown = false;
		updateTask = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				if (GameManager.getInstance().hasGame()) {
					RepeatingGameTrigger lootTrigger = (RepeatingGameTrigger) GameManager.getInstance().getActiveGame().getTrigger("missilewars.loot");

					if (lootTrigger != null) {
						if (lootTrigger.isRunning()) {
							lootCountdownShown = true;
							NovaScoreboardManager.getInstance().setGlobalLine(LOOT_COUNTDOWN_LINE, new StaticTextLine(ChatColor.GOLD + "New item in: " + ChatColor.AQUA + ((int) (lootTrigger.getTicksLeft() / 20))));
						} else {
							if (lootCountdownShown) {
								NovaScoreboardManager.getInstance().clearGlobalLine(LOOT_COUNTDOWN_LINE);
								lootCountdownShown = false;
							}
						}
					}
				}

				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					Team team = TeamManager.getTeamManager().getPlayerTeam(player);

					if (team == null) {
						NovaScoreboardManager.getInstance().setPlayerLine(player, TEAM_LINE, new StaticTextLine(ChatColor.GRAY + "No team"));
					} else {
						NovaScoreboardManager.getInstance().setPlayerLine(player, TEAM_LINE, new StaticTextLine(team.getDisplayName() + " team"));
					}
				});
			}
		}, 5L);
	}

	@Override
	public void onEnable() throws Exception {
		updateTask.start();

		GameStartScoreboardCountdown.getInstance().setCountdownLine(2);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}
}