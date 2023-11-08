package net.novauniverse.mctournamentsystem.bungeecord.authdb;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorUser;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.APIKey;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.User;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.JSONFileUtils;

public class AuthDB {
	private File file;

	private List<CommentatorUser> commentators;
	private List<User> users;
	private List<APIKey> apiKeys;

	public AuthDB(File file) throws IOException {
		this.file = file;
		this.commentators = new ArrayList<>();
		this.users = new ArrayList<>();
		this.apiKeys = new ArrayList<>();

		if (!file.exists()) {
			Log.info("AuthDB", "Creating initial AuthDB");

			User admin = User.createNew("admin", "admin", true);
			admin.getPermissions().add(AuthPermission.ADMIN);

			this.users.add(admin);
			save();
		}

		load();
	}

	public void load() throws JSONException, IOException {
		JSONObject data = JSONFileUtils.readJSONObjectFromFile(file);

		JSONArray commentators = data.getJSONArray("commentators");
		JSONArray users = data.getJSONArray("users");
		JSONArray apiKeys = data.getJSONArray("api_keys");

		this.commentators.clear();
		this.users.clear();
		this.apiKeys.clear();

		for (int i = 0; i < commentators.length(); i++) {
			JSONObject commentatorData = commentators.getJSONObject(i);

			String username = commentatorData.getString("username");

			if (this.commentators.stream().anyMatch(c -> c.getUsername().equalsIgnoreCase(username))) {
				Log.warn("AuthDB", "Duplicate commentator " + username);
				return;
			}

			String passwordHash = commentatorData.getString("password_hash");
			UUID passwordChangeId = UUID.fromString(commentatorData.getString("password_change_id"));
			UUID minecraftUUID = UUID.fromString(commentatorData.getString("minecraft_uuid"));

			this.commentators.add(new CommentatorUser(username, passwordHash, passwordChangeId, minecraftUUID));
		}

		for (int i = 0; i < users.length(); i++) {
			JSONObject userData = users.getJSONObject(i);

			String username = userData.getString("username");

			if (this.apiKeys.stream().anyMatch(u -> u.getUser().getUsername().equalsIgnoreCase(username))) {
				Log.warn("AuthDB", "Duplicate user " + username);
				return;
			}

			String passwordHash = userData.getString("password_hash");
			UUID passwordChangeId = UUID.fromString(userData.getString("password_change_id"));
			boolean hideIps = userData.optBoolean("hide_ips", false);
			boolean allowManageUsers = userData.optBoolean("allow_manage_users", false);

			JSONArray permissionList = userData.getJSONArray("permissions");
			List<AuthPermission> permissions = new ArrayList<>();

			for (int j = 0; j < permissionList.length(); j++) {
				String permString = permissionList.getString(j);
				try {
					permissions.add(AuthPermission.valueOf(permString));
				} catch (Exception e) {
					Log.warn("AuthDB", "Unknown permission: " + permString);
				}

			}

			this.users.add(new User(username, passwordHash, passwordChangeId, hideIps, permissions, allowManageUsers));
		}

		for (int i = 0; i < apiKeys.length(); i++) {
			JSONObject apiKeyData = apiKeys.getJSONObject(i);

			String key = apiKeyData.getString("key");

			if (this.apiKeys.stream().anyMatch(k -> k.getKey().equalsIgnoreCase(key))) {
				Log.warn("AuthDB", "Duplicate api key " + key);
				return;
			}

			String username = apiKeyData.getString("user");

			User user = this.users.stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
			if (user == null) {
				Log.warn("AuthDB", "Api key " + key + " has an invalid user: " + username);
			} else {
				this.apiKeys.add(new APIKey(key, user));
			}
		}

	}

	public void save() throws IOException {
		JSONObject data = new JSONObject();

		JSONArray commentators = new JSONArray();
		JSONArray users = new JSONArray();
		JSONArray apiKeys = new JSONArray();

		this.commentators.forEach(c -> {
			JSONObject entry = new JSONObject();
			entry.put("username", c.getUsername());
			entry.put("password_hash", c.getPasswordHash());
			entry.put("password_change_id", c.getPasswordChangeId().toString());
			entry.put("minecraft_uuid", c.getMinecraftUuid().toString());
			commentators.put(entry);
		});

		this.users.forEach(u -> {
			JSONArray permissions = new JSONArray();
			u.getPermissions().forEach(p -> permissions.put(p.name()));

			JSONObject entry = new JSONObject();
			entry.put("username", u.getUsername());
			entry.put("password_hash", u.getPasswordHash());
			entry.put("password_change_id", u.getPasswordChangeId().toString());
			entry.put("hide_ips", u.isHideIps());
			entry.put("permissions", permissions);
			entry.put("allow_manage_users", u.isAllowManagingAccounts());

			users.put(entry);
		});

		this.apiKeys.forEach(k -> {
			JSONObject entry = new JSONObject();
			entry.put("key", k.getKey());
			entry.put("user", k.getUser().getUsername());
			apiKeys.put(entry);
		});

		data.put("commentators", commentators);
		data.put("users", users);
		data.put("api_keys", apiKeys);

		JSONFileUtils.saveJson(file, data, 4);
	}

	public List<CommentatorUser> getCommentators() {
		return commentators;
	}

	public List<User> getUsers() {
		return users;
	}

	public List<APIKey> getApiKeys() {
		return apiKeys;
	}
}