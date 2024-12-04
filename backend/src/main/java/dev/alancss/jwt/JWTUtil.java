package dev.alancss.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Instant;
import java.util.Date;
import java.util.Map;

import static java.time.temporal.ChronoUnit.DAYS;

@Service
public class JWTUtil {

    private static final String SECRET_KEY = "y9e#1k8_2t7$e3r_5c4^e0s_t1n&8a3_t6r*0o5_p7m+2i9";

    public String issueToken(String subject) {
        return issueToken(subject, Map.of());
    }

    public String issueToken(String subject, String... scopes) {
        return issueToken(subject, Map.of("scopes", scopes));
    }

    public String issueToken(String subject, Map<String, Object> claims) {
        return Jwts.builder()
                .claims(claims)
                .subject(subject)
                .issuer("https://alancss.dev")
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plus(15, DAYS)))
                .signWith(getSigningKey())
                .compact();
    }

    public String getSubject(String token) {
        return getClaims(token)
                .getSubject();
    }

    private Claims getClaims(String token) {
        return Jwts
                .parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean isTokenValid(String token, String username) {
        String subject = getSubject(token);
        return subject.equals(username) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return getClaims(token).getExpiration().before(Date.from(Instant.now()));
    }

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(SECRET_KEY.getBytes());
    }

}
