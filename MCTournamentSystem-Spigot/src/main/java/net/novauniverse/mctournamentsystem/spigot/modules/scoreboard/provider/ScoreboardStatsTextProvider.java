package net.novauniverse.mctournamentsystem.spigot.modules.scoreboard.provider;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import net.zeeraa.novacore.spigot.teams.Team;

public interface ScoreboardStatsTextProvider {
	String getPlayerScoreLineContent(Player player, int score);

	String getTeamScoreLineContent(Player player, @Nullable Team team, int teamScore);

	String getKillLineContent(Player player, int kills);
}