package net.novauniverse.mctournamentsystem.spigot.team;

import net.md_5.bungee.api.ChatColor;
import net.novauniverse.mctournamentsystem.commons.team.TeamColorProvider;
import net.novauniverse.mctournamentsystem.spigot.TournamentSystem;
import net.novauniverse.mctournamentsystem.spigot.modules.cache.PlayerNameCache;
import net.novauniverse.mctournamentsystem.spigot.score.ScoreManager;
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

	/**
	 * Add score distributed to members. Note that this does not add the score to
	 * the team
	 * 
	 * @param score The amount to distribute
	 */
	public void distributePointsToPlayers(int score) {
		int toAdd = (int) Math.floor(score / members.size());
		members.forEach(uuid -> ScoreManager.getInstance().addPlayerScore(uuid, toAdd, false));
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
		if (TournamentSystem.getInstance().getTeamNameOverrides().containsKey(teamNumber)) {
			return TournamentSystem.getInstance().getTeamNameOverrides().get(teamNumber);
		}
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