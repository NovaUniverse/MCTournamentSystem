package net.novauniverse.mctournamentsystem.commons.utils;

public class WrappedString {
	private String string;

	public WrappedString() {
		this(null);
	}

	public WrappedString(String string) {
		this.string = string;

	}

	public String get() {
		return string;
	}

	public void set(String string) {
		this.string = string;
	}

	@Override
	public String toString() {
		return string;
	}
}