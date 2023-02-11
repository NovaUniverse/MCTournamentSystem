package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.commentator;

import java.util.Map;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class GetCommentatorGuestKeyHandler extends APIEndpoint {
	public GetCommentatorGuestKeyHandler() {
		super(true);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		json.put("commentator_guest_key", TournamentSystem.getInstance().getCommentatorGuestKey().getKey());
		return json;
	}
}