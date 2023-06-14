package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class GetChatLogHandler extends TournamentEndpoint {
	public GetChatLogHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray messages = new JSONArray();

		String sql = "SELECT id, uuid, username, content, sent_at FROM chat_log WHERE session_id = ?";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

		ps.setString(1, TournamentSystemCommons.getSessionId().toString());

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			JSONObject entry = new JSONObject();
			entry.put("message_id", rs.getInt("id"));
			entry.put("uuid", rs.getString("uuid"));
			entry.put("username", rs.getString("username"));
			entry.put("content", rs.getString("content"));
			entry.put("sent_at", rs.getString("sent_at"));
			messages.put(entry);
		}

		rs.close();
		ps.close();

		result.put("success", true);
		result.put("messages", messages);
		
		return new JSONResponse(result);
	}
}