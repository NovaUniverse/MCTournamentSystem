package net.novauniverse.mctournamentsystem.bungeecord.security;

import org.mindrot.jbcrypt.BCrypt;

public class PasswordHashing {
	public static String hashPassword(String password) {
		return BCrypt.hashpw(password, BCrypt.gensalt());
	}

	public static boolean verifyPassword(String password, String hash) {
		return BCrypt.checkpw(password, hash);
	}
}