package net.novauniverse.mctournamentsystem.lobby.modules.lobby.kotl.score;

import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class KingOfTheLadderScore {
	private UUID uuid;
	private String name;
	private int score;

	public KingOfTheLadderScore(Player player) {
		this.uuid = player.getUniqueId();
		this.name = player.getName();
		this.score = 0;
	}

	public void updateName() {
		Player player = Bukkit.getServer().getPlayer(uuid);
		if (player != null) {
			name = player.getName();
		}
	}

	public UUID getUuid() {
		return uuid;
	}

	public int getScore() {
		return score;
	}

	public void incrementScore() {
		score++;
	}

	public String getName() {
		return name;
	}
}