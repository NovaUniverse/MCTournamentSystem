package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.chat;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class GetChatLogHandler extends APIEndpoint {
	public GetChatLogHandler() {
		super(true);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray messages = new JSONArray();

		String sql = "SELECT uuid, username, content, sent_at FROM chat_log WHERE session_id = ?";
		PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

		ps.setString(1, TournamentSystemCommons.getSessionId().toString());

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			JSONObject entry = new JSONObject();
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

		return result;
	}
}