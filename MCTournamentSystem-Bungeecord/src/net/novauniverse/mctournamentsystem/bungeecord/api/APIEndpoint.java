package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIKeyStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APITokenStore;

@SuppressWarnings("restriction")
public abstract class APIEndpoint implements HttpHandler {
	private boolean requireAuthentication = false;

	public APIEndpoint(boolean requireAuthentication) {
		this.requireAuthentication = requireAuthentication;
	}

	public boolean allowCommentatorAccess() {
		return false;
	}

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		APIAccessToken token = null;

		if (params.containsKey("access_token")) {
			token = APITokenStore.getToken(params.get("access_token"));
		}

		JSONObject result;

		boolean apiKeyOk = false;

		if (params.containsKey("api_key")) {
			String apiKey = params.get("api_key");
			if (APIKeyStore.getApiKeys().contains(apiKey)) {
				apiKeyOk = true;
			}
		}

		if (allowCommentatorAccess()) {
			if (params.containsKey("commentator_key")) {
				String commentatorKey = params.get("commentator_key");
				if (APIKeyStore.getCommentatorKeys().containsKey(commentatorKey)) {
					apiKeyOk = true;
				}
			}
		}

		if (requireAuthentication && token == null && !TournamentSystem.getInstance().isWebserverDevelopmentMode() && !apiKeyOk) {
			result = new JSONObject();
			result.put("success", false);
			result.put("error", "unauthorized");
			result.put("message", "Access token is missing or invalid. Please login to use this system");
		} else {
			if (requireAuthentication && token == null && TournamentSystem.getInstance().isWebserverDevelopmentMode()) {
				token = APITokenStore.DUMMY_TOKEN;
			}

			try {
				result = this.handleRequest(exchange, params, token);
			} catch (Exception e) {
				result = new JSONObject();
				result.put("success", false);
				result.put("error", "exception");
				result.put("message", "An internal exception occured while trying to proccess your request. " + e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			}
		}

		String responseString = result.toString(4);

		exchange.sendResponseHeaders(200, responseString.getBytes().length);

		OutputStream os = exchange.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
	}

	public abstract JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception;

	public boolean isRequireAuthentication() {
		return requireAuthentication;
	}
}