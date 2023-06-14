package net.novauniverse.mctournamentsystem.bungeecord.api.auth.internal;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.servers.ManagedServer;

public class InternalAuthProvider implements AuthenticationProvider {
	public static final InternalAuth AuthObject = new InternalAuth();

	@Override
	public Authentication authenticate(Request request) {
		String auth = request.getFirstRequestHeader("authorization");
		if (auth != null) {
			if (auth.length() > 0) {
				String[] authParts = auth.split(" ");
				String thePartWeCareAbout = authParts[authParts.length - 1];

				if (TournamentSystem.getInstance().getManagedServers()
						.stream()
						.filter(ManagedServer::isRunning)
						.anyMatch(s -> s.getInternalAPIAccessKey().equals(thePartWeCareAbout))) {
					return AuthObject;
				}
			}
		}
		return null;
	}
}