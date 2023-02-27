package net.novauniverse.mctournamentsystem.bungeecord.servers;

public class ServerAutoRegisterData {
	private boolean enabled;
	private String host;
	private int port;
	
	public ServerAutoRegisterData(boolean enabled, String host, int port) {
		this.enabled = enabled;
		this.host = host;
		this.port = port;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	
	public String getHost() {
		return host;
	}
	
	public int getPort() {
		return port;
	}
}