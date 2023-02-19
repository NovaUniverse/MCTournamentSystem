package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator;

import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class GetCommentatorGuestKeyHandler extends APIEndpoint {
	public GetCommentatorGuestKeyHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();
		json.put("commentator_guest_key", TournamentSystem.getInstance().getCommentatorGuestKey().getKey());
		return json;
	}
}