package net.novauniverse.mctournamentsystem.spigot.score;

import java.util.UUID;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerNameCache;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.teams.Team;

public class PlayerScoreData extends ScoreData {
	private UUID uuid;

	public PlayerScoreData(UUID uuid, int score) {
		super(score);
		this.uuid = uuid;
	}

	public UUID getUuid() {
		return uuid;
	}

	@Override
	public String toString() {
		ChatColor color = ChatColor.AQUA;

		if (NovaCore.getInstance().hasTeamManager()) {
			Team team = NovaCore.getInstance().getTeamManager().getPlayerTeam(uuid);

			if (team != null) {
				color = team.getTeamColor();
			}
		}

		return color + PlayerNameCache.getInstance().getPlayerName(uuid) + ChatColor.GOLD + " : " + ChatColor.AQUA + this.getScore();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof PlayerScoreData) {
			return ((PlayerScoreData) obj).uuid.equals(uuid);
		}

		if (obj instanceof Player) {
			return ((Player) obj).getUniqueId().equals(uuid);
		}

		if (obj instanceof UUID) {
			return ((UUID) obj).equals(uuid);
		}

		return super.equals(obj);
	}
}