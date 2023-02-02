package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.util.ArrayList;
import java.util.List;
import javax.annotation.Nullable;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKey;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;

public class APIKeyStore {
	private static final List<APIKey> apiKeys = new ArrayList<APIKey>();
	private static final List<CommentatorAuth> commentatorKeys = new ArrayList<CommentatorAuth>();

	public static List<APIKey> getApiKeys() {
		return apiKeys;
	}

	public static List<CommentatorAuth> getCommentatorKeys() {
		return commentatorKeys;
	}

	public static void addApiKey(APIKey key) {
		apiKeys.add(key);
	}

	public static boolean hasAPIKey(String key) {
		return APIKeyStore.getAPIKey(key) != null;
	}

	@Nullable
	public static APIKey getAPIKey(String key) {
		return apiKeys.stream().filter(k -> k.getKey().equals(key)).findFirst().orElse(null);
	}

	public static boolean hasCommentatorKey(String key) {
		return APIKeyStore.getCommentatorKey(key) != null;
	}

	@Nullable
	public static CommentatorAuth getCommentatorKey(String key) {
		return commentatorKeys.stream().filter(k -> k.getKey().equals(key)).findFirst().orElse(null);
	}

	public static void addCommentatorKey(CommentatorAuth key) {
		commentatorKeys.add(key);
	}
}