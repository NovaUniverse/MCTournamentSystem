package net.novauniverse.mctournamentsystem.commons;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.FileUtils;
import org.json.JSONObject;

import net.zeeraa.novacore.commons.log.Log;

// License check system
public class LCS {
	private static boolean valid = false;
	private static boolean expired = false;
	private static boolean demo = false;
	private static String licensedTo = null;
	private static String expiresAt = null;

	public static boolean connectivityCheck() {
		JSONObject json = LCS.getResource("https://novauniverse.net/api/connectivity_check/");
		if (json != null) {
			return json.getBoolean("success");
		}
		return false;
	}

	public static boolean check(File file) {
		Log.info("Checking license for TournamentSystem");
		String key;
		try {
			key = FileUtils.readFileToString(file, StandardCharsets.UTF_8);
		} catch (IOException e) {
			Log.error("Failed to read license key from file " + file.getAbsolutePath());
			return false;
		}

		JSONObject json = LCS.getResource("https://novauniverse.net/api/license/tournament_system/" + key);

		if (json == null) {
			Log.error("Failed to connect to the license servers");
			return false;
		}

		if (!json.getBoolean("success")) {
			Log.error("Invalid license key. Please check that you have received a valid one");
			return false;
		}

		JSONObject data = json.getJSONObject("data");

		LCS.valid = true;
		LCS.expiresAt = data.getString("expires_at");

		if (!data.getBoolean("is_active")) {
			Log.error("License key expired. join our discord for novauniverse.net and open a ticket to get support regarding this");
			LCS.expired = true;
			return false;
		}

		demo = data.getBoolean("is_demo");

		LCS.licensedTo = data.getString("owner");

		if (demo) {
			Log.warn("Your license key is a demo key. Only use this key for evaluation purposes");
		}

		Log.success("License key valid");

		return true;
	}

	private static JSONObject getResource(String urlString) {
		try {
			URL url = new URL(urlString);
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("GET");
			connection.setRequestProperty("User-Agent", "MinecraftTournamentSystem");
			connection.setRequestProperty("accept", "application/json");

			connection.getResponseCode();

			BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String inputLine;
			StringBuffer response = new StringBuffer();
			while ((inputLine = in.readLine()) != null) {
				response.append(inputLine);
			}
			in.close();

			return new JSONObject(response.toString());
		} catch (IOException e) {
			return null;
		}
	}

	public static boolean isValid() {
		return valid;
	}

	public static boolean isExpired() {
		return expired;
	}

	public static String getLicensedTo() {
		return licensedTo;
	}

	public static String getExpiresAt() {
		return expiresAt;
	}

	public static boolean isDemo() {
		return demo;
	}
}