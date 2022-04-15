package net.novauniverse.mctournamentsystem.spigot.eliminationmessage;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class TournamentSystemDefaultPlayerEliminationMessage implements ITournamentSystemPlayerEliminationMessageProvider {
	@Override
	public String getEliminationMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
		ChatColor playerColor = ChatColor.AQUA;
		ChatColor killerColor = ChatColor.RED;

		if (NovaCore.getInstance().getTeamManager() != null) {
			Team playerTeam = NovaCore.getInstance().getTeamManager().getPlayerTeam(player);
			if (playerTeam != null) {
				playerColor = playerTeam.getTeamColor();
			}
		}

		switch (reason) {
		case DEATH:
			return LanguageManager.getString("novacore.game.elimination.player.died", playerColor.toString(), player.getName());

		case COMBAT_LOGGING:
			LanguageManager.getString("novacore.game.elimination.player.combat_logging", playerColor.toString(), player.getName());
			break;

		case DID_NOT_RECONNECT:
			return LanguageManager.getString("novacore.game.elimination.player.did_not_reconnect", playerColor.toString(), player.getName());

		case COMMAND:
			return LanguageManager.getString("novacore.game.elimination.player.command", playerColor.toString(), player.getName());

		case KILLED:
			String killerName = "";
			Entity killerEntity = null;
			if (killer != null) {
				if (killer instanceof Projectile) {
					Entity theBoiWhoFirered = (Entity) ((Projectile) killer).getShooter();

					if (theBoiWhoFirered != null) {
						killerName = theBoiWhoFirered.getName();
						killerEntity = theBoiWhoFirered;
					} else {
						killerName = killer.getName();
						killerEntity = killer;
					}
				} else {
					killerName = killer.getName();
					killerEntity = killer;
				}
			}

			if (killerEntity instanceof Player) {
				Team killerTeam = TeamManager.getTeamManager().getPlayerTeam((Player) killerEntity);
				if(killerTeam != null) {
					killerColor = killerTeam.getTeamColor();
				}
			}

			return LanguageManager.getString("novacore.game.elimination.player.killed", playerColor.toString(), player.getName(), killerColor + killerName);

		case QUIT:
			return LanguageManager.getString("novacore.game.elimination.player.quit", playerColor.toString(), player.getName());

		default:
			return LanguageManager.getString("novacore.game.elimination.player.unknown", playerColor.toString(), player.getName());
		}

		return "ERR:UNKNOWN_ELIMINATION_REASON";
	}
}