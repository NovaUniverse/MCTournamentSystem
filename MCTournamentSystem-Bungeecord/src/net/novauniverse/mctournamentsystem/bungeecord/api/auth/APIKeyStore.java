package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class APIKeyStore {
	private static final List<String> apiKeys = new ArrayList<String>();
	private static final Map<String, UUID> commentatorKeys = new HashMap<String, UUID>();

	public static List<String> getApiKeys() {
		return apiKeys;
	}

	public static Map<String, UUID> getCommentatorKeys() {
		return commentatorKeys;
	}

	public static void addApiKey(String key) {
		apiKeys.add(key);
	}

	public static void addCommentatorKey(String key, UUID uuid) {
		commentatorKeys.put(key, uuid);
	}
}