package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.bedwars;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import net.novauniverse.bedwars.NovaBedwars;
import net.novauniverse.bedwars.game.Bedwars;
import net.novauniverse.bedwars.game.config.GeneratorUpgrade;
import net.novauniverse.bedwars.game.events.BedDestructionEvent;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

public class BedwarsManager extends NovaModule implements Listener {
	public static final int BEDWARS_COUNTDOWN_LINE = 5;
	public static final int HAS_BED_LINE = 6;

	public static final int BED_DESTRUCTION_SCORE = 20;

	private GeneratorUpgradeSorter sorter = new GeneratorUpgradeSorter();
	private Task task;

	public BedwarsManager() {
		super("TournamentSystem.GameSpecific.BedwarsManager");
	}

	@Override
	public void onLoad() {
		task = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bedwars game = NovaBedwars.getInstance().getGame();
				if (game.hasStarted()) {
					GeneratorUpgrade nextUpgrade = game.getGeneratorUpgrades().stream().filter(u -> u.getTimeLeft() > 0).sorted(sorter).findFirst().orElse(null);
					if (nextUpgrade != null) {
						NetherBoardScoreboard.getInstance().setGlobalLine(BEDWARS_COUNTDOWN_LINE, ChatColor.GOLD + nextUpgrade.getName() + " in: " + ChatColor.AQUA + TextUtils.secondsToTime(nextUpgrade.getTimeLeft()));
					} else if (game.getBedDestructionTime() > 0) {
						NetherBoardScoreboard.getInstance().setGlobalLine(BEDWARS_COUNTDOWN_LINE, ChatColor.GOLD + "Bed destruction: " + ChatColor.AQUA + TextUtils.secondsToTime(game.getBedDestructionTime()));
					} else {
						NetherBoardScoreboard.getInstance().clearGlobalLine(BEDWARS_COUNTDOWN_LINE);
					}

					Bukkit.getServer().getOnlinePlayers().forEach(player -> {
						NetherBoardScoreboard.getInstance().setPlayerLine(HAS_BED_LINE, player, ChatColor.GOLD + "Has bed: " + (game.hasBed(player) ? ChatColor.GREEN + "Yes" : ChatColor.RED + "No"));
					});
				}
			}
		}, 10L);
	}

	@EventHandler
	public void onBedDestruction(BedDestructionEvent e) {
		Player player = e.getPlayer();
		player.sendMessage(ChatColor.GRAY + "Enemy bed destroyed. +" + BED_DESTRUCTION_SCORE + " points");
		ScoreManager.getInstance().addPlayerScore(player, BED_DESTRUCTION_SCORE, true);
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