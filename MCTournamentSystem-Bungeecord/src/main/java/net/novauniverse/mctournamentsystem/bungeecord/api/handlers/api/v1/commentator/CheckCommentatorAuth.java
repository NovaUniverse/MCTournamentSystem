package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;

public class CheckCommentatorAuth extends TournamentEndpoint {
	public CheckCommentatorAuth() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}
	
	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		boolean ok = false;
		if (authentication != null) {
			if (authentication instanceof CommentatorAuth) {
				ok = true;
			}
		}

		JSONObject json = new JSONObject();
		json.put("ok", ok);
		return new JSONResponse(json);
	}
}