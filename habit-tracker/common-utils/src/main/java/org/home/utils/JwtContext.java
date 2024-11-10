package org.home.utils;

import io.jsonwebtoken.Claims;

/**
 * The `JwtContext` class provides a way to store and access JWT claims.
 */
public class JwtContext {
    private static final ThreadLocal<Claims> claimsThreadLocal = new ThreadLocal<>();

    /**
     * Stores the given JWT claims in the current thread's context.
     *
     * @param claims the JWT claims to be stored
     */
    public static void setClaims(Claims claims) {
        claimsThreadLocal.set(claims);
    }

    /**
     * Retrieves the JWT claims stored in the current thread's context.
     *
     * @return the JWT claims associated with the current thread
     */
    public static Claims getClaims() {
        return claimsThreadLocal.get();
    }

    /**
     * Clears the JWT claims stored in the current thread's context.
     */
    public static void clear() {
        claimsThreadLocal.remove();
    }
}
