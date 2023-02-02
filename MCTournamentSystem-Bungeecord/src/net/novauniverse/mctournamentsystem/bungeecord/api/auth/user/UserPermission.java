package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import java.util.ArrayList;
import java.util.List;

public enum UserPermission {
	ADMIN,
	MANAGE_SERVERS,
	SEND_PLAYERS,
	START_GAME,
	MANAGE_TRIGGERS,
	MANAGE_STAFF,
	MANAGE_WHITELIST,
	IMPORT_SCORE_SNAPSHOT,
	EDIT_TEAMS,
	VIEW_COMMENTATOR_GUEST_KEY,
	BROADCAST_MESSAGE,
	MANAGE_SETTINGS,
	SET_NEXT_MINIGAME,
	CLEAR_DATA,
	SHUTDOWN;

	public static final List<UserPermission> generatePermissionList(UserPermission... permissions) {
		List<UserPermission> result = new ArrayList<>();
		for (UserPermission permission : permissions) {
			result.add(permission);
		}
		return result;
	}

	public static final List<UserPermission> blank() {
		return new ArrayList<>();
	}
}