package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public enum AuthPermission {
	ADMIN,
	MANAGE_SERVERS,
	SEND_PLAYERS,
	START_GAME,
	MANAGE_TRIGGERS,
	MANAGE_STAFF,
	REMOTE_EXECUTE_SERVER_COMMAND,
	MANAGE_WHITELIST,
	IMPORT_SCORE_SNAPSHOT,
	EDIT_TEAMS,
	VIEW_COMMENTATOR_GUEST_KEY,
	BROADCAST_MESSAGE,
	MANAGE_SETTINGS,
	SET_NEXT_MINIGAME,
	CLEAR_DATA,
	SHUTDOWN;

	public static final List<AuthPermission> generatePermissionList(AuthPermission... permissions) {
		List<AuthPermission> result = new ArrayList<>();
		for (AuthPermission permission : permissions) {
			result.add(permission);
		}
		return result;
	}

	public static final List<AuthPermission> blank() {
		return new ArrayList<>();
	}
	
	public static final List<AuthPermission> admin() {
		return Collections.singletonList(ADMIN);
	}
}