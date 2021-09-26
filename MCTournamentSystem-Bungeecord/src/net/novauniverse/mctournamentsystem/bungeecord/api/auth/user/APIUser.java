package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

public class APIUser {
	private String username;
	private String password;

	public APIUser(String username, String password) {
		this.username = username;
		this.password = password;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}
}