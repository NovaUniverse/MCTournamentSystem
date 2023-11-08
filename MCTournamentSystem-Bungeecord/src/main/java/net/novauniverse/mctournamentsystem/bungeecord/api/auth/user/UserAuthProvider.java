package net.novauniverse.mctournamentsystem.bungeecord.api.auth.user;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.interfaces.DecodedJWT;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.JWTAuthProvider;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.JWTTokenType;

public class UserAuthProvider extends JWTAuthProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(Request request) {
		String auth = request.getFirstRequestHeader("authorization");
		if (auth != null) {
			if (auth.length() > 0) {
				String[] authParts = auth.split(" ");
				String token = authParts[authParts.length - 1];

				APIKey key = TournamentSystem.getInstance().getAuthDB().getApiKeys().stream().filter(t -> t.getKey().equals(token)).findFirst().orElse(null);
				if (key != null) {
					return new UserAuth(key.getUser(), UserAuthType.API_KEY);
				}

				try {
					DecodedJWT jwt = jwtVerifier.verify(token);
					String type = jwt.getClaim("type").asString();

					if (type.equalsIgnoreCase(JWTTokenType.USER.name())) {
						String username = jwt.getClaim("username").asString();

						User user = TournamentSystem.getInstance().getAuthDB().getUsers().stream().filter(c -> c.getUsername().equalsIgnoreCase(username)).findFirst().orElse(null);
						if (user != null) {
							String pci = jwt.getClaim("pwid").asString();
							if (user.getPasswordChangeId().toString().equalsIgnoreCase(pci)) {
								return new UserAuth(user, UserAuthType.TOKEN);
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
