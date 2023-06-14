package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.commentator;

import java.util.UUID;

import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

public class CommentatorTPHandler extends TournamentEndpoint {
	public CommentatorTPHandler() {
		super(true);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public boolean allowCommentatorAccess() {
		return true;
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject json = new JSONObject();
		int code = 200;

		if (authentication instanceof CommentatorAuth) {
			CommentatorAuth auth = (CommentatorAuth) authentication;
			UUID target = null;

			if (request.getQueryParameters().containsKey("target")) {
				try {
					target = UUID.fromString(request.getQueryParameters().get("target"));
				} catch (Exception e) {
				}
			}

			if (target != null) {
				if (auth.getMinecraftUuid() == null) {
					json.put("success", false);
					json.put("error", "unauthorized");
					json.put("message", "Guest commentators cant use the tp function. Please ask the staff if you want a full access commentator key");
					json.put("http_response_code", 401);
					code = 401;
				} else {

					UUID uuid = auth.getMinecraftUuid();

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
				code = 400;
			}
		} else {
			json.put("success", false);
			json.put("error", "unauthorized");
			json.put("message", "Invalid auth type: " + authentication.getClass().getName());
			json.put("http_response_code", 400);
			code = 400;
		}

		return new JSONResponse(json, code);
	}
}