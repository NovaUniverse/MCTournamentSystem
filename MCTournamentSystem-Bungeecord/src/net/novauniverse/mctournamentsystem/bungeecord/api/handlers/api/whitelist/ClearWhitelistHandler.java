package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.whitelist;

import java.sql.PreparedStatement;
import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class ClearWhitelistHandler extends APIEndpoint {
	public ClearWhitelistHandler() {
		super(true);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.MANAGE_WHITELIST;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		String sql = "TRUNCATE whitelist";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
		ps.executeUpdate();
		ps.close();

		json.put("success", true);

		return json;
	}
}