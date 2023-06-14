package net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator;

import net.novauniverse.apilib.http.auth.Authentication;
import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.apilib.http.request.Request;
import net.novauniverse.mctournamentsystem.bungeecord.api.auth.apikey.APIKeyStore;

public class CommentatorAuthProvider implements AuthenticationProvider {
	@Override
	public Authentication authenticate(Request request) {
		String auth = request.getFirstRequestHeader("authorization");
		if (auth != null) {
			if (auth.length() > 0) {
				String[] authParts = auth.split(" ");
				String thePartWeCareAbout = authParts[authParts.length - 1];

				CommentatorAuth result = APIKeyStore.getCommentatorKey(thePartWeCareAbout);
				if (result != null) {
					return result;
				}

				result = APIKeyStore.getCommentatorKey(thePartWeCareAbout);
				if (result != null) {
					return result;
				}
			}
		}

		if (request.getQueryParameters().containsKey("commentator_key")) {
			CommentatorAuth result = APIKeyStore.getCommentatorKey(request.getQueryParameters().get("commentator_key"));
			if (result != null) {
				return result;
			}
		}
		return null;
	}
}