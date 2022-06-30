package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import java.util.ArrayList;
import java.util.List;

public class APIUserStore {
	private static final List<APIUser> users = new ArrayList<APIUser>();

	public static final void addUser(APIUser user) {
		APIUserStore.users.add(user);
	}

	public static final List<APIUser> getUsers() {
		return APIUserStore.users;
	}
}