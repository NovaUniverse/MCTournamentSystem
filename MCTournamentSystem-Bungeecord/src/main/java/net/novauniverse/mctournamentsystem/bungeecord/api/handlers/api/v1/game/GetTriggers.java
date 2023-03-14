package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.zeeraa.novacore.commons.utils.HashUtils;

public class GetTriggers extends APIEndpoint {

	public GetTriggers() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication, HTTPMethod method) throws Exception {
		JSONObject result = new JSONObject();
		JSONArray triggers = new JSONArray();
		
		List<String> indexedKeys = new ArrayList<>();
		
		TournamentSystem.getInstance().getPlayerTelementryManager().getData().values().forEach(data -> {
			if(data.getMetadata().has("triggers")) {
				JSONArray triggerDataList = data.getMetadata().getJSONArray("triggers");
				for(int i = 0; i < triggerDataList.length(); i++) {
					JSONObject trigger = triggerDataList.getJSONObject(i);
					
					String sessionId = trigger.getString("session_id");
					String name = trigger.getString("name");
					
					if(!indexedKeys.contains(name + sessionId)) {
						indexedKeys.add(name + sessionId);
						
						triggers.put(trigger);
					}
				}
			}
		});
		
		result.put("triggers", triggers);
		return result;
	}
}