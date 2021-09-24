package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class ClearPlayersHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		try {
			String sql = "DELETE FROM players";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.executeUpdate();

			ps.close();

			json.put("success", true);
		} catch (Exception e) {
			json.put("success", false);
			json.put("error", e.getClass().getName());
			json.put("message", e.getClass().getName() + " " + e.getMessage());
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}