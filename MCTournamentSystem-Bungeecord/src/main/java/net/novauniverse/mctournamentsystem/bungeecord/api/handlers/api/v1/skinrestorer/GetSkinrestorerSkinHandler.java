package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.skinrestorer;

import org.json.JSONObject;

import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;

public class GetSkinrestorerSkinHandler extends TournamentEndpoint {
	public GetSkinrestorerSkinHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (request.getQueryParameters().containsKey("username")) {
			String username = request.getQueryParameters().get("username");
			if (ProxyServer.getInstance().getPluginManager().getPlugin("SkinsRestorer") != null) {
				String skin = SkinsRestorerAPI.getApi().getSkinName(username);
				IProperty data = null;
				if (skin != null) {
					data = SkinsRestorerAPI.getApi().getSkinData(skin);
				}
				if (data == null) {
					json.put("success", true);
					json.put("has_skin", false);
				} else {
					json.put("success", true);
					json.put("has_skin", true);
					json.put("skin_name", skin);
					json.put("skin_data", data.getValue());
				}
			} else {
				json.put("success", false);
				json.put("error", "skinrestorer_not_found");
				json.put("message", "This server does not have skinrestorer installed");
				json.put("http_response_code", 409);
				code = 409;
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "Missing username");
			json.put("http_response_code", 400);
			code = 400;
		}
		return new JSONResponse(json, code);
	}
}