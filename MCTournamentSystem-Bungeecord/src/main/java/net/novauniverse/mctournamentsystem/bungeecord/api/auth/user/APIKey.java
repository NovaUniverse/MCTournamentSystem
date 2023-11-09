package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

public class APIKey {
	private String key;
	private User user;

	public APIKey(String key, User user) {
		this.key = key;
		this.user = user;
	}

	public String getKey() {
		return key;
	}

	public User getUser() {
		return user;
	}
}