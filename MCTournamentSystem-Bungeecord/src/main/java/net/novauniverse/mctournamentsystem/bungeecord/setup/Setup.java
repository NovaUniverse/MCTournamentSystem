package net.novauniverse.mctournamentsystem.bungeecord.setup;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class Setup {
	public static void run() {
		String initialName = System.getProperty("tsInitialName");
		if (initialName != null) {
			if (TournamentSystemCommons.getTournamentName() == null) {
				Log.info("Setup", "Setting name to " + initialName + " specified by the -DtsInitialName parameter");
				TournamentSystemCommons.setTournamentName(initialName);
			}
		}

		try {
			String initialMOTD = System.getProperty("tsInitialMOTD");
			if (initialMOTD != null) {
				if (TournamentSystemCommons.getConfigValue("motd") == null) {
					Log.info("Setup", "Setting MOTD to " + initialMOTD + " specified by the -DtsInitialMOTD parameter");
					TournamentSystemCommons.setConfigValue("motd", initialMOTD);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String initialURL = System.getProperty("tsInitialURL");
		if (initialURL != null) {
			if (TournamentSystemCommons.getScoreboardURL() == null) {
				Log.info("Setup", "Setting scoreboard url to " + initialURL + " specified by the -DtsInitialURL parameter");
				TournamentSystemCommons.setScoreboardURL(initialURL);
			}
		}
	}
}