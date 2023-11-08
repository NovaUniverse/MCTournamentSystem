package net.novauniverse.mctournamentsystem.bungeecord.api.handlers.api.v1.user;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.json.JSONObject;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.enums.HTTPMethod;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.apilib.http.response.AbstractHTTPResponse;
import net.novauniverse.apilib.http.response.JSONResponse;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.TournamentEndpoint;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.JWTTokenType;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.user.User;
import net.novauniverse.mctournamentsystem.bungeecord.security.JWTProperties;
import net.novauniverse.mctournamentsystem.bungeecord.security.PasswordHashing;

public class LoginHandler extends TournamentEndpoint {
	public LoginHandler() {
		super(false);
		setAllowedMethods(HTTPMethod.POST);
	}

	@Override
	public AbstractHTTPResponse handleRequest(Request request, Authentication authentication) throws Exception {
		JSONObject result = new JSONObject();
		JSONObject loginBody = null;
		int code = 200;

		try {
			loginBody = new JSONObject(request.getBody());
		} catch (Exception e) {
			result.put("success", false);
			result.put("error", "bad_request");
			result.put("message", "Missing or invalid json data");
			result.put("exception", e.getClass().getName() + " " + ExceptionUtils.getMessage(e));
			result.put("http_response_code", 400);
			code = 400;
		}

		if (loginBody != null) {
			if (loginBody.has("username")) {
				if (loginBody.has("password")) {
					String username = loginBody.getString("username");
					String password = loginBody.getString("password");

					User user = TournamentSystem.getInstance().getAuthDB().getUsers().stream().filter(u -> u.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);

					if (user != null) {
						if (PasswordHashing.verifyPassword(password, user.getPasswordHash())) {
							try {
								KeyPair pair = TournamentSystem.getInstance().getTokenKeyPair();
								Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
								JWTCreator.Builder builder = JWT.create();
								builder.withIssuer(JWTProperties.ISSUER);
								builder.withClaim("type", JWTTokenType.USER.name());
								builder.withClaim("pwid", user.getPasswordChangeId().toString());
								builder.withClaim("username", user.getUsername());
								String token = builder.sign(algorithm);

								JSONObject userData = new JSONObject();
								userData.put("username", user.getUsername());
								userData.put("permissions", user.getPermissionsAsJSON());
								userData.put("can_manage_accounts", user.isAllowManagingAccounts());

								result.put("success", true);
								result.put("token", token);
								result.put("user", userData);
							} catch (JWTCreationException e) {
								e.printStackTrace();
								throw new RuntimeException("An error occured while signing token");
							}
						} else {
							result.put("success", false);
							result.put("error", "login_fail");
							result.put("message", "Invalid username or password");
							result.put("http_response_code", 401);
							code = 401;
						}
					} else {
						result.put("success", false);
						result.put("error", "login_fail");
						result.put("message", "Invalid username or password");
						result.put("http_response_code", 401);
						code = 401;
					}

				} else {
					result.put("success", false);
					result.put("error", "bad_request");
					result.put("message", "Missing parameter: password");
					result.put("http_response_code", 400);
					code = 400;
				}
			} else {
				result.put("success", false);
				result.put("error", "bad_request");
				result.put("message", "Missing parameter: username");
				result.put("http_response_code", 400);
				code = 400;
			}
		}

		return new JSONResponse(result, code);
	}
}