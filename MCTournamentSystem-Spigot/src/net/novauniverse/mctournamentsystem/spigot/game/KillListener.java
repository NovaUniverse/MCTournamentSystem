package net.novauniverse.mctournamentsystem.spigot.game;

import java.sql.PreparedStatement;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.module.NovaModule;
import net.zeeraa.novacore.spigot.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.utils.ProjectileUtils;

public class KillListener extends NovaModule implements Listener {
	@Override
	public String getName() {
		return "TSKillListener";
	}

	public KillListener() {
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
				killerPlayer.setLevel(killerPlayer.getLevel() + 1);

				try {
					String sql = "UPDATE players SET kills = kills + 1 WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, killerPlayer.getUniqueId().toString());
					ps.executeUpdate();
					ps.close();

					PlayerKillCache.getInstance().invalidate(killerPlayer);
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.error(getName(), "Failed to add kill to player " + killerPlayer.getName() + " (" + killerPlayer.getUniqueId().toString() + "). " + ex.getClass().getName() + " " + ex.getMessage());
				}
			}
		}
	}
}