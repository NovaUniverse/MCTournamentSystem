package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import java.util.List;

public class TournamentResult {
	private String displayName;
	private List<TournamentTeamResult> teams;

	public TournamentResult(String displayName, List<TournamentTeamResult> teams) {
		this.displayName = displayName;
		this.teams = teams;
	}

	public String getDisplayName() {
		return displayName;
	}

	public List<TournamentTeamResult> getTeams() {
		return teams;
	}
}