package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.commentator;

import java.util.Map;
import java.util.UUID;

import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.mctournamentsystem.bungeecord.api.APIEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class CommentatorTPHandler extends APIEndpoint {
	public CommentatorTPHandler() {
		super(true);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();

		if (authentication instanceof CommentatorAuth) {
			UUID target = null;

			if (params.containsKey("target")) {
				try {
					target = UUID.fromString(params.get("target"));
				} catch (Exception e) {
				}
			}

			if (target != null) {
				if (((CommentatorAuth) authentication).getMinecraftUserUUID() == null) {
					json.put("success", false);
					json.put("error", "unauthorized");
					json.put("message", "Guest commentators cant use the tp function. Please ask the staff if you want a full access commentator key");
					json.put("http_response_code", 401);
				} else {

					UUID uuid = ((CommentatorAuth) authentication).getMinecraftUserUUID();

					ProxiedPlayer player = ProxyServer.getInstance().getPlayer(uuid);
					if (player != null) {
						ByteArrayDataOutput out = ByteStreams.newDataOutput();

						out.writeUTF("commentator_tp");
						out.writeUTF(uuid.toString());
						out.writeUTF(target.toString());

						player.getServer().getInfo().sendData(TournamentSystemCommons.DATA_CHANNEL, out.toByteArray());

						json.put("success", true);
					} else {
						json.put("success", false);
						json.put("error", "commentator_not_online");
						json.put("message", "Commentators minecraft account is not online");
					}
				}
			} else {
				json.put("success", false);
				json.put("error", "bad request");
				json.put("message", "Missing or invalid parameter: target");
				json.put("http_response_code", 400);
			}
		} else {
			json.put("success", false);
			json.put("error", "unauthorized");
			json.put("message", "Invalid auth type: " + authentication.getClass().getName());
			json.put("http_response_code", 400);
		}

		return json;
	}
}