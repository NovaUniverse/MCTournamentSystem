package net.novauniverse.mctournamentsystem.spigot.kills;

import java.sql.PreparedStatement;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.zeeraa.novacore.commons.log.Log;

public class KillManager {
	public static final void addPlayerKill(Player player) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "UPDATE players SET kills = kills + 1 WHERE uuid = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setString(1, player.getUniqueId().toString());
					ps.executeUpdate();
					ps.close();

					new BukkitRunnable() {
						@Override
						public void run() {
							PlayerKillCache.getInstance().invalidate(player);
						}
					}.runTask(TournamentSystem.getInstance());
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.error("KillManager", "Failed to add kill to player " + player.getName() + " (" + player.getUniqueId().toString() + "). " + ex.getClass().getName() + " " + ex.getMessage());
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}
}