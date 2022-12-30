package net.novauniverse.mctournamentsystem.commons.dynamicconfig;

import java.util.Map;

public class DynamicConfig {
	private final Map<Integer, String> teamColors;
	private final Map<Integer, String> teamNames;

	public DynamicConfig(Map<Integer, String> teamColors, Map<Integer, String> teamNames) {
		this.teamColors = teamColors;
		this.teamNames = teamNames;
	}

	public Map<Integer, String> getTeamColors() {
		return teamColors;
	}

	public Map<Integer, String> getTeamNames() {
		return teamNames;
	}
}