package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import java.util.List;

import org.json.JSONArray;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.IPVisibilitySettings;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;

public class APIUser implements IPVisibilitySettings {
	private String username;
	private String password;
	private List<AuthPermission> permissions;
	private boolean hidePlayerIPs;

	public APIUser(String username, String password, List<AuthPermission> permissions, boolean hidePlayerIPs) {
		this.username = username;
		this.password = password;
		this.permissions = permissions;
		this.hidePlayerIPs = hidePlayerIPs;
	}

	public String getUsername() {
		return username;
	}

	public String getPassword() {
		return password;
	}

	public List<AuthPermission> getPermissions() {
		return permissions;
	}
	
	@Override
	public boolean isHidePlayerIPs() {
		return hidePlayerIPs;
	}

	public boolean hasPermission(AuthPermission permission) {
		if (permissions.contains(AuthPermission.ADMIN)) {
			return true;
		}
		return permissions.contains(permission);
	}

	public JSONArray getPermissionsAsJSON() {
		JSONArray permissions = new JSONArray();

		if (this.hasPermission(AuthPermission.ADMIN)) {
			for (AuthPermission permission : AuthPermission.values()) {
				permissions.put(permission.name());
			}
		} else {
			this.permissions.forEach(permission -> permissions.put(permission.name()));
		}

		return permissions;
	}
}