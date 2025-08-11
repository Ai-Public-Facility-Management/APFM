package server.domain;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Component
@RequiredArgsConstructor
public class JwtUtil {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private long expirationMs; // ← 오타 수정

    /** 공통 서명 키 */
    private Key getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    /** 토큰 생성 (role 없이) */
    public String generateToken(String email) {
        return generateToken(email, null);
    }

    /** 토큰 생성 (role 포함) */
    public String generateToken(String email, String role) {
        Date now = new Date();
        Date expiration = new Date(now.getTime() + expirationMs);

        var builder = Jwts.builder()
                .setSubject(email)
                .setIssuedAt(now)
                .setExpiration(expiration)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256);

        if (role != null && !role.isEmpty()) {
            builder.claim("role", role); // "ADMIN" / "USER"
        }
        return builder.compact();
    }

    /** 토큰에서 이메일 추출 */
    public String extractEmail(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }

    /** 토큰에서 역할 추출 (없으면 null) */
    public String extractRole(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }

    /** 토큰 유효성 검사 */
    public boolean validateToken(String token) {
        try{
            Jwts.parserBuilder().setSigningKey(getSigningKey()).build().parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /** 남은 만료시간(ms) */
    public long getRemainingExpiration(String token) {
        Date expiration = Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();

        return expiration.getTime() - System.currentTimeMillis();
    }

    /** Authorization: Bearer ... 에서 토큰만 분리 */
    public String resolveToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}