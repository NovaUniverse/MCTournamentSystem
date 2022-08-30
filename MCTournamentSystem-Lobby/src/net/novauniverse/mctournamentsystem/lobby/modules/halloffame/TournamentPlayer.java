package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import java.util.UUID;

public class TournamentPlayer {
	private UUID uuid;
	private String username;
	private int score;
	private int kills;

	public TournamentPlayer(UUID uuid, String username, int score, int kills) {
		this.uuid = uuid;
		this.username = username;
		this.score = score;
		this.kills = kills;
	}

	public UUID getUuid() {
		return uuid;
	}

	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getUsername() {
		return username;
	}

	public int getScore() {
		return score;
	}

	public int getKills() {
		return kills;
	}
}