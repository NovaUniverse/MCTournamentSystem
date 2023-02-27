package net.novauniverse.mctournamentsystem.bungeecord.listener.playertelementry;

import java.util.UUID;

import org.json.JSONObject;

public class PlayerTelementryData {
	// Values that only changes once
	private UUID uuid;
	private String username;

	// Values that will change
	private String server;

	private double health;
	private double maxHealth;

	private int food;
	private int closestEnemyDistance;

	private boolean gameEnabled;
	private boolean inGame;

	private String gamemode;

	private JSONObject metadata;

	public PlayerTelementryData(UUID uuid, String username, String server) {
		this.uuid = uuid;
		this.username = username;
		this.server = server;

		this.health = 0;
		this.maxHealth = 0;
		this.food = 0;
		this.closestEnemyDistance = Integer.MAX_VALUE;
		this.gamemode = null;

		this.gameEnabled = false;
		this.inGame = false;

		this.metadata = new JSONObject();

	}

	public JSONObject toJSON() {
		JSONObject json = new JSONObject();

		json.put("uuid", uuid.toString());
		json.put("username", username);
		json.put("gamemode", gamemode);
		json.put("health", health);
		json.put("max_health", maxHealth);
		json.put("food", food);
		json.put("closest_enemy_distance", closestEnemyDistance);
		json.put("game_enabled", gameEnabled);
		json.put("in_game", inGame);
		json.put("server", server);
		json.put("metadata", metadata);

		return json;
	}

	public UUID getUuid() {
		return uuid;
	}

	public String getUsername() {
		return username;
	}

	public String getServer() {
		return server;
	}

	public void setServer(String server) {
		this.server = server;
	}

	public double getHealth() {
		return health;
	}

	public void setHealth(double health) {
		this.health = health;
	}

	public double getMaxHealth() {
		return maxHealth;
	}

	public void setMaxHealth(double maxHealth) {
		this.maxHealth = maxHealth;
	}

	public int getFood() {
		return food;
	}

	public void setFood(int food) {
		this.food = food;
	}

	public String getGamemode() {
		return gamemode;
	}

	public void setGamemode(String gamemode) {
		this.gamemode = gamemode;
	}

	public int getClosestEnemyDistance() {
		return closestEnemyDistance;
	}

	public void setClosestEnemyDistance(int closestEnemyDistance) {
		this.closestEnemyDistance = closestEnemyDistance;
	}

	public boolean isGameEnabled() {
		return gameEnabled;
	}

	public void setGameEnabled(boolean gameEnabled) {
		this.gameEnabled = gameEnabled;
	}

	public boolean isInGame() {
		return inGame;
	}

	public void setInGame(boolean inGame) {
		this.inGame = inGame;
	}

	public JSONObject getMetadata() {
		return metadata;
	}

	public void setMetadata(JSONObject metadata) {
		this.metadata = metadata;
	}
}