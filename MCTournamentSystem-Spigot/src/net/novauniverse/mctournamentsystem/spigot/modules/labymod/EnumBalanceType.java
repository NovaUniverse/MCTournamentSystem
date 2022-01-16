package net.novauniverse.mctournamentsystem.spigot.modules.labymod;

public enum EnumBalanceType {
	CASH("cash"), BANK("bank");

	private final String key;

	EnumBalanceType(String key) {
		this.key = key;
	}

	public String getKey() {
		return this.key;
	}
}