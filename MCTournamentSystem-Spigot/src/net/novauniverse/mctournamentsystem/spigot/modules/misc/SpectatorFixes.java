package net.novauniverse.mctournamentsystem.spigot.modules.misc;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.WorldBorder;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.tasks.Task;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;
import net.zeeraa.novacore.spigot.tasks.SimpleTask;

@NovaAutoLoad(shouldEnable = true)
public class SpectatorFixes extends NovaModule implements Listener {
	private static final double BORDER_DISTANCE_BEFORE_TELEPORT_BACK = 10;

	private Task task;

	@Override
	public String getName() {
		return "ts.spectatorfixes";
	}

	@Override
	public void onEnable() throws Exception {
		this.task = new SimpleTask(new Runnable() {
			@Override
			public void run() {
				Bukkit.getServer().getOnlinePlayers().forEach(player -> {
					if (player.getGameMode() == GameMode.SPECTATOR) {
						// Prevent spectators from entering the void
						if (player.getLocation().getBlockY() < -5) {
							Location goodLocation = player.getLocation().clone();
							goodLocation.setY(0);
							player.teleport(goodLocation);
						}

						// Prevent spectators from escaping the world border
						WorldBorder border = player.getWorld().getWorldBorder();
						Location borderCenter = border.getCenter();

						double xDist = getDistance(player.getLocation().getX(), borderCenter.getX());
						double zDist = getDistance(player.getLocation().getZ(), borderCenter.getZ());

						final double tpDist = (border.getSize() / 2) + BORDER_DISTANCE_BEFORE_TELEPORT_BACK;

						Log.trace("BorderTest", "xDist: " + xDist + " zDist: " + zDist + " border: " + border.getSize() + " tpAt: " + tpDist);

						if (xDist > tpDist) {
							Location location = player.getLocation().clone();
							location.setX((border.getSize() * (location.getX() > 0 ? 1 : -1)) / 2);
							player.teleport(location);
						}

						if (zDist > tpDist) {
							Location location = player.getLocation().clone();
							location.setZ((border.getSize() * (location.getZ() > 0 ? 1 : -1)) / 2);
							player.teleport(location);
						}
					}
				});
			}
		}, 5L);
		Task.tryStartTask(task);
	}

	@Override
	public void onDisable() throws Exception {
		Task.tryStopTask(task);
	}

	public double getDistance(double arg1, double arg2) {
		if (arg1 <= arg2) {
			return (arg2 - arg1);
		}
		if (arg1 >= arg2) {
			return (arg1 - arg2);
		}
		return 0;
	}

	@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
	public void onPlayerDamage(EntityDamageEvent e) {
		if (e.getEntity() instanceof Player) {
			Player player = (Player) e.getEntity();

			if (player.getGameMode() == GameMode.SPECTATOR) {
				if (e.getCause() == DamageCause.VOID) {
					e.setCancelled(true);
				}
			}
		}
	}
}