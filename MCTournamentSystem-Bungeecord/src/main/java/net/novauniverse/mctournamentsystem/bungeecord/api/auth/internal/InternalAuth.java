package net.novauniverse.mctournamentsystem.bungeecord.api.auth.internal;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

public class InternalAuth extends Authentication {
	private static final APIUser user = new APIUser("InternalAPIAccess", "", UserPermission.generatePermissionList(UserPermission.ADMIN), false);

	@Override
	public APIUser getUser() {
		return user;
	}

	@Override
	public String getDescriptiveUserName() {
		return "Internal API user";
	}
}