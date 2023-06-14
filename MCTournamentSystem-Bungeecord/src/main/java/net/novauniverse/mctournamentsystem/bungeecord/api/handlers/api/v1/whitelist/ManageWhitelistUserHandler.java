package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.whitelist;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.UUID;

import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.api.novauniverse.NovaUniverseAPI;
import net.zeeraa.novacore.commons.api.novauniverse.data.MojangPlayerProfile;
import net.zeeraa.novacore.commons.log.Log;

public class ManageWhitelistUserHandler extends TournamentEndpoint {
	public ManageWhitelistUserHandler() {
		super(false);

		setAllowedMethods(HTTPMethod.PUT, HTTPMethod.DELETE, HTTPMethod.GET);

		setMethodBasedPermission(HTTPMethod.PUT, AuthPermission.MANAGE_WHITELIST);
		setMethodBasedPermission(HTTPMethod.DELETE, AuthPermission.MANAGE_WHITELIST);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getMethod() == HTTPMethod.GET) {
			JSONArray data = new JSONArray();

			String sql = "SELECT * FROM whitelist";

			PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);
			ResultSet rs = ps.executeQuery();

			while (rs.next()) {
				JSONObject entry = new JSONObject();

				entry.put("uuid", rs.getString("uuid"));
				entry.put("username", rs.getString("username"));
				entry.put("offline_mode", rs.getBoolean("offline_mode"));

				data.put(entry);
			}

			rs.close();
			ps.close();

			json.put("success", true);
			json.put("users", data);
		} else {
			if (request.getQueryParameters().containsKey("uuid")) {
				UUID uuid = UUID.fromString(request.getQueryParameters().get("uuid"));

				String sql;

				boolean isInsert = false;
				String name = null;
				boolean offlineMode = false;

				if (request.getMethod() == HTTPMethod.PUT) {
					if (request.getQueryParameters().containsKey("username")) {
						name = request.getQueryParameters().get("username");
					}

					if (request.getQueryParameters().containsKey("offline_mode")) {
						offlineMode = request.getQueryParameters().get("offline_mode").equalsIgnoreCase("true");
					}

					isInsert = true;

					sql = "REPLACE INTO whitelist (id, uuid, username, offline_mode) VALUES(null, ?, ?, ?)";
				} else {
					sql = "DELETE FROM whitelist WHERE uuid = ?";
				}

				PreparedStatement ps = TournamentSystemCommons.getDBConnection().getConnection().prepareStatement(sql);

				ps.setString(1, uuid.toString());

				if (isInsert) {
					if (name == null && offlineMode == false) {
						try {
							Log.trace("AddWhitelistHandler", "Trying to get name of " + uuid.toString());
							MojangPlayerProfile profile = NovaUniverseAPI.getProfile(uuid);
							name = profile.getName();
							Log.trace("AddWhitelistHandler", "Name of " + uuid.toString() + " is " + name);
						} catch (Exception e) {
							Log.warn("AddWhitelistHandler", "Failed to get name of " + uuid.toString() + ". " + e.getClass().getName() + " " + e.getMessage());
						}
					}

					ps.setString(2, name);
					ps.setBoolean(3, offlineMode);
				}

				ps.executeUpdate();
				ps.close();

				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "bad_request");
				json.put("message", "missing parameter: uuid");
				json.put("http_response_code", 400);
				code = 400;
			}
		}

		return new JSONResponse(json, code);
	}
}