package com.gestioneEventi.services;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.gestioneEventi.models.User;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Service for handling JWT token operations.
 * Provides functionality for generating access and refresh tokens, as well as token validation and parsing.
 * Uses HMAC-SHA algorithm for token signing.
 *
 */
@Service
public class JwtService {
    private final SecretKey key;
    private final Duration accessExpiration;
    private final Duration refreshExpiration;

    /**
     * Constructor for JwtService.
     * Initializes the service with JWT configuration from application properties.
     *
     * @param secret The secret key for signing tokens
     * @param accessExpMillis Access token expiration time in milliseconds
     * @param refreshExpMillis Refresh token expiration time in milliseconds
     */
    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-expiration}") long accessExpMillis,
            @Value("${app.jwt.refresh-expiration}") long refreshExpMillis) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessExpiration = Duration.ofMillis(accessExpMillis);
        this.refreshExpiration = Duration.ofMillis(refreshExpMillis);
    }

    /**
     * Generates an access token for the specified user.
     * The token includes user ID, email, and role information.
     *
     * @param user The user for whom to generate the access token
     * @return A signed JWT access token
     */
    public String generateAccessToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("uid", user.getId())
                .claim("role", user.getRole())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(accessExpiration)))
                .signWith(key)
                .compact();
    }

    /**
     * Generates a refresh token for the specified user.
     * Refresh tokens are used to obtain new access tokens without re-authentication.
     *
     * @param user The user for whom to generate the refresh token
     * @return A signed JWT refresh token
     */
    public String generateRefreshToken(User user) {
        Instant now = Instant.now();
        return Jwts.builder()
                .subject(user.getEmail())
                .claim("type", "refresh")
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(refreshExpiration)))
                .signWith(key)
                .compact();
    }

    /**
     * Parses and validates a JWT token.
     * Verifies the token signature and extracts claims.
     *
     * @param token The JWT token to parse
     * @return The parsed JWT claims
     * @throws JwtException if the token is invalid or expired
     */
    public Jws<Claims> parseToken(String token) {
        return Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
    }

    /**
     * Validates whether a JWT token is valid.
     * Checks token signature and expiration.
     *
     * @param token The JWT token to validate
     * @return true if the token is valid, false otherwise
     */
    public boolean isTokenValid(String token) {
        try {
            parseToken(token);
            return true;
        } catch (JwtException ex) {
            return false;
        }
    }
}