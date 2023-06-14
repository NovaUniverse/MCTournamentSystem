package net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.apilib.http.request.Request;

public class APIKeyAuthProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(Request request) {
		String auth = request.getFirstRequestHeader("authorization");
		if (auth != null) {
			if (auth.length() > 0) {
				String[] authParts = auth.split(" ");
				String thePartWeCareAbout = authParts[authParts.length - 1];

				APIKeyAuth keyAuth = APIKeyStore.getAPIKey(thePartWeCareAbout);
				if (keyAuth != null) {
					return keyAuth;
				}

				keyAuth = APITokenStore.getToken(thePartWeCareAbout);
				if (keyAuth != null) {
					return keyAuth;
				}
			}
		}

		if (request.getFirstRequestHeader("Cookie") != null) {
			for (String header : request.getRequestHeaders().get("Cookie")) {
				String[] cookies = header.split(";");
				for (String cookie : cookies) {
					cookie = cookie.trim();
					if (cookie.startsWith("ts_access_token=")) {
						String token = cookie.split("ts_access_token=")[1];
						APIKeyAuth keyAuth = APITokenStore.getToken(token);
						if (keyAuth != null) {
							return keyAuth;
						}
					}
				}
			}
		}

		return null;
	}
}