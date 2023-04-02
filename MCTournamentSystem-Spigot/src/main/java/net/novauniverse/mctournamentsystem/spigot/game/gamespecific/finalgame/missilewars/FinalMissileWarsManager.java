package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.finalgame.missilewars;

import org.bukkit.event.Listener;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.games.finalgame.missilewars.NovaFinalMissileWars;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.DelayedGameTrigger;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.triggers.RepeatingGameTrigger;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class FinalMissileWarsManager extends NovaModule implements Listener {
	private Task task;

	public static int LOOT_COUNTDOWN_LINE = 6;
	public static int SUDDEN_DEATH_COUNTDOWN_LINE = 7;
	private boolean lootCountdownShown;
	private boolean suddenDeathCountdownShown;

	public FinalMissileWarsManager() {
		super("TournamentSystem.GameSpecific.FinalMissileWarsManager");
	}

	@Override
	public void onLoad() {
		lootCountdownShown = false;
		NovaFinalMissileWars.getInstance().setFinalGameTeamProvider(new FinalMissileWarsTeamPairProvider());
		task = new SimpleTask(TournamentSystem.getInstance(), () -> {
			if (GameManager.getInstance().hasGame()) {
				if (LOOT_COUNTDOWN_LINE > -1) {
					RepeatingGameTrigger lootTrigger = (RepeatingGameTrigger) NovaFinalMissileWars.getInstance().getGame().getLootTrigger();

					if (lootTrigger.isRunning()) {
						lootCountdownShown = true;
						NetherBoardScoreboard.getInstance().setGlobalLine(LOOT_COUNTDOWN_LINE, ChatColor.GOLD + "New item in: " + ChatColor.AQUA + ((int) Math.ceil((double) lootTrigger.getTicksLeft() / 20D)));
					} else if (lootCountdownShown) {
						NetherBoardScoreboard.getInstance().clearGlobalLine(LOOT_COUNTDOWN_LINE);
						lootCountdownShown = false;
					}
				}

				if (SUDDEN_DEATH_COUNTDOWN_LINE > -1) {
					DelayedGameTrigger suddenDeathTrigger = (DelayedGameTrigger) NovaFinalMissileWars.getInstance().getGame().getSuddenDeathTrigger();

					if (suddenDeathTrigger.isRunning()) {
						suddenDeathCountdownShown = true;
						NetherBoardScoreboard.getInstance().setGlobalLine(SUDDEN_DEATH_COUNTDOWN_LINE, ChatColor.GOLD + "Sudden death: " + ChatColor.AQUA + TextUtils.secondsToTime(((int) (suddenDeathTrigger.getTicksLeft() / 20))));
					} else if (suddenDeathCountdownShown) {
						NetherBoardScoreboard.getInstance().clearGlobalLine(SUDDEN_DEATH_COUNTDOWN_LINE);
						suddenDeathCountdownShown = false;
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
		Task.tryStartTask(task);
	}
}