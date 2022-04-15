package net.novauniverse.mctournamentsystem.spigot.modules.autorespawn;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;

import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.annotations.NovaAutoLoad;

@NovaAutoLoad(shouldEnable = true)
public class AutoRespawnModule extends NovaModule implements Listener {
	public AutoRespawnModule() {
		super("TournamentSystem.AutoRespawn");
	}
	
	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent e) {
		e.getPlayer().spigot().respawn();
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerDeath(PlayerDeathEvent e) {
		e.getEntity().spigot().respawn();
	}
}
