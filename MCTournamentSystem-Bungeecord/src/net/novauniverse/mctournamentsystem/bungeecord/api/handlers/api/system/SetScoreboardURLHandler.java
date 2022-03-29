package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class SetScoreboardURLHandler extends APIEndpoint {
	public SetScoreboardURLHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("url")) {
			String url = URLDecoder.decode(params.get("url"), StandardCharsets.UTF_8.name());
			Log.info("SetScoreboardURL", "Setting scoreboard url to " + url);
			try {
				TournamentSystemCommons.setScoreboardURL(url);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: url");
		}

		return json;
	}
}