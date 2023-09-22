package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.spleef;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import net.novauniverse.games.spleef.game.Spleef;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;

public class SpleefManager extends NovaModule implements Listener {
	public SpleefManager() {
		super("TournamentSystem.GameSpecific.SpleefManager");
	}

	public static int SPLEEF_DECAY_LINE = 5;

	private int taskId;
	private boolean decayLineShown;

	private GameManager gameManager;

	@Override
	public void onLoad() {
		this.taskId = -1;
		this.decayLineShown = false;
		gameManager = GameManager.getInstance();
	}

	@Override
	public void onEnable() {
		if (taskId == -1) {
			taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(TournamentSystem.getInstance(), new Runnable() {
				@Override
				public void run() {
					Spleef spleef = (Spleef) gameManager.getActiveGame();

					if (spleef.hasStarted()) {
						if (spleef.hasActiveMap()) {
							if (spleef.getDecayModule() != null) {
								if (!TournamentSystem.getInstance().isDisableScoreboard()) {
									if (spleef.getDecayModule().getStartTrigger().isRunning()) {
										decayLineShown = true;

										int timeLeft = (int) spleef.getDecayModule().getStartTrigger().getTicksLeft() / 20;

										ChatColor color;

										if (timeLeft < (spleef.getDecayModule().getBeginAfter() / 3)) {
											color = ChatColor.RED;
										} else if (timeLeft < (spleef.getDecayModule().getBeginAfter() / 2)) {
											color = ChatColor.YELLOW;
										} else {
											color = ChatColor.GREEN;
										}

										NovaScoreboardManager.getInstance().setGlobalLine(SPLEEF_DECAY_LINE, new StaticTextLine(ChatColor.GOLD + "Decay in: " + color + TextUtils.secondsToMinutesSeconds(timeLeft)));
									} else {
										if (decayLineShown) {
											NovaScoreboardManager.getInstance().clearGlobalLine(SPLEEF_DECAY_LINE);
											decayLineShown = false;
										}
									}
								}
							}
						}
					}
				}
			}, 5L, 5L);
		}
	}

	@Override
	public void onDisable() {
		if (taskId != -1) {
			Bukkit.getScheduler().cancelTask(taskId);
			taskId = -1;
		}
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = false)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().setFireTicks(0);
	}
}