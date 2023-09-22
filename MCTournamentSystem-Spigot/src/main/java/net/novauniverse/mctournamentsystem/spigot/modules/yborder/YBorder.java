package net.novauniverse.mctournamentsystem.spigot.modules.yborder;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
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
import net.zeeraa.novacore.spigot.abstraction.particle.NovaDustOptions;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.NovaScoreboardManager;
import net.zeeraa.novacore.spigot.module.modules.scoreboard.text.StaticTextLine;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = false)
public class YBorder extends NovaModule implements Listener {
	private int yLimit;
	private Task decreaseTask;
	private Task damageTask;

	private Task particleTask;

	private List<Player> aboveLimit;

	private boolean color;

	public static final int SCOREBOARD_LINE = 7;

	private boolean paused;

	private boolean useParticles;

	public static final double PARTICLE_WIDTH = 7;
	public static final double PARTICLE_DENSITY = 0.5;

	public static NovaDustOptions PARTICLE_COLOR = new NovaDustOptions(Color.RED);

	public YBorder() {
		super("TournamentSystem.YBorder");
	}

	@Override
	public void onLoad() {
		yLimit = 255;

		decreaseTask = null;
		damageTask = null;

		aboveLimit = new ArrayList<Player>();

		paused = false;

		color = false;

		useParticles = true;
	}

	@Override
	public void onEnable() throws Exception {
		Task.tryStopTask(decreaseTask);
		Task.tryStopTask(damageTask);
		Task.tryStopTask(particleTask);

		paused = false;

		decreaseTask = new SimpleTask(TournamentSystem.getInstance(), () -> {
			if (yLimit > 0) {
				if (!paused) {
					yLimit--;
				}
			}

			color = !color;
			showLimit();
		}, 20L, 20L);

		damageTask = new SimpleTask(TournamentSystem.getInstance(), () -> {
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
		}, 20L, 20L);

		particleTask = new SimpleTask(TournamentSystem.getInstance(), () -> {
			Bukkit.getServer().getOnlinePlayers().forEach(player -> {
				for (double x = 0; x <= PARTICLE_WIDTH; x += PARTICLE_DENSITY) {
					for (double z = 0; z <= PARTICLE_WIDTH; z += PARTICLE_DENSITY) {
						Location location = new Location(player.getWorld(), ((player.getLocation().getX()) - (PARTICLE_WIDTH / 2) + x), yLimit, ((player.getLocation().getZ() + z) - (PARTICLE_WIDTH / 2)));
						NovaCore.getInstance().getNovaParticleProvider().showColoredRedstoneParticle(location, PARTICLE_COLOR, player);
						// ParticleEffect.REDSTONE.display(location, Color.RED, player);
					}
				}
			});
		}, 10L);

		decreaseTask.start();
		damageTask.start();
		particleTask.start();

		LanguageManager.broadcast("tournamentsystem.yborder.start");

		showLimit();
	}

	@Override
	public void onDisable() throws Exception {
		LanguageManager.broadcast("tournamentsystem.yborder.remove");

		Task.tryStopTask(decreaseTask);
		Task.tryStopTask(damageTask);
		Task.tryStopTask(particleTask);

		if (!TournamentSystem.getInstance().isDisableScoreboard()) {
			NovaScoreboardManager.getInstance().clearGlobalLine(SCOREBOARD_LINE);
		}
	}

	private void showLimit() {
		if (!TournamentSystem.getInstance().isDisableScoreboard()) {
			NovaScoreboardManager.getInstance().setGlobalLine(SCOREBOARD_LINE, new StaticTextLine((color ? ChatColor.RED : ChatColor.YELLOW) + TextUtils.ICON_WARNING + ChatColor.RED + " Height limit Y: " + ChatColor.AQUA + yLimit + " " + (color ? ChatColor.RED : ChatColor.YELLOW) + TextUtils.ICON_WARNING));
		}
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerQuit(PlayerQuitEvent e) {
		if (aboveLimit.contains(e.getPlayer())) {
			aboveLimit.remove(e.getPlayer());
		}
	}

	public void reset() {
		yLimit = 255;
	}

	public void setyLimit(int yLimit) {
		this.yLimit = yLimit;
	}

	public int getyLimit() {
		return yLimit;
	}

	public boolean isUseParticles() {
		return useParticles;
	}

	public void setUseParticles(boolean useParticles) {
		this.useParticles = useParticles;
	}

	public boolean isPaused() {
		return paused;
	}

	public void setPaused(boolean paused) {
		this.paused = paused;
	}
}