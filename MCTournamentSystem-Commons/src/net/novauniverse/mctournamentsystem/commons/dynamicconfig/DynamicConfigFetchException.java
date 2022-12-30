package net.novauniverse.mctournamentsystem.commons.dynamicconfig;

public class DynamicConfigFetchException extends Exception {
	private static final long serialVersionUID = -3769873328967364288L;

	public DynamicConfigFetchException(String message) {
		super(message);
	}

	public DynamicConfigFetchException(String message, Throwable cause) {
		super(message, cause);
	}
}