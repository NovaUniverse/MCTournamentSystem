package net.novauniverse.mctournamentsystem.commons.team;

public class TeamNameProvider {
	public static String getDisplayName(int teamNumber) {
		if (TeamOverrides.nameOverrides.containsKey(teamNumber)) {
			return TeamOverrides.nameOverrides.get(teamNumber);
		}

		return "Team " + teamNumber;
	}
}