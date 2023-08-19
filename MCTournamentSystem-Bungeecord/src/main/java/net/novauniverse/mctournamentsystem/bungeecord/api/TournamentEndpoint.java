package net.novauniverse.mctournamentsystem.bungeecord.api;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nullable;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationResponse;
import net.novauniverse.apilib.http.endpoint.HTTPEndpoint;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.enums.HTTPResponseCode;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.AuthPermission;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator.CommentatorAuth;
import net.novauniverse.mctournamentsystem.commons.TournamentSystemCommons;
import net.zeeraa.novacore.commons.database.DBConnection;

public abstract class TournamentEndpoint extends HTTPEndpoint {
	private boolean requireAuthentication;
	private Map<HTTPMethod, AuthPermission> methodSpecificPermissions;

	public TournamentEndpoint(boolean requireAuthentication) {
		this.requireAuthentication = requireAuthentication;
		this.methodSpecificPermissions = new HashMap<>();

		setRequireAuthentication(requireAuthentication);
	}

	protected void setMethodBasedPermission(HTTPMethod method, AuthPermission permission) {
		methodSpecificPermissions.put(method, permission);
	}

	public boolean allowCommentatorAccess() {
		return false;
	}

	@Nullable
	public AuthPermission getRequiredPermission() {
		return null;
	}

	protected static DBConnection getDBConnection() {
		return TournamentSystemCommons.getDBConnection();
	}
	
	@Override
	public final AuthenticationResponse handleAuthentication(Authentication authentication, Request request) {
		if (requireAuthentication && authentication == null) {
			return AuthenticationResponse.customFailResponse("You need to log in to access this endpoint", HTTPResponseCode.UNAUTHORIZED);
		}

		if (authentication != null) {
			if (authentication instanceof CommentatorAuth) {
				if (!allowCommentatorAccess()) {
					return AuthenticationResponse.customFailResponse("This endpoint is not allowed for commentators", HTTPResponseCode.FORBIDDEN);
				}
			}
		}

		if (methodSpecificPermissions.containsKey(request.getMethod())) {
			AuthPermission permission = methodSpecificPermissions.get(request.getMethod());
			if (authentication != null) {
				if (authentication.hasPermission(AuthPermission.ADMIN.name())) {
					return AuthenticationResponse.OK;
				}

				if (authentication.hasPermission(permission.name())) {
					return AuthenticationResponse.OK;
				}
			}
			return AuthenticationResponse.customFailResponse("Missing permission " + permission.name() + " for this method", HTTPResponseCode.FORBIDDEN);
		}

		if (getRequiredPermission() != null) {
			if (authentication != null) {
				if (authentication.hasPermission(AuthPermission.ADMIN.name())) {
					return AuthenticationResponse.OK;
				}

				if (authentication.hasPermission(getRequiredPermission().name())) {
					return AuthenticationResponse.OK;
				}
			}
			return AuthenticationResponse.customFailResponse("Missing permission " + getRequiredPermission().name(), HTTPResponseCode.FORBIDDEN);
		}

		return AuthenticationResponse.OK;
	}
}
