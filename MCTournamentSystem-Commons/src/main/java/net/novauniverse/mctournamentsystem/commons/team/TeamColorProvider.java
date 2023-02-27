package net.novauniverse.mctournamentsystem.commons.team;

import net.md_5.bungee.api.ChatColor;

public class TeamColorProvider {
	public static ChatColor getTeamColor(int teamNumber) {
		if (TeamOverrides.colorOverrides.containsKey(teamNumber)) {
			return TeamOverrides.colorOverrides.get(teamNumber);
		}

		switch (((teamNumber - 1) % 15) + 1) {

		case 1:
			return ChatColor.DARK_BLUE;

		case 2:
			return ChatColor.DARK_GREEN;

		case 3:
			return ChatColor.DARK_AQUA;

		case 4:
			return ChatColor.DARK_RED;

		case 5:
			return ChatColor.DARK_PURPLE;

		case 6:
			return ChatColor.GOLD;

		case 7:
			return ChatColor.GRAY;

		case 8:
			return ChatColor.DARK_GRAY;

		case 9:
			return ChatColor.BLUE;

		case 10:
			return ChatColor.GREEN;

		case 11:
			return ChatColor.AQUA;

		case 12:
			return ChatColor.RED;

		case 13:
			return ChatColor.LIGHT_PURPLE;

		case 14:
			return ChatColor.YELLOW;

		case 15:
			return ChatColor.WHITE;

		default:
			return ChatColor.BLACK;
		}
	}
}