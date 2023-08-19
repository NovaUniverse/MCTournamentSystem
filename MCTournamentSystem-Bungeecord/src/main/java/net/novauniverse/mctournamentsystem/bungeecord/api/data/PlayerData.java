package net.novauniverse.mctournamentsystem.bungeecord.api.data;

import java.util.UUID;

import org.json.JSONObject;

public class PlayerData {
	private final int id;
	private final UUID uuid;

	private final int kills;
	private final int score;
	private final int teamScore;
	private final int teamNumber;
	private final String username;
	private final JSONObject metadata;

	public PlayerData(int id, UUID uuid, int kills, int score, int teamScore, int teamNumber, String username, JSONObject metadata) {
		this.id = id;
		this.uuid = uuid;
		this.kills = kills;
		this.score = score;
		this.teamScore = teamScore;
		this.teamNumber = teamNumber;
		this.username = username;
		this.metadata = metadata;
	}
	
	public int getId() {
		return id;
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