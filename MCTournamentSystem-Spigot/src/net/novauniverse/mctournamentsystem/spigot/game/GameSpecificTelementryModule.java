package net.novauniverse.mctournamentsystem.spigot.game;

import net.novauniverse.mctournamentsystem.spigot.modules.telementry.metadata.ITelementryMetadataProvider;

public class GameSpecificTelementryModule {
	private Class<? extends ITelementryMetadataProvider> providerClass;
	private String gameName;
	private boolean sensitive;

	public GameSpecificTelementryModule(String gameName, Class<? extends ITelementryMetadataProvider> providerClass) {
		this(gameName, providerClass, false);
	}

	public GameSpecificTelementryModule(String gameName, Class<? extends ITelementryMetadataProvider> providerClass, boolean sensitive) {
		this.gameName = gameName;
		this.providerClass = providerClass;
		this.sensitive = sensitive;
	}

	public Class<? extends ITelementryMetadataProvider> getProviderClass() {
		return providerClass;
	}

	public String getGameName() {
		return gameName;
	}

	public boolean isSensitive() {
		return sensitive;
	}
}