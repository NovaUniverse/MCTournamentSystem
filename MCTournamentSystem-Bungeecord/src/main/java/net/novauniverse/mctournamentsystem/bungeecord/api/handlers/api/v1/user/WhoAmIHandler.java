package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user;

import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKeyAuth;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;

public class WhoAmIHandler extends TournamentEndpoint {
	public WhoAmIHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();

		result.put("success", true);

		if (authentication == null) {
			result.put("logged_in", false);
		} else {
			if (authentication instanceof APIKeyAuth) {
				APIKeyAuth auth = (APIKeyAuth) authentication;
				result.put("logged_in", true);
				result.put("type", "user");
				result.put("username", auth.getUser().getUsername());
				result.put("permissions", auth.getUser().getPermissionsAsJSON());
			} else if (authentication instanceof CommentatorAuth) {
				CommentatorAuth commentatorAuth = (CommentatorAuth) authentication;
				result.put("logged_in", true);
				result.put("type", "commentator");
				result.put("username", commentatorAuth.getName());
				result.put("uuid", commentatorAuth.getMinecraftUuid().toString());
			}
		}

		return new JSONResponse(result);
	}
}