package net.novauniverse.mctournamentsystem.bungeecord.api.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

import org.apache.commons.io.IOUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;

public class ManagedServerStateReportingEndpoint implements HttpHandler {
	@Override
	public void handle(HttpExchange exchange) throws IOException {
		ManagedServer authenticatedServer = null;
		JSONObject response = new JSONObject();
		int responseCode = 200;

		if (exchange.getRequestMethod().toUpperCase() != "POST") {
			String auth = exchange.getRequestHeaders().getFirst("authorization");
			if (auth != null) {
				if (auth.length() > 0) {
					String[] authParts = auth.split(" ");
					String thePartWeCareAbout = authParts[authParts.length - 1];

					if (thePartWeCareAbout.length() > 0) {
						authenticatedServer = TournamentSystem.getInstance().getManagedServers().stream().filter(s -> s.getStateReportingKey().equals(thePartWeCareAbout)).findFirst().orElse(null);
					}
				}
			}

			if (authenticatedServer != null) {
				JSONObject data = null;

				try {
					String body = IOUtils.toString(exchange.getRequestBody(), StandardCharsets.UTF_8);
					data = new JSONObject(body);
				} catch (Exception e) {
					response.put("error", "Parse error");
					responseCode = 400;
				}

				if (data != null) {
					authenticatedServer.setLastStateReport(data);
					response.put("success", true);
				}
			} else {
				response.put("error", "Missing or invalid reporting token");
				responseCode = 403;
			}
		} else {
			response.put("error", "Method not allowed");
			responseCode = 405;
		}

		String responseStr = response.toString();
		byte[] responseByteArray = responseStr.getBytes(StandardCharsets.UTF_8);
		exchange.sendResponseHeaders(responseCode, responseByteArray.length);

		OutputStream os = exchange.getResponseBody();
		os.write(responseByteArray);
		os.close();
	}
}