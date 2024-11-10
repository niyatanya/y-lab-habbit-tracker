package org.home.utils;

import io.jsonwebtoken.Claims;

public class JwtContext {
    private static final ThreadLocal<Claims> claimsThreadLocal = new ThreadLocal<>();

    public static void setClaims(Claims claims) {
        claimsThreadLocal.set(claims);
    }

    public static Claims getClaims() {
        return claimsThreadLocal.get();
    }

    public static void clear() {
        claimsThreadLocal.remove();
    }
}
