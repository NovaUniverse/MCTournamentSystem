package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.TournamentSystemAuth;

public class UserAuth extends TournamentSystemAuth {
	private User user;
	private UserAuthType type;
	
	public UserAuth(User user, UserAuthType type) {
		super(user.getPermissions());
		this.user = user;
		this.type = type;
	}

	@Override
	public boolean isHidePlayerIPs() {
		return user.isHideIps();
	}

	@Override
	public String getName() {
		return user.getUsername();
	}

	@Override
	public String getDescriptiveUserName() {
		return type.getDescription() + " for " + user.getUsername();
	}
	
	public UserAuthType getType() {
		return type;
	}
	
	public User getUser() {
		return user;
	}
}