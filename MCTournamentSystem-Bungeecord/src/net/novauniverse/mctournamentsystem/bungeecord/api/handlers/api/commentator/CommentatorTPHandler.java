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
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIKeyStore;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class CommentatorTPHandler extends APIEndpoint {
	public CommentatorTPHandler() {
		super(true);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception {
		JSONObject json = new JSONObject();

		UUID target = null;

		if (params.containsKey("target")) {
			try {
				target = UUID.fromString(params.get("target"));
			} catch (Exception e) {
			}
		}

		if (target != null) {
			if (params.containsKey("commentator_key")) {
				if (APIKeyStore.getCommentatorKeys().containsKey(params.get("commentator_key"))) {
					UUID uuid = APIKeyStore.getCommentatorKeys().get(params.get("commentator_key"));

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
						json.put("error", "failed");
						json.put("message", "Commentators minecraft account is not online");
					}
				} else {
					json.put("success", false);
					json.put("error", "unauthorized");
					json.put("message", "Invalid key");
				}
			} else {
				json.put("success", false);
				json.put("error", "unauthorized");
				json.put("message", "This action requires a commentator key");
			}
		} else {
			json.put("success", false);
			json.put("error", "bad request");
			json.put("message", "Missing or invalid parameter: target");
		}

		return json;
	}
}