package org.home.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.SignatureAlgorithm;
import org.home.model.Role;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for handling JWT token generation and validation.
 */
public class JwtTokenUtil {

    /** Secret key used for signing JWT tokens */
    public static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    /** Token expiration time in milliseconds (10 hours) */
    private static final long EXPIRATION_TIME = 36000000;

    /**
     * Generates a JWT token for a given username and role.
     *
     * @param username the username to be included as the subject of the token
     * @param role the role of the user to be included as a claim
     * @return the generated JWT token as a string
     */
    public static String generateToken(String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role);
        claims.put("username", username);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SECRET_KEY)
                .compact();
    }

    /**
     * Validates a JWT token and extracts the claims if the token is valid.
     *
     * @param token the JWT token to be validated
     * @return the claims contained in the token
     */
    public static Claims validateToken(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(SECRET_KEY)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}
