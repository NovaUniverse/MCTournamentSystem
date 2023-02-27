package net.novauniverse.mctournamentsystem.spigot.score;

import org.bukkit.ChatColor;

import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;

public class TeamScoreData extends ScoreData {
	private TournamentSystemTeam team;

	public TeamScoreData(TournamentSystemTeam team) {
		super(team.getScore());
		this.team = team;
	}

	public TeamScoreData(TournamentSystemTeam team, int score) {
		super(score);
		this.team = team;
	}

	public Integer getTeamNumber() {
		return team.getTeamNumber();
	}

	public TournamentSystemTeam getTeam() {
		return team;
	}

	@Override
	public String toString() {
		String teamName;

		if (TournamentSystem.getInstance().isForceShowTeamNameInLeaderboard()) {
			teamName = team.getTeamColor() + team.getDisplayName();
		} else {
			if (team.getMembers().size() > 0) {
				teamName = team.getTeamColor() + team.getMemberString();
			} else {
				teamName = team.getTeamColor() + team.getDisplayName();
			}
		}

		return teamName + ChatColor.GOLD + " : " + ChatColor.AQUA + this.getScore();
	}
}