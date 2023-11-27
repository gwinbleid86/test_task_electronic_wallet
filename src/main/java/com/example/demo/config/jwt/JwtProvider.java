package com.example.demo.config.jwt;

import com.example.demo.model.Secret;
import com.example.demo.model.User;
import com.example.demo.repository.SecretRepository;
import com.example.demo.repository.UserRepository;
import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.UUID;
import java.util.function.Function;

@Component
@Slf4j
@RequiredArgsConstructor
public class JwtProvider {
    @Value("$(jwt.secret)")
    private String jwtSecret;

    private static final SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

    private static final Long TOKEN_EXPIRE_TIME = 12 * 2_592_000_000L; //milliseconds

    private final UserRepository userRepository;

    private final SecretRepository secretRepository;

    public String generateToken(User user) {
        Secret secret = Secret.builder()
                .secretText(UUID.randomUUID().toString())
                .user(user)
                .isActive(Boolean.TRUE)
                .build();
        secretRepository.saveAndFlush(secret);

        user.getSecrets().add(secret);
        userRepository.save(user);

        return Jwts.builder()
                .setSubject(user.getEmail())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + TOKEN_EXPIRE_TIME))
                .claim("secret", secret.getSecretText())
                .signWith(signatureAlgorithm, getSignKey())
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            Jwts.parser().setSigningKey(getSignKey()).parseClaimsJws(authToken);
            return true;
        } catch (SignatureException ex) {
            log.error("Invalid JWT signature");
        } catch (MalformedJwtException ex) {
            log.error("Invalid JWT token");
        } catch (ExpiredJwtException ex) {
            log.error("Expired JWT token");
        } catch (UnsupportedJwtException ex) {
            log.error("Unsupported JWT token");
        } catch (IllegalArgumentException ex) {
            log.error("JWT claims string is empty.");
        }
        return false;
    }

    public String getLoginFromToken(String token) {
        Claims claims = Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
        return claims.getSubject();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public String extractSecret(String token) {
        return extractClaim(token, claims -> (String) claims.get("secret"));
    }

    public String extractPurpose(String token) {
        return extractClaim(token, claims -> (String) claims.get("purpose"));
    }

    public Key getSignKey() {
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
        return new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token).getBody();
    }
}
