package net.novauniverse.mctournamentsystem.bungeecord.misc;

import javax.annotation.Nullable;

public class CustomTheme {
	private String name;
	private String url;
	@Nullable
	private String baseTheme;
	
	public CustomTheme(String name, String url, @Nullable String baseTheme) {
		this.name = name;
		this.url = url;
		this.baseTheme = baseTheme;
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
}