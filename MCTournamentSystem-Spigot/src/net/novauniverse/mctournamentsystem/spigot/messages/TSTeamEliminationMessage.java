package net.novauniverse.mctournamentsystem.spigot.messages;

import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.utils.TextUtils;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.module.modules.game.messages.TeamEliminationMessage;
import net.zeeraa.novacore.spigot.teams.Team;

public class TSTeamEliminationMessage implements TeamEliminationMessage {
	@Override
	public void showTeamEliminatedMessage(Team team, int placement) {
		if (team instanceof TournamentSystemTeam) {
			TournamentSystemTeam mcfTeam = (TournamentSystemTeam) team;

			LanguageManager.broadcast("tournamentsystem.game.elimination.team.eliminated", mcfTeam.getTeamColor(), mcfTeam.getDisplayName(), TextUtils.ordinal(placement));
			// Bukkit.getServer().broadcastMessage(ChatColor.RED + "" + ChatColor.BOLD +
			// "Team Eliminated> " + mcfTeam.getTeamColor() + ChatColor.BOLD + ("Team " +
			// mcfTeam.getTeamNumber()) + ChatColor.GOLD + ChatColor.BOLD + " " +
			// TextUtils.ordinal(placement) + " place");
		}
	}
}