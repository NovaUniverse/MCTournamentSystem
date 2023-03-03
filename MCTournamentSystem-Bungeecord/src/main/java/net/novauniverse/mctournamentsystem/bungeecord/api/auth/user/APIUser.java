package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import java.util.List;

import org.json.JSONArray;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.IPVisibilitySettings;

public class APIUser implements IPVisibilitySettings {
	private String username;
	private String password;
	private List<UserPermission> permissions;
	private boolean hidePlayerIPs;

	public APIUser(String username, String password, List<UserPermission> permissions, boolean hidePlayerIPs) {
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

	public List<UserPermission> getPermissions() {
		return permissions;
	}
	
	@Override
	public boolean isHidePlayerIPs() {
		return hidePlayerIPs;
	}

	public boolean hasPermission(UserPermission permission) {
		if (permissions.contains(UserPermission.ADMIN)) {
			return true;
		}
		return permissions.contains(permission);
	}

	public JSONArray getPermissionsAsJSON() {
		JSONArray permissions = new JSONArray();

		if (this.hasPermission(UserPermission.ADMIN)) {
			for (UserPermission permission : UserPermission.values()) {
				permissions.put(permission.name());
			}
		} else {
			this.permissions.forEach(permission -> permissions.put(permission.name()));
		}

		return permissions;
	}
}