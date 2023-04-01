package net.novauniverse.mctournamentsystem.spigot.team;

import java.util.List;

import net.novauniverse.mctournamentsystem.spigot.score.TeamScoreData;
import net.novauniverse.mctournamentsystem.spigot.score.TopScore;
import net.zeeraa.novacore.commons.utils.Pair;
import net.zeeraa.novacore.spigot.teams.Team;

public class DefaultTopTeamProvider {
	public static final Pair<Team> getTopParticipants() {
		List<TeamScoreData> scoreData = TopScore.getTeamTopScore(2);

		Team team1 = scoreData.remove(0).getTeam();
		Team team2 = scoreData.remove(0).getTeam();

		return new Pair<Team>(team1, team2);
	}
}