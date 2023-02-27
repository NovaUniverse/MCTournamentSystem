package net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator;

import java.util.UUID;

import javax.annotation.Nullable;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

public class CommentatorAuth extends Authentication {
	public static final APIUser CommentatorUser = new APIUser("Commentator", "", UserPermission.blank());

	private String key;
	private UUID minecraftUserUUID;
	private String identifier;

	public CommentatorAuth(String key, UUID minecraftUserUUID, String identifier) {
		this.key = key;
		this.minecraftUserUUID = minecraftUserUUID;
		this.identifier = identifier;
	}

	public String getIdentifier() {
		return identifier;
	}

	public String getKey() {
		return key;
	}

	@Nullable
	public UUID getMinecraftUserUUID() {
		return minecraftUserUUID;
	}

	@Override
	public APIUser getUser() {
		return CommentatorUser;
	}
	
	@Override
	public String getDescriptiveUserName() {
		return "Commentator " + identifier;
	}
}