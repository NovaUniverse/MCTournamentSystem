package net.novauniverse.mctournamentsystem.commons.socketapi;

import javax.annotation.Nullable;

import org.json.JSONObject;

public class SocketAPIConfig {
	private String url;
	private String key;
	private boolean enabled;

	public SocketAPIConfig(JSONObject json) {
		enabled = json.optBoolean("enabled", false);
		url = json.getString("url");
		key = json.optString("key", "");
	}

	public String getUrl() {
		return url;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Nullable
	public static final SocketAPIConfig parse(JSONObject json) {
		if (json == null) {
			return null;
		}
		return new SocketAPIConfig(json);
	}
}