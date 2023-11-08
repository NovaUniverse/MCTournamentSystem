package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.security.PasswordHashing;

public class User {
	private String username;
	private String passwordHash;
	private UUID passwordChangeId;
	private boolean hideIps;
	private List<AuthPermission> permissions;
	private boolean allowManagingAccounts; 

	public User(String username, String passwordHash, UUID passwordChangeId, boolean hideIps, List<AuthPermission> permissions, boolean allowManagingAccounts) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.passwordChangeId = passwordChangeId;
		this.hideIps = hideIps;
		this.permissions = permissions;
		this.allowManagingAccounts = allowManagingAccounts;
	}

	public String getUsername() {
		return username;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public UUID getPasswordChangeId() {
		return passwordChangeId;
	}

	public List<AuthPermission> getPermissions() {
		return permissions;
	}

	public boolean isHideIps() {
		return hideIps;
	}

	public void setHideIps(boolean hideIps) {
		this.hideIps = hideIps;
	}
	
	public boolean isAllowManagingAccounts() {
		return allowManagingAccounts;
	}

	public void changePassword(String password) {
		passwordHash = PasswordHashing.hashPassword(password);
		passwordChangeId = UUID.randomUUID();
	}

	public static User createNew(String username, String password, boolean allowManagingAccounts) {
		String hash = PasswordHashing.hashPassword(password);
		UUID passwordChangeId = UUID.randomUUID();

		return new User(username, hash, passwordChangeId, false, new ArrayList<>(), allowManagingAccounts);
	}

	public JSONArray getPermissionsAsJSON() {
		JSONArray result = new JSONArray();
		permissions.forEach(p -> result.put(p.name()));
		return result;
	}
}