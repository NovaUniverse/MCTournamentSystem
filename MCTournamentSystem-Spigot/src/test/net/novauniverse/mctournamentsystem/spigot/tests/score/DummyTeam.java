package net.novauniverse.mctournamentsystem.spigot.tests.score;

import net.novauniverse.mctournamentsystem.spigot.team.TournamentSystemTeam;

public class DummyTeam extends TournamentSystemTeam {
	public DummyTeam(int teamNumber, int score, int kills) {
		super(teamNumber, score, kills);
	}
}