package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.skinrestorer;

import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.skinsrestorer.api.SkinsRestorerAPI;
import net.skinsrestorer.api.property.IProperty;

public class GetSkinrestorerSkinHandler extends APIEndpoint {
	public GetSkinrestorerSkinHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();
		if (params.containsKey("username")) {
			String username = params.get("username");
			if (ProxyServer.getInstance().getPluginManager().getPlugin("SkinsRestorer") != null) {
				String skin = SkinsRestorerAPI.getApi().getSkinName(username);
				IProperty data = null;
				if(skin != null) {
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
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "Missing username");
			json.put("http_response_code", 400);
		}
		return json;
	}
}