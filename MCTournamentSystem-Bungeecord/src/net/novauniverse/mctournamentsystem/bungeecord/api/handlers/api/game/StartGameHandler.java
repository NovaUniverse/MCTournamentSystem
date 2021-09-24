package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.game;

import java.io.IOException;
import java.io.OutputStream;
import org.json.JSONObject;

import com.google.common.io.ByteArrayDataOutput;
import com.google.common.io.ByteStreams;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;

@SuppressWarnings("restriction")
public class StartGameHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		if (ProxyServer.getInstance().getOnlineCount() == 0) {
			json.put("success", false);
			json.put("error", "no_players");
			json.put("message", "No players online to use for plugin message channel");
		} else {
			for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
				ByteArrayDataOutput out = ByteStreams.newDataOutput();

				out.writeUTF("start_game");

				player.getServer().getInfo().sendData(TournamentSystemCommons.DATA_CHANNEL, out.toByteArray());
			}

			json.put("success", true);
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}