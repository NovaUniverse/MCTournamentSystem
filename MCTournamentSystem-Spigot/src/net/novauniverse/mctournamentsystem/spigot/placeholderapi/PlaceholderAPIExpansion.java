package net.novauniverse.mctournamentsystem.spigot.placeholderapi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class PlaceholderAPIExpansion extends PlaceholderExpansion {

	@Override
	public @NotNull String getIdentifier() {
		return "tournamentsystem";
	}

	@Override
	public @NotNull String getAuthor() {
		return "tournamentsystem";
	}

	@Override
	public @NotNull String getVersion() {
		return "1.0.0";
	}

	@Override
	public boolean persist() {
		return true;
	}

	@Override
	public @Nullable String onPlaceholderRequest(Player player, @NotNull String params) {
		if (params.equalsIgnoreCase("score")) {
			return "" + ScoreManager.getInstance().getPlayerScore(player);
		} else if (params.equalsIgnoreCase("team_score")) {
			TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
			if (team != null) {
				return "" + team.getScore();
			}
			return "0";
		} else if (params.equalsIgnoreCase("team_name")) {
			TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
			if (team != null) {
				return "" + team.getDisplayName();
			}

			return "No team";
		} else if (params.equalsIgnoreCase("team_color")) {
			TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
			if (team != null) {
				return "" + team.getTeamColor();
			}
			return "";
		} else if (params.equalsIgnoreCase("kills")) {
			return "" + PlayerKillCache.getInstance().getPlayerKills(player.getUniqueId());
		}

		return null;
	}
}
