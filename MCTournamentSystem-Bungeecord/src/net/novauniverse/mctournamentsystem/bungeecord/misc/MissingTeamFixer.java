package net.novauniverse.mctournamentsystem.bungeecord.misc;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class MissingTeamFixer {
	public static void fixTeams() {
		int teamCount = TournamentSystem.getInstance().getTeamSize();

		List<Integer> missingTeams = new ArrayList<Integer>();
		for (int i = 0; i < teamCount; i++) {
			missingTeams.add((Integer) i + 1);
		}

		try {
			String sql = "SELECT team_number FROM teams";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ResultSet rs = ps.executeQuery();
			while (rs.next()) {
				int teamNumber = rs.getInt("team_number");
				if (missingTeams.contains((Integer) teamNumber)) {
					missingTeams.remove((Integer) teamNumber);
				}
			}

			rs.close();
			ps.close();
		} catch (Exception ee) {
			ee.printStackTrace();
		}

		missingTeams.forEach(i -> {
			try {
				String sql = "INSERT INTO teams (team_number) VALUES (?)";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setInt(1, i);

				ps.execute();

				ps.close();
			} catch (Exception ee) {
				ee.printStackTrace();
			}
		});
	}
}