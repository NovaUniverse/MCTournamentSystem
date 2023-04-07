package net.novauniverse.mctournamentsystem.commons.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.stream.Collectors;

import org.json.JSONException;
import org.json.JSONObject;

public class TournamentSystemAPI {
	private String baseUrl;
	private String accessToken;
	private int timeout;

	public TournamentSystemAPI(String baseUrl, String accessToken, int timeout) {
		this.baseUrl = baseUrl;
		this.accessToken = accessToken;
		this.timeout = timeout;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public int getTimeout() {
		return timeout;
	}

	public void setTimeout(int timeout) {
		this.timeout = timeout;
	}

	private void makePOSTRequest(String endpoint, String body) throws IOException, TournamentSystemAPIException {
		URL url = new URL(baseUrl + endpoint);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setConnectTimeout(timeout);
		connection.setReadTimeout(timeout);
		connection.setInstanceFollowRedirects(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json");
		connection.setRequestProperty("charset", "utf-8");
		connection.setRequestProperty("Authorization", "Bearer " + accessToken);
		connection.connect();

		OutputStream os = connection.getOutputStream();
		byte[] input = "".getBytes("utf-8");
		os.write(input, 0, input.length);
		os.flush();
		os.close();

		int httpResult = connection.getResponseCode();

		BufferedReader br = null;
		if (100 <= connection.getResponseCode() && connection.getResponseCode() <= 399) {
			br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
		} else {
			br = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
		}

		JSONObject response = null;
		try {
			response = new JSONObject(br.lines().collect(Collectors.joining()));
		} catch (JSONException e) {
		}

		if (httpResult != 200) {
			String message = "API Returned http error: " + httpResult;
			if (response != null) {
				if (response.has("message")) {
					message = "HTTP " + httpResult + ": " + response.getString("message");
				} else {
					message = "HTTP " + httpResult + ": " + response.toString();
				}
			}

			throw new TournamentSystemAPIException(message);
		}
	}

	public void startServer(String name) throws IOException, TournamentSystemAPIException {
		String endpoint = "/api/v1/servers/start?server=" + name;
		makePOSTRequest(endpoint, "");
	}

	public void killServer(String name) throws IOException, TournamentSystemAPIException {
		String endpoint = "/api/v1/servers/stop?server=" + name;
		makePOSTRequest(endpoint, "");
	}
}