package net.novauniverse.mctournamentsystem.bungeecord.misc;

import javax.annotation.Nullable;

import org.json.JSONObject;

public class CustomTheme {
	private String name;
	private String url;
	
	@Nullable
	private String baseTheme;
	
	@Nullable
	private JSONObject serverConsoleTheme;
	
	public CustomTheme(String name, String url, @Nullable String baseTheme, @Nullable JSONObject serverConsoleTheme) {
		this.name = name;
		this.url = url;
		this.baseTheme = baseTheme;
		this.serverConsoleTheme = serverConsoleTheme;
	}
	
	public String getName() {
		return name;
	}
	
	public String getUrl() {
		return url;
	}
	
	public String getBaseTheme() {
		return baseTheme;
	}
	
	public JSONObject getServerConsoleTheme() {
		return serverConsoleTheme;
	}
}