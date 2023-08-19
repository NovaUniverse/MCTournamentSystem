package net.novauniverse.mctournamentsystem.bungeecord.api.data;

public class TeamData {
	private final int teamId;
	private final int teamNumber;
	private final int score;
	private final int kills;

	public TeamData(int id, int teamNumber, int score, int kills) {
		this.teamId = id;
		this.teamNumber = teamNumber;
		this.score = score;
		this.kills = kills;
	}
	
	public int getTeamId() {
		return teamId;
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