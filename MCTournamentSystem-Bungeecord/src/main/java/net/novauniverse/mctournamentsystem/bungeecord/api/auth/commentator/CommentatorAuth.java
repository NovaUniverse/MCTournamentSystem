package net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator;

import java.util.Collections;
import java.util.UUID;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.TournamentSystemAuth;

public class CommentatorAuth extends TournamentSystemAuth {
	private String name;
	private String key;

	private UUID minecraftUuid;

	public CommentatorAuth(String name, String key, UUID minecraftUuid) {
		super(Collections.emptyList());

		this.name = name;
		this.key = key;
		this.minecraftUuid = minecraftUuid;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getDescriptiveUserName() {
		return "Commentator key for " + name;
	}

	public String getKey() {
		return key;
	}

	public UUID getMinecraftUuid() {
		return minecraftUuid;
	}

	@Override
	public boolean isHidePlayerIPs() {
		return true;
	}
}