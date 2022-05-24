package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import java.util.List;

public class TournamentTeamResult {
	private int teamNumber;
	private int score;
	private List<TournamentPlayer> players;

	public TournamentTeamResult(int teamNumber, int score, List<TournamentPlayer> players) {
		this.teamNumber = teamNumber;
		this.score = score;
		this.players = players;

	}

	public List<TournamentPlayer> getPlayers() {
		return players;
	}

	public int getScore() {
		return score;
	}

	public int getTeamNumber() {
		return teamNumber;
	}
}