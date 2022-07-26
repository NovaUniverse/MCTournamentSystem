package net.novauniverse.mctournamentsystem.spigot.game;

import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;

public class GameSpecificTelementryModule {
	private Class<? extends ITelementryMetadataProvider> providerClass;
	private String gameName;

	public GameSpecificTelementryModule(String gameName, Class<? extends ITelementryMetadataProvider> providerClass) {
		this.gameName = gameName;
		this.providerClass = providerClass;
	}

	public Class<? extends ITelementryMetadataProvider> getProviderClass() {
		return providerClass;
	}

	public String getGameName() {
		return gameName;
	}
}