package net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.JWTAuthProvider;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.JWTTokenType;

public class CommentatorAuthProvider extends JWTAuthProvider implements AuthenticationProvider {
	public CommentatorAuthProvider() {
		super();
	}

	@Override
	public Authentication authenticate(Request request) {
		String auth = request.getFirstRequestHeader("authorization");
		if (auth != null) {
			if (auth.length() > 0) {
				String[] authParts = auth.split(" ");
				String token = authParts[authParts.length - 1];

				try {
					DecodedJWT jwt = jwtVerifier.verify(token);
					String type = jwt.getClaim("type").asString();

					if (type.equalsIgnoreCase(JWTTokenType.COMMENTATOR.name())) {
						String username = jwt.getClaim("username").asString();

						CommentatorUser user = TournamentSystem.getInstance().getAuthDB().getCommentators().stream().filter(c -> c.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
						if (user != null) {
							String pci = jwt.getClaim("pwid").asString();
							if (user.getPasswordChangeId().toString().equalsIgnoreCase(pci)) {
								return new CommentatorAuth(user);
							}
						}
					}
				} catch (JWTVerificationException e) {
				}
			}
		}
		return null;
	}
}