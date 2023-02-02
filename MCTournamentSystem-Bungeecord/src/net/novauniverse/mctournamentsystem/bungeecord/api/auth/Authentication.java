package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.List;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

public abstract class Authentication {
	public abstract APIUser getUser();

	public boolean hasPermission(UserPermission permission) {
		return this.getUser().hasPermission(permission);
	}

	public List<UserPermission> getPermissions() {
		return this.getUser().getPermissions();
	}
	
	public abstract String getDescriptiveUserName();
}