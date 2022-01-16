package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.whitelist;

import java.sql.PreparedStatement;
import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class AddWhitelistHandler extends APIEndpoint {
	public AddWhitelistHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		if (params.containsKey("uuid")) {
			try {
				UUID uuid = UUID.fromString(params.get("uuid"));

				String sql = "REPLACE INTO whitelist (id, uuid) VALUES(null, ?)";
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, uuid.toString());

				ps.executeUpdate();
				ps.close();
				
				json.put("success", true);
			} catch (IllegalArgumentException e) {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "invalid uuid provided");
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: uuid");
		}

		return json;
	}
}