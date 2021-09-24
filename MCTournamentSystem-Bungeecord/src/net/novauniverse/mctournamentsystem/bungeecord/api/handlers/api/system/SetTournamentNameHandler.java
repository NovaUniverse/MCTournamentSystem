package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.api.WebServer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

@SuppressWarnings("restriction")
public class SetTournamentNameHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		if (params.containsKey("name")) {
			String name = URLDecoder.decode(params.get("name"), StandardCharsets.UTF_8.name());
			Log.info("SetTournamentNameHandler", "Reanaming tournament to " + name);
			try {
				TournamentSystemCommons.setTournamentName(name);
				json.put("success", true);
			} catch (Exception e) {
				json.put("success", false);
				json.put("error", e.getClass().getName());
				json.put("message", e.getClass().getName() + " " + e.getMessage());
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: name");
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}