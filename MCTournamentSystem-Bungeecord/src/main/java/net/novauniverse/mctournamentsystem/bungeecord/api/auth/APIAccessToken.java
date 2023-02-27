package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.UUID;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;

public class APIAccessToken extends Authentication {
	private UUID uuid;
	private APIUser user;

	public APIAccessToken(UUID uuid, APIUser user) {
		this.uuid = uuid;
		this.user = user;
	}

	@Override
	public APIUser getUser() {
		return user;
	}

	public UUID getUuid() {
		return uuid;
	}
	
	@Override
	public String getDescriptiveUserName() {
		return "User access token for " + user.getUsername();
	}
}