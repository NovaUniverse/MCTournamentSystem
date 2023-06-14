package net.novauniverse.mctournamentsystem.bungeecord.api.auth.internal;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.TournamentSystemAuth;

public class InternalAuth extends TournamentSystemAuth {
	public InternalAuth() {
		super(AuthPermission.admin());
	}

	@Override
	public String getName() {
		return "Internal";
	}

	@Override
	public String getDescriptiveUserName() {
		return "Internal Authentication";
	}
	
	@Override
	public boolean isHidePlayerIPs() {
		return false;
	}
}