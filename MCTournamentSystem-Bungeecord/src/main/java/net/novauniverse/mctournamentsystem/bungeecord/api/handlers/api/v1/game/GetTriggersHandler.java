package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONObject;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;

public class GetTriggersHandler extends TournamentEndpoint {

	public GetTriggersHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.GET);
	}
	
	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
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
		return new JSONResponse(result);
	}
}