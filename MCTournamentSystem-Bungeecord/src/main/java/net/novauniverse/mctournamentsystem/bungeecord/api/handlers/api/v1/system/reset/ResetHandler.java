package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.system.reset;

import java.sql.PreparedStatement;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.misc.MissingTeamFixer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.winner.LockedWinnerManagement;

public class ResetHandler extends TournamentEndpoint {
	public ResetHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.DELETE);
	}

	@Override
	public AuthPermission getRequiredPermission() {
		return AuthPermission.CLEAR_DATA;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		try {
			String sql = "DELETE FROM teams";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		try {
			String sql = "DELETE FROM players";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		try {
			String sql = "UPDATE tsdata SET data_value = null WHERE data_key = \"active_server\"";
			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

			ps.execute();
			ps.close();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		MissingTeamFixer.fixTeams();

		try {
			LockedWinnerManagement.unlockWinner();
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}

		json.put("success", true);
		return new JSONResponse(json);
	}
}