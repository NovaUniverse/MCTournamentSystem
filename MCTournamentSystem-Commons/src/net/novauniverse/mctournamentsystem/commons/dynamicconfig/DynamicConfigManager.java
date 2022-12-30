package net.novauniverse.mctournamentsystem.commons.dynamicconfig;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONObject;

public class DynamicConfigManager {
	public static int FETCH_TIMEOUT = 10000;
	public static int READ_TIMEOUT = 10000;

	public static final DynamicConfig getDynamicConfig(String dynamicConfigUrl) throws DynamicConfigFetchException {
		JSONObject responseJson;
		try {
			URL url = new URL(dynamicConfigUrl);

			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestProperty("accept", "application/json");
			connection.setRequestProperty("User-Agent", "NovaUniverse Tournament System");

			connection.setConnectTimeout(FETCH_TIMEOUT);
			connection.setReadTimeout(READ_TIMEOUT);
			
			connection.setInstanceFollowRedirects(true);

			connection.connect();

			int code = connection.getResponseCode();
			if (!("" + code).startsWith("2")) {
				throw new DynamicConfigFetchException("Non success status code " + code + " received when trying to fetch config from " + url.toString());
			}

			InputStream responseStream = connection.getInputStream();

			InputStreamReader isr = new InputStreamReader(responseStream);
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

			responseJson = new JSONObject(response.toString());

		} catch (Exception e) {
			throw new DynamicConfigFetchException("Failed to fetch dynamic config from " + dynamicConfigUrl, e);
		}

		try {
			Map<Integer, String> teamColors = new HashMap<>();
			Map<Integer, String> teamNames = new HashMap<>();

			if (responseJson.has("team_colors")) {
				JSONObject teamColorData = responseJson.getJSONObject("team_colors");
				teamColorData.keySet().forEach(key -> {
					Integer team = Integer.parseInt(key);
					String color = teamColorData.getString(key);

					teamColors.put(team, color);
				});
			}

			if (responseJson.has("team_names")) {
				JSONObject teamNameData = responseJson.getJSONObject("team_names");
				teamNameData.keySet().forEach(key -> {
					Integer team = Integer.parseInt(key);
					String name = teamNameData.getString(key);

					teamNames.put(team, name);
				});
			}

			return new DynamicConfig(teamColors, teamNames);
		} catch (Exception e) {
			throw new DynamicConfigFetchException("Failed to parse dynamic config", e);
		}
	}
}