package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import javax.annotation.Nullable;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APIKeyStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.APITokenStore;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.Authentication;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.UserPermission;

// If you get warnings here in eclipse follow this guide https://stackoverflow.com/a/25945740
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

	public boolean shouldPrettyPrintOutput() {
		return true;
	}

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		Authentication authentication = null;

		if (params.containsKey("access_token")) {
			authentication = APITokenStore.getToken(params.get("access_token"));
		}

		JSONObject result = new JSONObject();

		if (params.containsKey("api_key")) {
			String apiKey = params.get("api_key");
			if (APIKeyStore.hasAPIKey(apiKey)) {
				authentication = APIKeyStore.getAPIKey(apiKey);
			}
		}

		if (allowCommentatorAccess()) {
			if (params.containsKey("commentator_key")) {
				String commentatorKey = params.get("commentator_key");
				if (APIKeyStore.hasCommentatorKey(commentatorKey)) {
					authentication = APIKeyStore.getCommentatorKey(commentatorKey);
				} else if (TournamentSystem.getInstance().getCommentatorGuestKey().getKey().equalsIgnoreCase(commentatorKey)) {
					authentication = TournamentSystem.getInstance().getCommentatorGuestKey();
				}
			}
		}

		if (!allowCommentatorAccess() && authentication instanceof CommentatorAuth) {
			authentication = null;
		}

		if (requireAuthentication && authentication == null && !TournamentSystem.getInstance().isWebserverDevelopmentMode()) {
			result.put("success", false);
			result.put("error", "unauthorized");
			result.put("message", "Access token is missing or invalid. Please login to use this system");
		} else {
			if (requireAuthentication && authentication == null && TournamentSystem.getInstance().isWebserverDevelopmentMode()) {
				authentication = APITokenStore.DUMMY_TOKEN;
			}

			boolean permissionCheckFail = false;
			if (authentication != null) {
				UserPermission requiredPermission = getRequiredPermission();
				if (requiredPermission != null) {
					if (!authentication.getUser().hasPermission(requiredPermission)) {
						permissionCheckFail = true;
						result.put("success", false);
						result.put("error", "unauthorized");
						result.put("message", "You are messing the required permission " + requiredPermission.name() + " to perform this request");
					}
				}
			}

			if (!permissionCheckFail) {
				try {
					result = this.handleRequest(exchange, params, authentication);
				} catch (Exception e) {
					result.put("success", false);
					result.put("error", "exception");
					result.put("message", "An internal exception occured while trying to proccess your request. " + e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
				}
			}
		}

		String responseString;
		if (shouldPrettyPrintOutput()) {
			responseString = result.toString(4);
		} else {
			responseString = result.toString();
		}

		int code = 200;

		if (result.has("http_response_code")) {
			code = result.getInt("http_response_code");
		}

		exchange.getResponseHeaders().add("Content-type", "application/json; charset=utf-8");

		exchange.getResponseHeaders().add("x-tournamentsystem-handler", this.getClass().getName());
		if (authentication != null) {
			exchange.getResponseHeaders().add("x-tournamentsystem-authtype", authentication.getClass().getName());
			exchange.getResponseHeaders().add("x-tournamentsystem-auth-user-descriptive-name", authentication.getDescriptiveUserName());
			exchange.getResponseHeaders().add("x-tournamentsystem-auth-user-name", authentication.getUser().getUsername());
		} else {
			exchange.getResponseHeaders().add("x-tournamentsystem-authtype", "none");
			exchange.getResponseHeaders().add("x-tournamentsystem-auth-user-descriptive-name", "null");
			exchange.getResponseHeaders().add("x-tournamentsystem-auth-user-name", "null");
		}

		byte[] responseByteArray = responseString.getBytes(StandardCharsets.UTF_8);

		exchange.sendResponseHeaders(code, responseByteArray.length);

		OutputStream os = exchange.getResponseBody();
		os.write(responseByteArray);
		os.close();
	}

	public abstract JSONObject handleRequest(HttpExchange exchange, Map<String, String> params, Authentication authentication) throws Exception;

	public boolean isRequireAuthentication() {
		return requireAuthentication;
	}
}