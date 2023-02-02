package net.novauniverse.mctournamentsystem.bungeecord.api.data;

import java.util.UUID;

import org.json.JSONObject;

public class PlayerData {
	private UUID uuid;

	private int kills;
	private int score;
	private int teamScore;
	private int teamNumber;
	private String username;
	private JSONObject metadata;

	public PlayerData(UUID uuid, int kills, int score, int teamScore, int teamNumber, String username, JSONObject metadata) {
		this.uuid = uuid;
		this.kills = kills;
		this.score = score;
		this.teamScore = teamScore;
		this.teamNumber = teamNumber;
		this.username = username;
		this.metadata = metadata;
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

	public JSONObject getMetadata() {
		return metadata;
	}
}