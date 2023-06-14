package net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.TournamentSystemAuth;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;

public class APIKeyAuth extends TournamentSystemAuth {
	private String key;
	private APIUser user;
	private Type type;

	public APIKeyAuth(String key, APIUser user, Type type) {
		super(Collections.emptyList());
		this.key = key;
		this.user = user;
		this.type = type;
	}

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		user.getPermissions().forEach(p -> permissions.add(p.name()));
		return permissions;
	}

	public String getKey() {
		return key;
	}

	public APIUser getUser() {
		return user;
	}

	public Type getType() {
		return type;
	}

	@Override
	public boolean isHidePlayerIPs() {
		return user.isHidePlayerIPs();
	}

	@Override
	public String getName() {
		return user.getUsername();
	}

	@Override
	public String getDescriptiveUserName() {
		return "Access token for user " + user.getUsername();
	}

	public static enum Type {
		TOKEN, API_KEY;
	}
}
