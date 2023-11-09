package net.novauniverse.mctournamentsystem.bungeecord.api.auth;

import java.security.KeyPair;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;

import net.novauniverse.apilib.http.auth.AuthenticationProvider;
import net.novauniverse.mctournamentsystem.bungeecord.TournamentSystem;
import net.novauniverse.mctournamentsystem.bungeecord.security.JWTProperties;

public abstract class JWTAuthProvider implements AuthenticationProvider {
	protected JWTVerifier jwtVerifier;

	public JWTAuthProvider() {
		KeyPair pair = TournamentSystem.getInstance().getTokenKeyPair();
		Algorithm algorithm = Algorithm.RSA256((RSAPublicKey) pair.getPublic(), (RSAPrivateKey) pair.getPrivate());
		jwtVerifier = JWT.require(algorithm)
				.withIssuer(JWTProperties.ISSUER)
				.build();
	}
}