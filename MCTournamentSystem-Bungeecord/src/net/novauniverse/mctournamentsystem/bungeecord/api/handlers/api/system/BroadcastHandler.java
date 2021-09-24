package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.system;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.novauniverse.mctournamentsystem.bungeecord.api.WebServer;

@SuppressWarnings("restriction")
public class BroadcastHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject json = new JSONObject();

		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		if (params.containsKey("message")) {
			String message = URLDecoder.decode(params.get("message"), StandardCharsets.UTF_8.name());

			ProxyServer.getInstance().broadcast(new TextComponent(ChatColor.translateAlternateColorCodes('�', message)));

			json.put("success", true);
		} else {
			json.put("success", false);
			json.put("error", "bad_request");
			json.put("message", "missing parameter: message");
		}

		String response = json.toString(4);

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}