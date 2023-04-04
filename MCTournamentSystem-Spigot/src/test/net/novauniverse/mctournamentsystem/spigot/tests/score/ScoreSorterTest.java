package net.novauniverse.mctournamentsystem.spigot.tests.score;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import org.junit.Test;

import net.novauniverse.mctournamentsystem.spigot.score.PlayerScoreData;
import net.novauniverse.mctournamentsystem.spigot.score.TeamScoreData;

public class ScoreSorterTest {
	@Test
	public void sortPlayerScore() {
		List<PlayerScoreData> score = new ArrayList<>();

		score.add(new PlayerScoreData(UUID.fromString("e4c3e99a-b5b1-4377-bc2f-c47a5b6a729e"), 1337));
		score.add(new PlayerScoreData(UUID.fromString("bff4f032-ae89-4af9-b838-b1a77e748783"), 420));
		score.add(new PlayerScoreData(UUID.fromString("e4c3e99a-b5b1-4377-bc2f-c47a5b6a729e"), 69));
		score.add(new PlayerScoreData(UUID.fromString("bff4f032-ae89-4af9-b838-b1a77e748783"), 42));
		score.add(new PlayerScoreData(UUID.fromString("680ad79f-8e67-46a4-aac8-4d508eab1f2c"), 123));
		score.add(new PlayerScoreData(UUID.fromString("11f462b7-c204-4061-9138-89557fc332b0"), 5000));

		Collections.sort(score);

		assert score.get(0).getUuid().toString().equalsIgnoreCase("11f462b7-c204-4061-9138-89557fc332b0") : "First entry in list is not player with highest score";
		assert score.get(5).getUuid().toString().equalsIgnoreCase("bff4f032-ae89-4af9-b838-b1a77e748783") : "Last entry in list is not player with lowest score";
	}

	@Test
	public void sortTeamScore() {
		List<TeamScoreData> score = new ArrayList<>();

		score.add(new TeamScoreData(new DummyTeam(1, 42, 0)));
		score.add(new TeamScoreData(new DummyTeam(2, 1337, 0)));
		score.add(new TeamScoreData(new DummyTeam(4, 69, 0)));
		score.add(new TeamScoreData(new DummyTeam(5, 420, 0)));

		Collections.sort(score);
		
		assert score.get(0).getTeamNumber() == 2 : "First entry in list is not team with highest score";
		assert score.get(3).getTeamNumber() == 1 : "First entry in list is not team with lowest score";
	}
}