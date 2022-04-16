package net.novauniverse.mctournamentsystem.spigot.modules.autorespawn;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class AutoRespawnModule extends NovaModule implements Listener {
	private Map<UUID, Location> respawnLocation;

	public AutoRespawnModule() {
		super("TournamentSystem.AutoRespawn");
	}

	@Override
	public void onLoad() {
		respawnLocation = new HashMap<UUID, Location>();
	}

	@Override
	public void onEnable() {
		respawnLocation.clear();
	}

	@Override
	public void onDisable() {
		respawnLocation.clear();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		respawnLocation.remove(e.getPlayer().getUniqueId());
		e.getPlayer().spigot().respawn();
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerQuit(PlayerQuitEvent e) {
		respawnLocation.remove(e.getPlayer().getUniqueId());
	}

	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerDeath(PlayerDeathEvent e) {
		new BukkitRunnable() {
			@Override
			public void run() {
				Location playerLocation = e.getEntity().getLocation();
				respawnLocation.put(e.getEntity().getUniqueId(), playerLocation);
				e.getEntity().spigot().respawn();
			}
		}.runTaskLater(TournamentSystem.getInstance(), 2L);
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerRespawn(PlayerRespawnEvent e) {
		if (respawnLocation.containsKey(e.getPlayer().getUniqueId())) {
			e.setRespawnLocation(respawnLocation.get(e.getPlayer().getUniqueId()));
			respawnLocation.remove(e.getPlayer().getUniqueId());
		}
	}
}