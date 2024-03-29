package net.novauniverse.mctournamentsystem.spigot.eliminationmessage;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;

import net.md_5.bungee.api.ChatColor;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.gameengine.module.modules.game.elimination.PlayerEliminationReason;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.teams.Team;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class TournamentSystemDefaultPlayerEliminationMessage implements ITournamentSystemPlayerEliminationMessageProvider {
	private Map<PlayerEliminationReason, CustomDefaultPlayerEliminationMessaageProvider> customProviders;

	public TournamentSystemDefaultPlayerEliminationMessage() {
		this.customProviders = new HashMap<>();
	}

	public void addCustomProvider(PlayerEliminationReason reason, CustomDefaultPlayerEliminationMessaageProvider provider) {
		this.customProviders.put(reason, provider);
	}

	public Map<PlayerEliminationReason, CustomDefaultPlayerEliminationMessaageProvider> getCustomProviders() {
		return customProviders;
	}

	@Override
	public String getEliminationMessage(OfflinePlayer player, Entity killer, PlayerEliminationReason reason, int placement) {
		if (customProviders.containsKey(reason)) {
			return customProviders.get(reason).getEliminationMessage(player, killer, reason, placement);
		}

		ChatColor playerColor = ChatColor.AQUA;
		ChatColor killerColor = ChatColor.RED;

		if (NovaCore.getInstance().getTeamManager() != null) {
			Team playerTeam = NovaCore.getInstance().getTeamManager().getPlayerTeam(player);
			if (playerTeam != null) {
				playerColor = playerTeam.getTeamColor();
			}
		}

		Log.trace("TournamentSystemDefaultPlayerEliminationMessage", "Elimination reason: " + reason.name());

		switch (reason) {
		case DEATH:
			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.died", playerColor.toString(), player.getName());

		case COMBAT_LOGGING:
			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.combat_logging", playerColor.toString(), player.getName());

		case DID_NOT_RECONNECT:
			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.did_not_reconnect", playerColor.toString(), player.getName());

		case COMMAND:
			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.command", playerColor.toString(), player.getName());

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
				if (killerTeam != null) {
					killerColor = killerTeam.getTeamColor();
				}
			}

			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.killed", playerColor.toString(), player.getName(), killerColor + "" + ChatColor.BOLD + killerName);

		case QUIT:
			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.quit", playerColor.toString(), player.getName());

		default:
			Log.error("TournamentSystemDefaultPlayerEliminationMessage", "ERR:UNKNOWN_ELIMINATION_REASON " + reason.name());
			return LanguageManager.getString(LanguageManager.getPrimaryLanguage(), "novacore.game.elimination.player.unknown", playerColor.toString(), player.getName());
		}
	}
}