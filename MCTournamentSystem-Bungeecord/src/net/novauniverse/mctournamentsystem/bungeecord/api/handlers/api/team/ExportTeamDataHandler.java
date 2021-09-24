package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team;

import java.io.IOException;
import java.io.OutputStream;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

@SuppressWarnings("restriction")
public class ExportTeamDataHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		JSONArray teamEntries = new JSONArray();

		try {
			String sql = "SELECT p.uuid AS uuid, p.username AS username, p.team_number AS team_number FROM players AS p LEFT JOIN teams AS t ON t.team_number = p.team_number";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject teamEntry = new JSONObject();

				teamEntry.put("uuid", rs.getString("uuid"));
				teamEntry.put("username", rs.getString("username"));
				teamEntry.put("team_number", rs.getInt("team_number"));

				teamEntries.put(teamEntry);
			}

			rs.close();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		json.put("teams_data", teamEntries);

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}