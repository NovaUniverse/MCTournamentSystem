package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;

public class GetServiceProvidersHandler implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		JSONObject response = new JSONObject();
		response.put("mojang_api_proxy", TournamentSystem.getInstance().getMojangAPIProxyURL());
		String responseStr = response.toString();
		byte[] responseByteArray = responseStr.getBytes(StandardCharsets.UTF_8);
		exchange.sendResponseHeaders(200, responseByteArray.length);
		
		OutputStream os = exchange.getResponseBody();
		os.write(responseByteArray);
		os.close();
	}
}