package net.novauniverse.mctournamentsystem.lobby.modules.halloffame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

public class ResultFetcher {
	public static final int FETCH_TIMEOUT = 6; // 6 seconds

	public static List<TournamentResult> fetch(String url) throws IOException {
		List<TournamentResult> result = new ArrayList<>();

		URL u = new URL(url);
		HttpURLConnection connection = (HttpURLConnection) u.openConnection();
		connection.setRequestMethod("GET");
		connection.setRequestProperty("User-Agent", "Mozilla/5.0");
		connection.setRequestProperty("accept", "application/json");

		connection.getResponseCode();

		connection.setConnectTimeout(FETCH_TIMEOUT * 1000);
		connection.setReadTimeout(FETCH_TIMEOUT * 1000);

		BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();

		JSONArray weeks = new JSONArray(response.toString());
		for (int i = 0; i < weeks.length(); i++) {
			JSONObject week = weeks.getJSONObject(i);

			List<TournamentTeamResult> teams = new ArrayList<TournamentTeamResult>();

			JSONArray teamsJSON = week.getJSONArray("teams");
			JSONArray playersJSON = week.getJSONArray("players");
			for (int j = 0; j < teamsJSON.length(); j++) {
				JSONObject teamJSON = teamsJSON.getJSONObject(j);
				int teamNumber = teamJSON.getInt("team_number");
				int teamScore = teamJSON.getInt("team_score");

				List<TournamentPlayer> players = new ArrayList<>();
				for (int k = 0; k < playersJSON.length(); k++) {
					JSONObject playerJSON = playersJSON.getJSONObject(k);
					if (playerJSON.getInt("team_number") == teamNumber) {
						UUID uuid = UUID.fromString(playerJSON.getString("uuid"));
						String username = playerJSON.getString("username");
						int score = playerJSON.getInt("score");
						int kills = playerJSON.getInt("kills");
						players.add(new TournamentPlayer(uuid, username, score, kills));
					}
				}
				teams.add(new TournamentTeamResult(teamNumber, teamScore, players));
			}

			result.add(new TournamentResult(week.getString("display_name"), teams));
		}

		return result;
	}
}