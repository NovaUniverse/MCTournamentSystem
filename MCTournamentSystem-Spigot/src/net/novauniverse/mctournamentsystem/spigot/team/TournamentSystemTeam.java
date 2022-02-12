package net.novauniverse.mctournamentsystem.spigot.team;

import org.bukkit.ChatColor;

import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerNameCache;
import net.zeeraa.novacore.commons.utils.UUIDUtils;
import net.zeeraa.novacore.spigot.teams.Team;

public class TournamentSystemTeam extends Team {
	private int teamNumber;
	private int score;

	public TournamentSystemTeam(int teamNumber, int score) {
		this.teamNumber = teamNumber;
		this.score = score;
	}

	public int getTeamNumber() {
		return teamNumber;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public String getMemberString() {
		String result = "";

		for (int i = members.size(); i > 0; i--) {
			result += PlayerNameCache.getInstance().getPlayerName(members.get(i - 1)) + (i == 1 ? "" : (i == 2 ? " and " : ", "));
		}

		return result;
	}

	@Override
	public ChatColor getTeamColor() {
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

	@Override
	public String getDisplayName() {
		return "Team " + this.getTeamNumber();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TournamentSystemTeam) {
			TournamentSystemTeam team2 = (TournamentSystemTeam) obj;

			return team2.getTeamNumber() == this.getTeamNumber();
		}

		if (obj instanceof Team) {
			Team team2 = (Team) obj;

			return UUIDUtils.isSame(this.getTeamUuid(), team2.getTeamUuid());
		}

		return this == obj;
	}
}