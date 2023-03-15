package net.novauniverse.mctournamentsystem.commons.socketapi;

import org.json.JSONObject;

import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.log.Log;

public class SocketAPIUtil {
	public static final void setupSocketAPI() {
		JSONObject config = TournamentSystemCommons.getTournamentSystemConfigData();
		try {
			SocketAPIConfig socketAPIConfig = SocketAPIConfig.parse(config.optJSONObject("socket_api"));
			String keyFromENV = System.getenv("WS_API_SERVER_KEY");
			if(keyFromENV != null) {
				socketAPIConfig.setKey(keyFromENV);
			}
			if (socketAPIConfig != null) {
				if (socketAPIConfig.isEnabled()) {
					Log.info("SocketAPI", "Starting SocketAPI");
					TournamentSystemCommons.setSocketAPI(new SocketAPI(socketAPIConfig));
				}
			}
		} catch (Exception e) {
			Log.error("SocketAPI", "Failed to init SocketAPI");
		}
	}

	public static final void shutdown() {
		if (TournamentSystemCommons.hasSocketAPI()) {
			TournamentSystemCommons.getSocketAPI().disconnect();
		}
	}
}