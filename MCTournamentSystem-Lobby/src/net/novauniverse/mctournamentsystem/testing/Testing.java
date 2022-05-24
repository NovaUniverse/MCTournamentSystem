package net.novauniverse.mctournamentsystem.testing;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.novauniverse.mctournamentsystem.lobby.modules.halloffame.ResultFetcher;
import net.novauniverse.mctournamentsystem.lobby.modules.halloffame.TournamentResult;
import net.novauniverse.mctournamentsystem.lobby.modules.halloffame.TournamentTeamResult;

public class Testing {

	public static void main(String[] args) {
		try {
			List<TournamentResult> result = ResultFetcher.fetch("https://novauniverse.net/api/mcf/result/");
			result.forEach(r -> {
				System.out.println("----- " + r.getDisplayName() + " -----");
				r.getTeams().forEach(t -> {
					System.out.println("Team " + t.getTeamNumber() + ": ");
					System.out.println("Score: " + t.getScore());
				});
				TournamentTeamResult top = Collections.max(r.getTeams(), Comparator.comparing(s -> s.getScore()));
				System.out.println("Top team is " + top.getTeamNumber() + " with " + top.getScore() + " points");
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}