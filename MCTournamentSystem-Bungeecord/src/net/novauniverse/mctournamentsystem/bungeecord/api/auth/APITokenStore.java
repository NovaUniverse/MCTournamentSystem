package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

public class APITokenStore {
	private static final List<APIAccessToken> tokens = new ArrayList<APIAccessToken>();

	public static final APIAccessToken DUMMY_TOKEN = new APIAccessToken(UUID.fromString("00000000-0000-0000-0000-000000000000"), new APIUser("DevelopmentUser", "", UserPermission.generatePermissionList(UserPermission.ADMIN)));

	public static final APIAccessToken createToken(APIUser user) {
		if (APITokenStore.tokens.size() >= 500000) {
			APITokenStore.tokens.remove(0);
		}

		APIAccessToken token = new APIAccessToken(UUID.randomUUID(), user);

		APITokenStore.tokens.add(token);

		return token;
	}

	public static final APIAccessToken getToken(String uuid) {
		return APITokenStore.tokens.stream().filter(token -> token.getUuid().toString().equalsIgnoreCase(uuid)).findFirst().orElse(null);
	}

	public static final APIAccessToken getToken(UUID uuid) {
		return APITokenStore.getToken(uuid.toString());
	}
}