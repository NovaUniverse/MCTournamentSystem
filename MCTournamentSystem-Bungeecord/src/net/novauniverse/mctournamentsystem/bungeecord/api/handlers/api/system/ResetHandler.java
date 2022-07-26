package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.misc.MissingTeamFixer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class ResetHandler extends APIEndpoint {
	public ResetHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		boolean success = true;

		try {
			String sql = "{ CALL reset_data() }";
			CallableStatement cs = TournamentSystemCommons.getDBConnection().getConnection().prepareCall(sql);

			cs.execute();
			cs.close();
		} catch (Exception e) {
			e.printStackTrace();

			success = false;

			json.put("error", "failed");
			json.put("message", e.getClass().getName() + " " + e.getMessage());
		}

		try {
			String sql = "DELETE FROM teams";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();

			success = false;

			json.put("error", "failed");
			json.put("message", e.getClass().getName() + " " + e.getMessage());
		}

		MissingTeamFixer.fixTeams();

		json.put("success", success);

		return json;
	}
}