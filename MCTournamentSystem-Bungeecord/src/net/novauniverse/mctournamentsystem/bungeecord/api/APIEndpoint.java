package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIAccessToken;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIKeyStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APITokenStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

@SuppressWarnings("restriction")
public abstract class APIEndpoint implements HttpHandler {
	private boolean requireAuthentication = false;

	public APIEndpoint(boolean requireAuthentication) {
		this.requireAuthentication = requireAuthentication;
	}

	public boolean allowCommentatorAccess() {
		return false;
	}

	@Nullable
	public UserPermission getRequiredPermission() {
		return null;
	}

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		APIAccessToken token = null;

		if (params.containsKey("access_token")) {
			token = APITokenStore.getToken(params.get("access_token"));
		}

		JSONObject result = new JSONObject();

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
				if (APIKeyStore.getCommentatorKeys().containsKey(commentatorKey) || TournamentSystem.getInstance().getCommentatorGuestKey().equalsIgnoreCase(commentatorKey)) {
					apiKeyOk = true;
				}
			}
		}

		if (requireAuthentication && token == null && !TournamentSystem.getInstance().isWebserverDevelopmentMode() && !apiKeyOk) {
			result.put("success", false);
			result.put("error", "unauthorized");
			result.put("message", "Access token is missing or invalid. Please login to use this system");
		} else {
			if (requireAuthentication && token == null && TournamentSystem.getInstance().isWebserverDevelopmentMode()) {
				token = APITokenStore.DUMMY_TOKEN;
			}

			boolean permissionCheckFail = false;
			if (token != null) {
				UserPermission requiredPermission = getRequiredPermission();
				if (requiredPermission != null) {
					if (!token.getUser().hasPermission(requiredPermission)) {
						permissionCheckFail = true;
						result.put("success", false);
						result.put("error", "unauthorized");
						result.put("message", "You are messing the required permission " + requiredPermission.name() + " to perform this request");
					}
				}
			}

			if (!permissionCheckFail) {
				try {
					result = this.handleRequest(exchange, params, token);
				} catch (Exception e) {
					result.put("success", false);
					result.put("error", "exception");
					result.put("message", "An internal exception occured while trying to proccess your request. " + e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				}
			}
		}

		String responseString = result.toString(4);

		int code = 200;

		if (result.has("http_response_code")) {
			code = result.getInt("http_response_code");
		}

		exchange.sendResponseHeaders(code, responseString.getBytes().length);
		OutputStream os = exchange.getResponseBody();
		os.write(responseString.getBytes());
		os.close();
	}

	public abstract JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, APIAccessToken accessToken) throws Exception;

	public boolean isRequireAuthentication() {
		return requireAuthentication;
	}
}