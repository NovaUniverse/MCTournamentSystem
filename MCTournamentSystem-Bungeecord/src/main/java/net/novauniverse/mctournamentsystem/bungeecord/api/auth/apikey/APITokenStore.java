package net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKeyAuth.Type;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIUser;

public class APITokenStore {
	private static final List<APIKeyAuth> tokens = new ArrayList<APIKeyAuth>();

	public static final APIKeyAuth DUMMY_TOKEN = new APIKeyAuth(UUID.fromString("00000000-0000-0000-0000-000000000000").toString(), new APIUser("DevelopmentUser", "", Collections.singletonList(AuthPermission.ADMIN), false), Type.TOKEN);

	public static final APIKeyAuth createToken(APIUser user) {
		if (APITokenStore.tokens.size() >= 500000) {
			APITokenStore.tokens.remove(0);
		}

		APIKeyAuth token = new APIKeyAuth(UUID.randomUUID().toString(), user, Type.TOKEN);

		APITokenStore.tokens.add(token);

		return token;
	}

	@Nullable
	public static final APIKeyAuth getToken(String uuid) {
		return APITokenStore.tokens.stream().filter(token -> token.getKey().equalsIgnoreCase(uuid)).findFirst().orElse(null);
	}

	@Nullable
	public static final APIKeyAuth getToken(UUID uuid) {
		return APITokenStore.getToken(uuid.toString());
	}
}