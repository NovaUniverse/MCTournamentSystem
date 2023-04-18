package net.novauniverse.mctournamentsystem.spigot.game.gamespecific.turfwars;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import net.novauniverse.game.turfwars.game.team.teampopulator.TurfWarsTeamPopulator;
import net.novauniverse.mctournamentsystem.spigot.score.TeamScoreData;
import net.novauniverse.mctournamentsystem.spigot.score.TopScore;
import net.zeeraa.novacore.commons.log.Log;
import net.zeeraa.novacore.commons.utils.Pair;

public class TournamentSystemTurfWarsTeamPopulator implements TurfWarsTeamPopulator {
	@Override
	public Pair<List<Player>> populateTeams() {
		List<Player> team1 = new ArrayList<>();
		List<Player> team2 = new ArrayList<>();

		boolean addToTeam1 = true;

		List<TeamScoreData> topTeams = TopScore.getTeamTopScore(Integer.MAX_VALUE);
		for (TeamScoreData team : topTeams) {
			if (team.getTeam().getOnlinePlayers().size() == 0) {
				Log.debug("TurfWarsTeamPopulator", "Skip team " + team.getTeam().getDisplayName() + " since its empty");
				continue;
			}

			Log.debug("TurfWarsTeamPopulator", "Assign team " + team.getTeam().getDisplayName() + " to team " + (addToTeam1 ? "1" : "2"));

			for (Player player : team.getTeam().getOnlinePlayers()) {
				if (addToTeam1) {
					team1.add(player);
				} else {
					team2.add(player);
				}
			}

			addToTeam1 = !addToTeam1;
		}

		return new Pair<List<Player>>(team1, team2);
	}
}