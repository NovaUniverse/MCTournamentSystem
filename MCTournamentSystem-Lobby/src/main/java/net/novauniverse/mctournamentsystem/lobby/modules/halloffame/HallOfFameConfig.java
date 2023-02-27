package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import java.util.List;

import net.zeeraa.novacore.spigot.utils.XYZLocation;

public class HallOfFameConfig {
	private XYZLocation nameHologramLocation;

	private List<HallOfFameNPC> npcs;

	private String url;

	private boolean debug;

	public HallOfFameConfig(XYZLocation nameHologramLocation, List<HallOfFameNPC> npcs, String url, boolean debug) {
		this.nameHologramLocation = nameHologramLocation;
		this.npcs = npcs;
		this.url = url;
		this.debug = debug;
	}

	public XYZLocation getNameHologramLocation() {
		return nameHologramLocation;
	}

	public List<HallOfFameNPC> getNpcs() {
		return npcs;
	}

	public String getUrl() {
		return url;
	}

	public boolean isDebug() {
		return debug;
	}
}