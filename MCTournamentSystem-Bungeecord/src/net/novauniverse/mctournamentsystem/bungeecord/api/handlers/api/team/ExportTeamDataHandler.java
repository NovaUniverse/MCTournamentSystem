package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

import com.sun.net.httpserver.HttpExchange;

@SuppressWarnings("restriction")
public class ExportTeamDataHandler extends APIEndpoint {
	public ExportTeamDataHandler() {
		super(true);
	}
	
	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
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
		
		return json;
	}
}