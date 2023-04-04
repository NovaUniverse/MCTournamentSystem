package net.novauniverse.mctournamentsystem.bungeecord.api.data;

public class TeamData {
	private int teamNumber;
	private int score;
	private int kills;

	public TeamData(int teamNumber, int score, int kills) {
		this.teamNumber = teamNumber;
		this.score = score;
		this.kills = kills;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public int getScore() {
		return score;
	}

	public int getKills() {
		return kills;
	}
}