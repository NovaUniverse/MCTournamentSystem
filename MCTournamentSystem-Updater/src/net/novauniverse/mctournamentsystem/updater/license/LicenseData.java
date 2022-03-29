package net.novauniverse.mctournamentsystem.updater.license;

public class LicenseData {
	private boolean valid;
	private boolean active;
	private boolean demo;
	private String owner;

	public LicenseData(boolean valid, boolean active, boolean demo, String owner) {
		this.valid = valid;
		this.active = active;
		this.demo = demo;
		this.owner = owner;
	}

	public boolean isValid() {
		return valid;
	}

	public boolean isActive() {
		return active;
	}

	public boolean isDemo() {
		return demo;
	}

	public String getOwner() {
		return owner;
	}
}
