package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.winner;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.apilib.http.response.TextResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.data.TeamData;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.novauniverse.mctournamentsystem.commons.winner.LockedWinnerManagement;
import net.zeeraa.novacore.commons.jarresourcereader.JARResourceReader;
import net.zeeraa.novacore.commons.utils.JSONObjectBuilder;

public class LockedWinnerHandler extends TournamentEndpoint {

	public LockedWinnerHandler() {
		super(false);

		setAllowedMethods(HTTPMethod.GET, HTTPMethod.DELETE, HTTPMethod.POST);

		setMethodBasedPermission(HTTPMethod.PUT, AuthPermission.LOCK_WINNER);
		setMethodBasedPermission(HTTPMethod.DELETE, AuthPermission.LOCK_WINNER);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		if (request.getMethod() == HTTPMethod.GET) {
			int lockedTeam = LockedWinnerManagement.getLockedWinner();
			return new JSONResponse(new JSONObjectBuilder().put("locked_winner", lockedTeam).build());
		} else if (request.getMethod() == HTTPMethod.DELETE) {
			LockedWinnerManagement.unlockWinner();
			return new JSONResponse();
		} else if (request.getMethod() == HTTPMethod.PUT) {
			if (request.getQueryParameters().containsKey("auto")) {
				List<TeamData> teamDataList = new ArrayList<TeamData>();
				String sql = JARResourceReader.readFileFromJARAsString(getClass(), "/sql/api/v1/status/get_team_data.sql");
				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
				ResultSet rs = ps.executeQuery();

				while (rs.next()) {
					TeamData td = new TeamData(rs.getInt("id"), rs.getInt("team_number"), rs.getInt("total_score"), rs.getInt("kills"));
					teamDataList.add(td);
				}

				rs.close();
				ps.close();

				if (teamDataList.size() == 0) {
					return new JSONResponse(new JSONObjectBuilder().put("message", "Team list is empty").build(), HTTPResponseCode.PRECONDITION_FAILED);
				}

				Collections.sort(teamDataList, Comparator.comparingInt(TeamData::getScore).reversed());

				int teamNumber = teamDataList.get(0).getTeamNumber();

				LockedWinnerManagement.lockWinner(teamNumber);

				return new JSONResponse(new JSONObjectBuilder().put("team", teamNumber).build());
			} else if (request.getQueryParameters().containsKey("team")) {
				try {
					int teamNumber = Integer.parseInt(request.getQueryParameters().get("team"));
					LockedWinnerManagement.lockWinner(teamNumber);
				} catch (NumberFormatException e) {
					return new JSONResponse(new JSONObjectBuilder().put("message", "Bad request: team has to be a number").build(), HTTPResponseCode.BAD_REQUEST);
				}
			} else {
				return new JSONResponse(new JSONObjectBuilder().put("message", "Bad request: Missing team or auto").build(), HTTPResponseCode.BAD_REQUEST);
			}
		}

		return new TextResponse("Bruh", HTTPResponseCode.NOT_IMPLEMENTED);
	}

}
