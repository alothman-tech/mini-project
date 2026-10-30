package nl.hu.library.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import nl.hu.library.domain.Role;
import nl.hu.library.domain.User;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

public final class JwtUtil {
    private static final long EXPIRY_MS = 60 * 60 * 1000; // 1 uur

    // Op productie ALTIJD via environment variable JWT_SECRET (minimaal 32 tekens).
    private static final SecretKey KEY = Keys.hmacShaKeyFor(
            secret().getBytes(StandardCharsets.UTF_8));

    private JwtUtil() { }

    public static String createToken(User user) {
        Date now = new Date();
        return Jwts.builder()
                .subject(user.getUsername())
                .claim("uid", user.getId())
                .claim("role", user.getRole().name())
                .issuedAt(now)
                .expiration(new Date(now.getTime() + EXPIRY_MS))
                .signWith(KEY)
                .compact();
    }

    /** Gooit een exception bij een ongeldig, aangepast of verlopen token. */
    public static AuthUser parse(String token) {
        Claims claims = Jwts.parser().verifyWith(KEY).build()
                .parseSignedClaims(token).getPayload();
        return new AuthUser(claims.get("uid", Integer.class), claims.getSubject(),
                Role.valueOf(claims.get("role", String.class)));
    }

    private static String secret() {
        String s = System.getenv("JWT_SECRET");
        return (s == null || s.length() < 32)
                ? "dev-only-secret-change-me-dev-only-secret-change-me"
                : s;
    }
}
