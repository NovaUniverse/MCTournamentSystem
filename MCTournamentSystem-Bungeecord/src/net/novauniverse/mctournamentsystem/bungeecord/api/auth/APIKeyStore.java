package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.ArrayList;
import java.util.List;

public class APIKeyStore {
	private static final List<String> apiKeys = new ArrayList<String>();

	public static List<String> getApiKeys() {
		return apiKeys;
	}

	public static void addApiKey(String key) {
		apiKeys.add(key);
	}
}