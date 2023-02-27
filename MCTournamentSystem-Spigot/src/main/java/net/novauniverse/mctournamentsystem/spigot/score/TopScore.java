package net.novauniverse.mctournamentsystem.spigot.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;
import net.zeeraa.novacore.spigot.NovaCore;
import net.zeeraa.novacore.spigot.teams.TeamManager;

public class TopScore {
	public static List<PlayerScoreData> getPlayerTopScore(int maxEntries) {
		List<PlayerScoreData> result = new ArrayList<PlayerScoreData>();

		ScoreManager.getInstance().getPlayerScoreCache().keySet().forEach(uuid -> {
			if (TeamManager.hasTeamManager()) {
				if (NovaCore.getInstance().getTeamManager().getPlayerTeam(uuid) != null) {
					PlayerScoreData scoreData = new PlayerScoreData(uuid, ScoreManager.getInstance().getPlayerScore(uuid));
					result.add(scoreData);
				}
			} else {
				PlayerScoreData scoreData = new PlayerScoreData(uuid, ScoreManager.getInstance().getPlayerScore(uuid));
				result.add(scoreData);
			}
		});

		Collections.sort(result);

		while (result.size() > maxEntries) {
			result.remove(result.size() - 1);
		}

		return result;
	}

	public static List<TeamScoreData> getTeamTopScore(int maxEntries) {
		List<TeamScoreData> result = new ArrayList<TeamScoreData>();

		if (NovaCore.getInstance().hasTeamManager()) {
			NovaCore.getInstance().getTeamManager().getTeams().forEach(team -> {
				TeamScoreData scoreData = new TeamScoreData((TournamentSystemTeam) team);
				result.add(scoreData);
			});
			Collections.sort(result);
		}

		while (result.size() > maxEntries) {
			result.remove(result.size() - 1);
		}

		return result;
	}
}