package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.reset;

import java.sql.PreparedStatement;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.bungeecord.misc.MissingTeamFixer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class ResetHandler extends APIEndpoint {
	public ResetHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.DELETE);
	}

	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.CLEAR_DATA;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();

		boolean success = true;

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

		try {
			String sql = "DELETE FROM players";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();

			success = false;

			json.put("error", "failed");
			json.put("message", e.getClass().getName() + " " + e.getMessage());
		}

		try {
			String sql = "UPDATE tsdata SET data_value = null WHERE data_key = \"active_server\"";
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