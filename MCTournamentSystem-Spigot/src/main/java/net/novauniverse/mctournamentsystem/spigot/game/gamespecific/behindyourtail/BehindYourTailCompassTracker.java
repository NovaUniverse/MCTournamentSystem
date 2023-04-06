package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.behindyourtail;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import net.novauniverse.behindyourtail.NovaBehindYourTail;
import net.novauniverse.behindyourtail.game.role.Role;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.GameManager;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTarget;
import net.zeeraa.novacore.spigot.module.modules.compass.CompassTrackerTarget;
import net.zeeraa.novacore.spigot.teams.Team;

public class BehindYourTailCompassTracker implements CompassTrackerTarget {
	@Override
	public CompassTarget getCompassTarget(Player player) {
		if (GameManager.getInstance().hasGame()) {
			List<UUID> players = new ArrayList<UUID>(GameManager.getInstance().getActiveGame().getPlayers());

			// Ignore self
			players.remove(player.getUniqueId());

			double closestDistance = Double.MAX_VALUE;
			CompassTarget result = null;

			Team team = null;
			if (NovaCore.getInstance().hasTeamManager()) {
				team = TournamentSystem.getInstance().getTeamManager().getPlayerTeam(player);
			}

			for (UUID uuid : players) {
				Player target = Bukkit.getServer().getPlayer(uuid);

				if (target != null) {
					if (target.isOnline()) {
						if (GameManager.getInstance().hasGame()) {
							if (!GameManager.getInstance().getActiveGame().getPlayers().contains(target.getUniqueId())) {
								continue;
							}
						}
						if (target.getLocation().getWorld() == player.getLocation().getWorld()) {
							Role role = NovaBehindYourTail.getInstance().getGame().getPlayerRole(target.getUniqueId());

							if(role == Role.HUNTER) {
								continue;
							}
							
							if (team != null) {
								Team p2team = NovaCore.getInstance().getTeamManager().getPlayerTeam(target);

								if (p2team != null) {
									if (team.equals(p2team)) {
										continue;
									}
								}
							}

							double dist = player.getLocation().distance(target.getLocation());

							if (dist < closestDistance) {
								closestDistance = dist;
								result = new CompassTarget(target.getLocation(), LanguageManager.getString(player, "tournamentsystem.game.behindyourtail.item.fox_tracker.tracking_message", target.getName()));
							}
						}
					}
				}
			}

			return result;
		}
		return null;
	}
}