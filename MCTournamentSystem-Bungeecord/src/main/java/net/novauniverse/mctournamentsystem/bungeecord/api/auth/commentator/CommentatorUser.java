package net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator;

import java.util.UUID;

import net.novauniverse.mctournamentsystem.bungeecord.security.PasswordHashing;

public class CommentatorUser {
	private String username;
	private String passwordHash;
	private UUID passwordChangeId;
	private UUID minecraftUuid;

	public CommentatorUser(String username, String passwordHash, UUID passwordChangeId, UUID minecraftUuid) {
		this.username = username;
		this.passwordHash = passwordHash;
		this.passwordChangeId = passwordChangeId;
		this.minecraftUuid = minecraftUuid;
	}

	public UUID getMinecraftUuid() {
		return minecraftUuid;
	}

	public UUID getPasswordChangeId() {
		return passwordChangeId;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getUsername() {
		return username;
	}

	public void changePassword(String password) {
		passwordHash = PasswordHashing.hashPassword(password);
		passwordChangeId = UUID.randomUUID();
	}

	public static CommentatorUser createNew(String username, String password, UUID minecraftUUID) {
		String passwordHash = PasswordHashing.hashPassword(password);
		UUID passwordChangeId = UUID.randomUUID();
		return new CommentatorUser(username, passwordHash, passwordChangeId, minecraftUUID);
	}
}