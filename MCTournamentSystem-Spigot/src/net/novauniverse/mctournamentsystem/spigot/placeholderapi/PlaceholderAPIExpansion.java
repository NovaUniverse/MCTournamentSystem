package net.novauniverse.mctournamentsystem.spigot.placeholderapi;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerKillCache;
import net.novauniverse.mctournamentsystem.spigot.modules.nextminigame.NextMinigameManager;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.commons.log.Log;
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
		try {
			if (params.equalsIgnoreCase("score")) {
				if (player == null) {
					return "";
				}
				return "" + ScoreManager.getInstance().getPlayerScore(player);
			} else if (params.equalsIgnoreCase("team_score")) {
				if (player == null) {
					return "";
				}
				TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					return "" + team.getScore();
				}
				return "0";
			} else if (params.equalsIgnoreCase("team_name")) {
				if (player == null) {
					return "";
				}
				TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					return "" + team.getDisplayName();
				}

				return "No team";
			} else if (params.equalsIgnoreCase("team_color")) {
				if (player == null) {
					return "";
				}
				TournamentSystemTeam team = (TournamentSystemTeam) TeamManager.getTeamManager().getPlayerTeam(player);
				if (team != null) {
					return "" + team.getTeamColor();
				}
				return "";
			} else if (params.equalsIgnoreCase("kills")) {
				if (player == null) {
					return "";
				}
				return "" + PlayerKillCache.getInstance().getPlayerKills(player.getUniqueId());
			} else if (params.equalsIgnoreCase("next_minigame")) {
				String next = NextMinigameManager.getInstance().getNextMinigame();
				return next == null ? "" : next;
			} else if (params.equalsIgnoreCase("next_minigame_full")) {
				String next = NextMinigameManager.getInstance().getNextMinigame();
				return next == null ? "" : "Next minigame: " + next;
			}

			return "Unknown Placeholder: " + params;
		} catch (Exception e) {
			Log.error("PlaceholderAPIExpansion", "Exception while parsing placeholder " + params + ". " + e.getClass().getName() + " " + e.getMessage());
			e.printStackTrace();
			return "Internal Error";
		}
	}
}