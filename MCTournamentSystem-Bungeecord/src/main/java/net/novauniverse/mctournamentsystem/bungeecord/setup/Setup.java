package net.novauniverse.mctournamentsystem.bungeecord.setup;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class Setup {
	public static void run() {
		String initialName = System.getProperty("tsInitialName");
		if (initialName != null) {
			if (TournamentSystemCommons.getTournamentName() == null) {
				if (initialName.trim().length() > 0) {
					Log.info("Setup", "Setting name to " + initialName + " specified by the -DtsInitialName parameter");
					TournamentSystemCommons.setTournamentName(initialName);
				}
			}
		}

		try {
			String initialMOTD = System.getProperty("tsInitialMOTD");
			if (initialMOTD != null) {
				if (TournamentSystemCommons.getConfigValue("motd") == null) {
					if (initialMOTD.trim().length() > 0) {
						Log.info("Setup", "Setting MOTD to " + initialMOTD + " specified by the -DtsInitialMOTD parameter");
						TournamentSystemCommons.setConfigValue("motd", initialMOTD);
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		String initialURL = System.getProperty("tsInitialURL");
		if (initialURL != null) {
			if (TournamentSystemCommons.getScoreboardURL() == null) {
				if (initialURL.trim().length() > 0) {
					Log.info("Setup", "Setting scoreboard url to " + initialURL + " specified by the -DtsInitialURL parameter");
					TournamentSystemCommons.setScoreboardURL(initialURL);
				}
			}
		}
	}

	public static void importFromURL(String url) throws IOException {
		String tournamentName = TournamentSystemCommons.getTournamentName();
		String scoreboardUrl = TournamentSystemCommons.getScoreboardURL();
		String motd = null;
		try {
			motd = TournamentSystemCommons.getConfigValue("motd");
		} catch (Exception e) {
			e.printStackTrace();
		}

		if (tournamentName == null || motd == null || scoreboardUrl == null) {
			Log.debug("Setup", "Trying to read config from " + url);
			URL urlObj = new URL(url);

			HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
			connection.setRequestProperty("accept", "application/json");
			connection.setRequestProperty("User-Agent", "NovaUniverse Tournament System");

			connection.setConnectTimeout(10000);
			connection.setReadTimeout(10000);

			connection.connect();

			InputStream responseStream = connection.getInputStream();

			InputStreamReader isr = new InputStreamReader(responseStream, "UTF-8");
			BufferedReader rd = new BufferedReader(isr);
			StringBuilder response = new StringBuilder();
			String line;
			while ((line = rd.readLine()) != null) {
				response.append(line);
				response.append('\r');
			}
			rd.close();
			isr.close();
			responseStream.close();
			connection.disconnect();

			JSONObject responseJson = new JSONObject(response.toString());

			if (responseJson.has("scoreboard_url")) {
				if (scoreboardUrl == null) {
					TournamentSystemCommons.setScoreboardURL(responseJson.getString("scoreboard_url"));
				}
			}

			if (responseJson.has("tournament_name")) {
				if (tournamentName == null) {
					TournamentSystemCommons.setTournamentName(responseJson.getString("tournament_name"));
				}
			}

			if (responseJson.has("motd")) {
				if (motd == null) {
					try {
						TournamentSystemCommons.setConfigValue("motd", responseJson.getString("motd"));
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		} else {
			Log.debug("Setup", "No need to import config since values has already been set");
		}
	}
}