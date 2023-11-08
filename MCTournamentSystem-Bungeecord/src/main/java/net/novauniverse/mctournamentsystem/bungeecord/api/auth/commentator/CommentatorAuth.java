package net.novauniverse.mctournamentsystem.bungeecord.api.auth.commentator;

import java.util.ArrayList;
import java.util.List;

import net.novauniverse.apilib.http.auth.Authentication;

public class CommentatorAuth extends Authentication {
	private CommentatorUser user;
	
	public CommentatorAuth(CommentatorUser user) {
		this.user = user;
	}

	@Override
	public List<String> getPermissions() {
		return new ArrayList<>();
	}
	
	public CommentatorUser getUser() {
		return user;
	}
}