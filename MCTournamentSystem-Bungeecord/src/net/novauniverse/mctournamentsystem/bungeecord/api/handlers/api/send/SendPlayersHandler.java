package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.send;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.novauniverse.mctournamentsystem.bungeecord.api.WebServer;

@SuppressWarnings("restriction")
public class SendPlayersHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		if (params.containsKey("server")) {

			ServerInfo server = ProxyServer.getInstance().getServerInfo(params.get("server"));

			if (server != null) {
				for (ProxiedPlayer player : ProxyServer.getInstance().getPlayers()) {
					player.connect(server);
				}

				json.put("success", true);
			} else {
				json.put("success", false);
				json.put("error", "server_not_found");
				json.put("message", "could not find server with that name");
			}
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: server");
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}