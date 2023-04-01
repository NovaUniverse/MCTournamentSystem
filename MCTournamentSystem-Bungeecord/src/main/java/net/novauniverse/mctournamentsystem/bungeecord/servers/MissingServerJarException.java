package net.novauniverse.mctournamentsystem.bungeecord.servers;

public class MissingServerJarException extends Exception {
	private static final long serialVersionUID = 140899469117272709L;

	public MissingServerJarException(String message) {
		super(message);
	}
}