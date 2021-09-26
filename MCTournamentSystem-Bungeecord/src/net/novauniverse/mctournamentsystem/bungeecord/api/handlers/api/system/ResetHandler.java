package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.sql.CallableStatement;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class ResetHandler extends APIEndpoint {
	public ResetHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		try {
			String sql = "{ CALL reset_data() }";
			CallableStatement cs = TournamentSystemCommons.getDBConnection().getConnection().prepareCall(sql);

			cs.execute();
			cs.close();

			json.put("success", true);
		} catch (Exception e) {
			e.printStackTrace();

			json.put("success", false);
			json.put("error", "failed");
			json.put("message", e.getClass().getName() + " " + e.getMessage());
		}

		return json;
	}
}