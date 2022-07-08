package net.novauniverse.mctournamentsystem.spigot.game;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import net.novauniverse.mctournamentsystem.spigot.kills.KillManager;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.utils.ProjectileUtils;

public class KillListener extends NovaModule implements Listener {
	public KillListener() {
		super("TournamentSystem.KillListener");

		this.addDependency(PlayerKillCache.class);
	}

	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onPlayerEliminated(PlayerEliminatedEvent e) {

		if (e.getPlayer().isOnline()) {
			Entity killer = e.getKiller();

			Player killerPlayer = null;

			if (ProjectileUtils.isProjectile(killer)) {
				Entity shooter = ProjectileUtils.getProjectileShooterEntity(killer);

				if (shooter != null) {
					if (shooter instanceof Player) {
						killerPlayer = (Player) shooter;
					}
				}
			} else if (killer instanceof Player) {
				killerPlayer = (Player) killer;
			}

			if (killerPlayer != null) {
				KillManager.addPlayerKill(killerPlayer);
			}
		}
	}
}