package net.novauniverse.mctournamentsystem.spigot.team;

import dev.lone.itemsadder.api.FontImages.FontImageWrapper;
import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.team.TeamColorProvider;
import net.novauniverse.mctournamentsystem.commons.team.TeamNameProvider;
import net.novauniverse.mctournamentsystem.commons.team.TeamOverrides;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerNameCache;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.spigot.teams.Team;

public class TournamentSystemTeam extends Team {
	private int teamNumber;
	private int score;
	private int kills;

	private String badge;

	public TournamentSystemTeam(int teamNumber, int score, int kills) {
		this.teamNumber = teamNumber;
		this.score = score;
		this.badge = null;
		this.kills = kills;
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

	public int getKills() {
		return kills;
	}

	public void setKills(int kills) {
		this.kills = kills;
	}

	/**
	 * Add score distributed to members. Note that this does not add the score to
	 * the team
	 * 
	 * @param score The amount to distribute
	 */
	public void distributePointsToPlayers(int score, String reason) {
		int toAdd = (int) Math.floor(score / members.size());
		members.forEach(uuid -> ScoreManager.getInstance().addPlayerScore(uuid, toAdd, false, reason));
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
		return TeamColorProvider.getTeamColor(teamNumber);
	}

	@Override
	public String getDisplayName() {
		return TeamNameProvider.getDisplayName(teamNumber);
	}

	public boolean hadBadge() {
		return badge != null;
	}

	public String getBadge() {
		return badge;
	}

	public void updateBadge() {
		if (TeamOverrides.badges.containsKey(teamNumber)) {
			String namespace = TeamOverrides.badges.get(teamNumber);
			try {
				String badgeString = new FontImageWrapper(namespace).getString();
				badge = badgeString;
				return;
			} catch (Exception e) {
				Log.error("TournamentSystemTeam", "Failed to parse font image " + namespace + ". " + e.getClass().getName() + " " + e.getMessage());
			}
		}
		badge = null;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof TournamentSystemTeam) {
			TournamentSystemTeam team2 = (TournamentSystemTeam) obj;

			return team2.getTeamNumber() == this.getTeamNumber();
		}

		if (obj instanceof Team) {
			Team team2 = (Team) obj;

			return this.getTeamUuid().equals(team2.getTeamUuid());
		}

		return this == obj;
	}
}