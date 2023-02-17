package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.game;

import java.util.Map;

import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.HTTPMethod;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class StartGameHandler extends APIEndpoint {
	public StartGameHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}
	
	@Override
	public UserPermission getRequiredPermission() {
		return UserPermission.START_GAME;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (ProxyServer.getInstance().getOnlineCount() == 0) {
			json.put("success", false);
			json.put("error", "no_players");
			json.put("message", "No players online to use for plugin message channel. Try again when there are players online");
			json.put("http_response_code", 409);
		} else {
			ProxyServer.getInstance().getPlayers().forEach(player -> {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();

				out.writeUTF("start_game");

				player.getServer().getInfo().sendData(TournamentSystemCommons.DATA_CHANNEL, out.toByteArray());
			});

			json.put("success", true);
		}

		return json;
	}
}