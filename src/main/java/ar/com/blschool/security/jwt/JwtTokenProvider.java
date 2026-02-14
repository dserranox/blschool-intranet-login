package ar.com.blschool.security.jwt;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider {

    private final SecretKey key;
    private final long expirationMinutes;

    public JwtTokenProvider(
            @Value("${security.jwt.secret:change-me-please-change-me-please-change-me-please-1234567890}") String secret,
            @Value("${security.jwt.exp-minutes:120}") long expirationMinutes) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMinutes = expirationMinutes;
    }

    public long getExpirationMinutes() {
        return expirationMinutes;
    }

    public String createToken(String username, List<String> authorities) {
        Instant now = Instant.now();
        Instant exp = now.plusSeconds(expirationMinutes * 60);
        return Jwts.builder()
                .subject(username)
                .claim("auth", authorities)
                .issuedAt(Date.from(now))
                .expiration(Date.from(exp))
                .signWith(key)
                .compact();
    }

    public Jws<Claims> parse(String token) throws JwtException {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    @SuppressWarnings("unchecked")
    public List<String> extractAuthorities(Claims claims) {
        Object a = claims.get("auth");
        if (a instanceof List<?>) {
            return ((List<?>) a).stream().map(Object::toString).collect(Collectors.toList());
        }
        return List.of();
    }
}
