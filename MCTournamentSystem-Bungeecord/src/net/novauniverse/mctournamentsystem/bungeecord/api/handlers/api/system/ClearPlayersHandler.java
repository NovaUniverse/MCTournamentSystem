package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.sql.PreparedStatement;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.misc.MissingTeamFixer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class ClearPlayersHandler extends APIEndpoint {
	public ClearPlayersHandler() {
		super(true);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.CLEAR_DATA;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
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

		MissingTeamFixer.fixTeams();

		return json;
	}
}