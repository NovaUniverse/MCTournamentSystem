package net.novauniverse.mctournamentsystem.bungeecord.api.data;

import java.util.UUID;

public class PlayerData {
	private UUID uuid;

	private int kills;
	private int score;
	private int teamScore;
	private int teamNumber;
	private String username;

	public PlayerData(UUID uuid, int kills, int score, int teamScore, int teamNumber, String username) {
		this.uuid = uuid;
		this.kills = kills;
		this.score = score;
		this.teamScore = teamScore;
		this.teamNumber = teamNumber;
		this.username = username;
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getKills() {
		return kills;
	}

	public int getScore() {
		return score;
	}

	public int getTeamScore() {
		return teamScore;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public String getUsername() {
		return username;
	}
}