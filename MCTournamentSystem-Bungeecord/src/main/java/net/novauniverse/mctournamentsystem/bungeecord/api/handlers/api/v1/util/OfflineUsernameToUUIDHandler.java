package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.util;

import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.google.common.base.Charsets;
import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;

public class OfflineUsernameToUUIDHandler extends APIEndpoint {
	public OfflineUsernameToUUIDHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject json = new JSONObject();
		if (params.containsKey("username")) {
			String name = params.get("username");
			json.put("name", name);
			json.put("uuid", UUID.nameUUIDFromBytes(("OfflinePlayer:" + name).getBytes(Charsets.UTF_8)).toString());
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: username");
			json.put("http_response_code", 400);
		}
		return json;
	}

}
