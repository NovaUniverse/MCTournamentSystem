package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.ArrayList;
import java.util.List;

import net.novauniverse.apilib.http.auth.Authentication;

public abstract class TournamentSystemAuth extends Authentication implements IPVisibilitySettings {
	private List<AuthPermission> permissions;

	public TournamentSystemAuth(List<AuthPermission> permissions) {
		this.permissions = permissions;
	}

	@Override
	public List<String> getPermissions() {
		List<String> permissions = new ArrayList<>();
		this.permissions.forEach(p -> permissions.add(p.name()));
		return permissions;
	}
	
	public abstract String getName();
	
	public abstract String getDescriptiveUserName();
}