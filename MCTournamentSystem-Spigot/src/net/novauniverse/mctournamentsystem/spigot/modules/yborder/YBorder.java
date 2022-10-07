package net.novauniverse.mctournamentsystem.spigot.modules.yborder;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NetherBoardScoreboard;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = false)
public class YBorder extends NovaModule implements Listener {
	private int yLimit;
	private Task decreaseTask;
	private Task damageTask;

	private List<Player> aboveLimit;

	private boolean color;

	public static final int SCOREBOARD_LINE = 7;

	private boolean paused;

	public YBorder() {
		super("TournamentSystem.YBorder");
	}
	
	public boolean isPaused() {
		return paused;
	}
	
	public void setPaused(boolean paused) {
		this.paused = paused;
	}

	@Override
	public void onLoad() {
		yLimit = 255;

		decreaseTask = null;
		damageTask = null;

		aboveLimit = new ArrayList<Player>();

		paused = false;

		color = false;
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStopTask(decreaseTask);
		Task.tryStopTask(damageTask);

		paused = false;

		decreaseTask = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				if (yLimit > 0) {
					if (!paused) {
						yLimit--;
					}
				}

				color = !color;
				showLimit();
			}
		}, 20L, 20L);

		damageTask = new SimpleTask(TournamentSystem.getInstance(), new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					boolean showMessage = false;

					if (NovaCore.isNovaGameEngineEnabled()) {
						if (GameManager.getInstance().isInGame(player)) {
							showMessage = true;
						}
					}

					if (showMessage) {
						if (player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
							if (player.getLocation().getY() > yLimit) {
								if (player.getHealth() > 0) {
									player.damage(1);

									if (!aboveLimit.contains(player)) {
										aboveLimit.add(player);
										player.sendMessage(LanguageManager.getString(player, "tournamentsystem.yborder.above_limit.warning", "" + yLimit));
									}

									String message = LanguageManager.getString(player, "tournamentsystem.yborder.above_limit.info", (player.getLocation().getBlockY() - yLimit) + "");

									if (message.length() <= 40) {
										VersionIndependentUtils.get().sendActionBarMessage(player, message);
									}
								} else {
									if (aboveLimit.contains(player)) {
										aboveLimit.remove(player);
									}
								}
							}
						}
					}
				});
			}
		}, 20L, 20L);

		decreaseTask.start();
		damageTask.start();

		LanguageManager.broadcast("tournamentsystem.yborder.start");

		showLimit();
	}

	@Override
	public void onDisable() throws Exception {
		LanguageManager.broadcast("tournamentsystem.yborder.remove");
		Task.tryStopTask(decreaseTask);
		Task.tryStopTask(damageTask);
		if (!TournamentSystem.getInstance().isDisableScoreboard()) {
			NetherBoardScoreboard.getInstance().clearGlobalLine(SCOREBOARD_LINE);
		}
	}

	private void showLimit() {
		if (!TournamentSystem.getInstance().isDisableScoreboard()) {
			NetherBoardScoreboard.getInstance().setGlobalLine(SCOREBOARD_LINE, (color ? ChatColor.RED : ChatColor.YELLOW) + TextUtils.ICON_WARNING + ChatColor.RED + " Height limit Y: " + ChatColor.AQUA + yLimit + " " + (color ? ChatColor.RED : ChatColor.YELLOW) + TextUtils.ICON_WARNING);
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (aboveLimit.contains(e.getPlayer())) {
			aboveLimit.remove(e.getPlayer());
		}
	}
}