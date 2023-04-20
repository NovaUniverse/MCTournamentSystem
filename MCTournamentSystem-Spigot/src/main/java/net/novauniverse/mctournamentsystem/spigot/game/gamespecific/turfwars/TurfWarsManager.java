package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.turfwars;

import org.bukkit.Bukkit;
import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.game.turfwars.TurfWarsPlugin;
import net.novauniverse.game.turfwars.game.TurfWars;
import net.novauniverse.game.turfwars.game.team.TurfWarsTeam;
import net.novauniverse.game.turfwars.game.team.TurfWarsTeamData;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.module.ModuleManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class TurfWarsManager extends NovaModule implements Listener {
	public static int BUILD_TIME_LINE = 6;
	public static int SCORE_LINE = 7;

	private Task updateTask;

	private GameManager gameManager;

	public TurfWarsManager() {
		super("TournamentSystem.TurfWarsManager");
	}

	@Override
	public void onLoad() {
		TurfWarsPlugin.Companion.getInstance().setTurfWarsTeamPopulator(new TournamentSystemTurfWarsTeamPopulator());

		gameManager = ModuleManager.getModule(GameManager.class);

		updateTask = new SimpleTask(getPlugin(), () -> {
			if (gameManager.hasGame()) {
				TurfWars game = (TurfWars) gameManager.getActiveGame();

				DelayedGameTrigger buildEndTrigger = game.getBuildTimeEndTrigger();
				DelayedGameTrigger buildStartTrigger = game.getBuildTimeStartTrigger();

				if (buildEndTrigger.isRunning()) {
					NetherBoardScoreboard.getInstance().setGlobalLine(BUILD_TIME_LINE, ChatColor.GOLD + "Build ends in: " + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(buildEndTrigger.getTicksLeft() / 20));
				} else if (buildStartTrigger.isRunning()) {
					NetherBoardScoreboard.getInstance().setGlobalLine(BUILD_TIME_LINE, ChatColor.GOLD + "Build starts in: " + ChatColor.AQUA + TextUtils.secondsToMinutesSeconds(buildStartTrigger.getTicksLeft() / 20));
				}
				Bukkit.getOnlinePlayers().forEach(player -> {
					TurfWarsTeamData first = game.getTeam1();
					TurfWarsTeamData second = game.getTeam2();

					if (game.isPlayerInGame(player)) {
						TurfWarsTeamData playerTeam = game.getTeam(player);
						if (playerTeam != null) {
							if (playerTeam.getTeam() == TurfWarsTeam.TEAM_2) {
								first = game.getTeam2();
								second = game.getTeam1();
							}
						}
					}

					int diff = first.getKills() - second.getKills();

					int firstTurf = (game.getTurfSize() / 2) + diff;
					int secondTurf = (game.getTurfSize() / 2) - diff;

					if (game.isPlayerInGame(player)) {
						NetherBoardScoreboard.getInstance().setPlayerLine(SCORE_LINE, player, ChatColor.GREEN + ChatColor.BOLD.toString() + "(You) " + firstTurf + ChatColor.WHITE + " vs " + ChatColor.RED + ChatColor.BOLD.toString() + secondTurf + " (Enemy)");
					} else {
						NetherBoardScoreboard.getInstance().setPlayerLine(SCORE_LINE, player, first.getTeamConfig().getChatColor() + ChatColor.BOLD.toString() + "team 1 " + firstTurf + ChatColor.WHITE + " vs " + second.getTeamConfig().getChatColor() + ChatColor.BOLD.toString() + secondTurf + " team 2");
					}
				});
			}
		}, 10L);
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStartTask(updateTask);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(updateTask);
	}
}