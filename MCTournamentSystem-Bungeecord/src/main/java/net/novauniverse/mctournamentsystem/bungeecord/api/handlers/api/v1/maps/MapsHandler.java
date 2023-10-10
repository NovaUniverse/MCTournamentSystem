package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.maps;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.apilib.http.response.TextResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class MapsHandler extends TournamentEndpoint {
	public MapsHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET, HTTPMethod.PUT, HTTPMethod.DELETE);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		if (request.getMethod() == HTTPMethod.GET) {
			JSONArray maps = new JSONArray();

			String sql = "SELECT * FROM map_pool";
			PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				int id = rs.getInt("id");
				UUID uuid = UUID.fromString(rs.getString("uuid"));
				String game = rs.getString("game_name");
				String name = rs.getString("map_name");
				boolean enabled = rs.getBoolean("enabled");

				JSONObject map = new JSONObject();
				map.put("id", id);
				map.put("uuid", uuid);
				map.put("game", game);
				map.put("name", name);
				map.put("enabled", enabled);

				maps.put(map);
			}

			rs.close();
			ps.close();

			return new JSONResponse(maps);
		}

		if (!request.getQueryParameters().containsKey("mapId")) {
			return new TextResponse("Bad request: missing mapId", HTTPResponseCode.BAD_REQUEST);
		}

		String mapId = request.getQueryParameters().get("mapId");
		String sql = "UPDATE map_pool SET enabled = ? WHERE uuid = ?";
		PreparedStatement ps = getDBConnection().getConnection().prepareStatement(sql);
		ps.setBoolean(1, request.getMethod() == HTTPMethod.PUT);
		ps.setString(2, mapId);
		ps.executeUpdate();
		ps.close();

		return new JSONResponse(new JSONObject());
	}
}