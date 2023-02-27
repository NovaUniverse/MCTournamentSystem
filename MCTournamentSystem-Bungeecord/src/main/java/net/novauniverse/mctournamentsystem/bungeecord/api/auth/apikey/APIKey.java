package net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;

public class APIKey extends Authentication {
	private String key;
	private APIUser user;

	public APIKey(String key, APIUser user) {
		this.key = key;
		this.user = user;
	}

	public String getKey() {
		return key;
	}

	@Override
	public APIUser getUser() {
		return user;
	}
	
	@Override
	public String getDescriptiveUserName() {
		return "API key for user " + user.getUsername();
	}
}