package net.novauniverse.mctournamentsystem.spigot.game.util;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.abstraction.VersionIndependentUtils;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.events.PlayerEliminatedEvent;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class DefaultPlayerEliminatedTitleProvider implements PlayerEliminatedTitleProvider {
	@Override
	public void show(PlayerEliminatedEvent e) {
		if (e.getPlayer().isOnline()) {
			Player player = e.getPlayer().getPlayer();

			String subtitle = ChatColor.RED + TextUtils.ordinal(e.getPlacement() + 1) + " place";

			Entity killerEntity = null;
			if (e.getKiller() != null) {
				if (e.getKiller() instanceof Projectile) {
					Entity theBoiWhoFirered = (Entity) ((Projectile) e.getKiller()).getShooter();

					if (theBoiWhoFirered != null) {
						killerEntity = theBoiWhoFirered;
					} else {
						killerEntity = e.getKiller();
					}
				} else {
					killerEntity = e.getKiller();
				}
			}

			ChatColor killerColor = ChatColor.RED;
			Team killerTeam = null;
			if (killerEntity != null) {
				if (killerEntity instanceof Player) {
					killerTeam = TeamManager.getTeamManager().getPlayerTeam((Player) killerEntity);
				}
			}

			if (killerTeam != null) {
				killerColor = killerTeam.getTeamColor();
			}

			switch (e.getReason()) {
			case KILLED:
				subtitle = ChatColor.RED + "Killed by " + killerColor + killerEntity.getName() + ChatColor.RED + ". " + TextUtils.ordinal(e.getPlacement() + 1) + " place";
				break;

			case COMMAND:
				subtitle = ChatColor.RED + "Eliminated by an admin";
				break;

			default:
				break;
			}

			VersionIndependentUtils.get().sendTitle(player, ChatColor.RED + "Eliminated", subtitle, 10, 60, 10);
		}
	}
}