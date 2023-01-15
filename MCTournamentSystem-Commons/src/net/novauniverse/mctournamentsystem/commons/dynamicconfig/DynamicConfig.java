package net.novauniverse.mctournamentsystem.commons.dynamicconfig;

import java.util.Map;

public class DynamicConfig {
	private final Map<Integer, String> teamColors;
	private final Map<Integer, String> teamNames;
	private final Map<Integer, String> teamBadges;

	public DynamicConfig(Map<Integer, String> teamColors, Map<Integer, String> teamNames, Map<Integer, String> teamBadges) {
		this.teamColors = teamColors;
		this.teamNames = teamNames;
		this.teamBadges = teamBadges;
	}

	public Map<Integer, String> getTeamColors() {
		return teamColors;
	}

	public Map<Integer, String> getTeamNames() {
		return teamNames;
	}

	public Map<Integer, String> getTeamBadges() {
		return teamBadges;
	}
}