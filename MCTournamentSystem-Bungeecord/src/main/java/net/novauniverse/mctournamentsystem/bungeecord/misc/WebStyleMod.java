package net.novauniverse.mctournamentsystem.bungeecord.misc;

public class WebStyleMod {
	private String name;
	private String cssUrl;
	
	public WebStyleMod(String name, String cssUrl) {
		this.name = name;
		this.cssUrl = cssUrl;
	}
	
	public String getName() {
		return name;
	}
	
	public String getCssUrl() {
		return cssUrl;
	}
}