package net.novauniverse.mctournamentsystem.spigot.kills;

import java.sql.PreparedStatement;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
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
							PlayerKillCache.getInstance().tryIncrement(player.getUniqueId());
						}
					}.runTask(TournamentSystem.getInstance());
				} catch (Exception ex) {
					ex.printStackTrace();
					Log.error("KillManager", "Failed to add kill to player " + player.getName() + " (" + player.getUniqueId().toString() + "). " + ex.getClass().getName() + " " + ex.getMessage());
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}

	public static final void addTeamKill(TournamentSystemTeam team) {
		new BukkitRunnable() {
			@Override
			public void run() {
				try {
					String sql = "UPDATE teams SET kills = kills + 1 WHERE team_number = ?";
					PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

					ps.setInt(1, team.getTeamNumber());
					ps.executeUpdate();
					ps.close();

				} catch (Exception ex) {
					ex.printStackTrace();
					Log.error("KillManager", "Failed to add kill to team " + team.getDisplayName() + " (" + team.getTeamNumber() + "). " + ex.getClass().getName() + " " + ex.getMessage());
				}
			}
		}.runTaskAsynchronously(TournamentSystem.getInstance());
	}
}