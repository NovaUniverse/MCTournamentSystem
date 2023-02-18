package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.annotation.Nonnull;
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
	private List<HTTPMethod> allowedMethods;
	private boolean requireAuthentication = false;
	private Map<HTTPMethod, UserPermission> methodBasedPermissions;

	public APIEndpoint(boolean requireAuthentication) {
		this.allowedMethods = new ArrayList<>();
		this.requireAuthentication = requireAuthentication;
		this.methodBasedPermissions = new HashMap<>();
	}

	public Map<HTTPMethod, UserPermission> getMethodBasedPermissions() {
		return methodBasedPermissions;
	}

	protected void setMethodBasedPermission(@Nonnull HTTPMethod method, @Nonnull UserPermission permission) {
		this.methodBasedPermissions.put(method, permission);
	}

	protected void setAllowedMethods(HTTPMethod... httpMethods) {
		allowedMethods.clear();
		for (HTTPMethod httpMethod : httpMethods) {
			allowedMethods.add(httpMethod);
		}
	}

	public boolean hasAllowedMethods() {
		return allowedMethods.size() > 0;
	}

	public boolean isMethodAllowed(HTTPMethod method) {
		if (this.hasAllowedMethods()) {
			return allowedMethods.contains(method);
		}
		return true;
	}

	public List<HTTPMethod> getAllowedMethods() {
		return allowedMethods;
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

	public boolean isRequireAuthentication() {
		return requireAuthentication;
	}

	@Override
	public final void handle(HttpExchange exchange) throws IOException {
		Map<String, String> params = WebServer.queryToMap(exchange.getRequestURI().getQuery());

		JSONObject result = new JSONObject();

		Authentication authentication = null;

		boolean usedCookie = false;
		if (params.containsKey("access_token")) {
			authentication = APITokenStore.getToken(params.get("access_token"));
		} else {
			if (exchange.getRequestHeaders().containsKey("Cookie")) {
				for (String header : exchange.getRequestHeaders().get("Cookie")) {
					String[] cookies = header.split(";");
					for (String cookie : cookies) {
						cookie = cookie.trim();
						if (cookie.startsWith("ts_access_token=")) {
							String token = cookie.split("ts_access_token=")[1];
							usedCookie = true;
							authentication = APITokenStore.getToken(token);
						}
					}
				}
			}
		}

		exchange.getResponseHeaders().add("x-is-access-token-from-cookie", "" + usedCookie);

		HTTPMethod method = null;

		boolean didProcess = false;

		try {
			method = HTTPMethod.valueOf(exchange.getRequestMethod().toUpperCase());
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "unknown_method");
			result.put("message", "The http method " + exchange.getRequestMethod() + " is not known to the server, valid values are " + String.join(",", Arrays.asList(HTTPMethod.values()).stream().map(Object::toString).collect(Collectors.toList())));
			result.put("http_response_code", 405);
		}

		if (method != null) {
			if (!this.isMethodAllowed(method)) {
				result.put("success", false);
				result.put("error", "method_not_allowed");
				result.put("message", "This endpoint does not accept the " + method + " method. Accepted methods: " + String.join(",", allowedMethods.stream().map(Object::toString).collect(Collectors.toList())));
				result.put("http_response_code", 405);
			} else {
				if (params.containsKey("api_key")) {
					// API key has higher priority
					authentication = null;
					String apiKey = params.get("api_key");
					if (APIKeyStore.hasAPIKey(apiKey)) {
						authentication = APIKeyStore.getAPIKey(apiKey);
					}
				}

				if (allowCommentatorAccess()) {
					if (params.containsKey("commentator_key")) {
						// Commentator key have higher priority
						authentication = null;
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
					result.put("http_response_code", 401);
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
								result.put("error", "access_denied");
								result.put("message", "You are messing the required permission " + requiredPermission.name() + " to perform this request");
								result.put("http_response_code", 403);
							}
						}
					}

					UserPermission requiredMethodPermission = this.getMethodBasedPermissions().get(method);
					if (requiredMethodPermission != null) {
						if (authentication == null && !TournamentSystem.getInstance().isWebserverDevelopmentMode()) {
							result.put("success", false);
							result.put("error", "unauthorized");
							result.put("message", "This endopint requires you to be logged in when using that method");
							result.put("http_response_code", 401);
							permissionCheckFail = true;
						} else {
							if (!authentication.getUser().hasPermission(requiredMethodPermission) && !TournamentSystem.getInstance().isWebserverDevelopmentMode()) {
								result.put("success", false);
								result.put("error", "access_denied");
								result.put("message", "This endopint requires you to have the " + requiredMethodPermission.name() + " permission to send " + method.name() + " requests");
								result.put("http_response_code", 403);
								permissionCheckFail = true;
							}
						}
					}

					if (!permissionCheckFail) {
						try {
							didProcess = true;
							result = this.handleRequest(exchange, params, authentication, method);
						} catch (Exception e) {
							result.put("success", false);
							result.put("error", "exception");
							result.put("message", "An internal exception occured while trying to proccess your request. " + e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
							result.put("http_response_code", 500);
						}
					}
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

		this.afterRequestProcessed(didProcess, result, exchange, params, authentication, method);
	}

	public void afterRequestProcessed(final boolean didProcess, final JSONObject response, final HttpExchange exchange, final Map<String, String> params, final Authentication authentication, final HTTPMethod method) {
	}

	public abstract JSONObject handleRequest(final HttpExchange exchange, final Map<String, String> params, final Authentication authentication, final HTTPMethod method) throws Exception;
}