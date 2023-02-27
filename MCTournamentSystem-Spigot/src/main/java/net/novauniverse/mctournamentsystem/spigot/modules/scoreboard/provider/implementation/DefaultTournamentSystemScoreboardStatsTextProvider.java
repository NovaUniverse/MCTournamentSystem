package net.novauniverse.mctournamentsystem.spigot.modules.scoreboard.provider.implementation;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import net.novauniverse.mctournamentsystem.spigot.modules.scoreboard.provider.ScoreboardStatsTextProvider;
import net.zeeraa.novacore.spigot.language.LanguageManager;
import net.zeeraa.novacore.spigot.teams.Team;

public class DefaultTournamentSystemScoreboardStatsTextProvider implements ScoreboardStatsTextProvider {
	@Override
	public String getPlayerScoreLineContent(Player player, int score) {
		return ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.score") + ChatColor.AQUA + score;
	}

	@Override
	public String getTeamScoreLineContent(Player player, Team team, int teamScore) {
		return ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.team_score") + ChatColor.AQUA + teamScore;
	}

	@Override
	public String getKillLineContent(Player player, int kills) {
		return ChatColor.GOLD + LanguageManager.getString(player, "tournamentsystem.scoreboard.kills") + ChatColor.AQUA.toString() + kills;
	}
}