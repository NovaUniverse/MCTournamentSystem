package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.team;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ExportTeamDataHandler extends TournamentEndpoint {
	public ExportTeamDataHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		JSONArray teamEntries = new JSONArray();

		String sql = "SELECT p.metadata AS metadata, p.uuid AS uuid, p.username AS username, p.team_number AS team_number FROM players AS p LEFT JOIN teams AS t ON t.team_number = p.team_number";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			JSONObject teamEntry = new JSONObject();

			teamEntry.put("uuid", rs.getString("uuid"));
			teamEntry.put("username", rs.getString("username"));
			teamEntry.put("team_number", rs.getInt("team_number"));

			JSONObject metadata = new JSONObject();
			String metadataString = rs.getString("metadata");
			if (metadataString != null) {
				metadata = new JSONObject(metadataString);
			}

			teamEntry.put("metadata", metadata);

			teamEntries.put(teamEntry);
		}

		rs.close();
		ps.close();

		json.put("teams_data", teamEntries);

		return new JSONResponse(json);
	}
}