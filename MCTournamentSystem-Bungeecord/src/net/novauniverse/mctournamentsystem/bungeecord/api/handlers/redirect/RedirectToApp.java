package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.redirect;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class RedirectToApp implements HttpHandler {
	public static final String CONTENT = "PGh0bWw+PGhlYWQ+PHNjcmlwdD53aW5kb3cubG9jYXRpb249Ii9hcHAvIjwvc2NyaXB0PjwvaGVhZD48L2h0bWw+";

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		String response = new String(Base64.getDecoder().decode(CONTENT));

		exchange.sendResponseHeaders(200, response.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(response.getBytes());
		os.close();
	}
}