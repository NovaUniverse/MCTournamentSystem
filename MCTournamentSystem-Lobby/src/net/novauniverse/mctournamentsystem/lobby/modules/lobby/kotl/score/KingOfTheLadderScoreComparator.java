package net.novauniverse.mctournamentsystem.lobby.modules.lobby.kotl.score;

import java.util.Comparator;

public class KingOfTheLadderScoreComparator implements Comparator<KingOfTheLadderScore> {
	@Override
	public int compare(KingOfTheLadderScore o1, KingOfTheLadderScore o2) {
		return o2.getScore() - o1.getScore();
	}
}