package net.novauniverse.mctournamentsystem.commons.config;

public class InternetCafeOptions {
	private String ggRockURL;

	public String getGGRockURL() {
		return ggRockURL;
	}
	 
	public boolean hasGGRockURL() {
		return ggRockURL != null;
	}
	
	public InternetCafeOptions() {
		this(null);
	}

	public InternetCafeOptions(String ggRockURL) {
		this.ggRockURL = ggRockURL;
	}
}