package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.hive;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.messages.PlayerEliminationMessage;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class HiveEliminationMessages implements PlayerEliminationMessage {
	@Override
	public void showPlayerEliminatedMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
		ChatColor color = ChatColor.AQUA;
		if (TeamManager.hasTeamManager()) {
			Team team = TeamManager.getTeamManager().getPlayerTeam(player.getUniqueId());
			if (team != null) {
				color = team.getTeamColor();
			}
		}

		switch (reason) {
		case COMMAND:
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player Eliminated> " + color + ChatColor.BOLD + player.getName() + ChatColor.RED + ChatColor.BOLD + " was removed from the game by an admin");
			break;

		case DID_NOT_RECONNECT:
			Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD + "Player Eliminated> " + color + ChatColor.BOLD + player.getName() + ChatColor.RED + ChatColor.BOLD + " did not reconnect in time");
			break;

		default:
			break;
		}
	}
}