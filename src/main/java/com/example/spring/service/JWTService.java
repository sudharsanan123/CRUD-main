package com.example.spring.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import com.example.spring.config.Role;
import com.example.spring.model.TokenResponse;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.slf4j.Logger;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
@Service
public class JWTService {

    private static final Logger logger = LoggerFactory.getLogger(JWTService.class);
    private String secretkey = "";

    public JWTService() {
        try {
            KeyGenerator keyGen = KeyGenerator.getInstance("HmacSHA256");
            SecretKey sk = keyGen.generateKey();
            secretkey = Base64.getEncoder().encodeToString(sk.getEncoded());
            logger.info("JWTService.JWTService() => Secret key generated successfully.");
        } catch (NoSuchAlgorithmException e) {
            logger.error("JWTService.JWTService() => Error generating secret key", e);
            throw new RuntimeException(e);
        }
    }
    public String generateToken(String username, Role role) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", role); // Include role in the token
    
        long currentTimeMillis = System.currentTimeMillis();
        Date expirationDate = new Date(currentTimeMillis + 600 * 6000 * 10000); // 1 hour expiration
        String token = Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(currentTimeMillis))
                .expiration(expirationDate)
                .signWith(getKey())
                .compact();
        
    TokenResponse tokenResponse = new TokenResponse(token, username, role.name(), expirationDate);
    try {
        return new ObjectMapper().writeValueAsString(tokenResponse);
    } catch (Exception e) {
        throw new RuntimeException("Error converting token data to JSON", e);
    }
}
    public SecretKey getKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretkey);
        logger.debug("JWTService.getKey() => Retrieving secret key.");
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public String extractUserName(String token) {
        logger.debug("JWTService.extractUserName(token)=>token;{}", token);
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimResolver) {
        final Claims claims = extractAllClaims(token);
        logger.debug("JWTService.extractClaim(token, claimResolver) => token: {}", token);
        return claimResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        logger.debug("JWTService.extractAllClaims(token) => token: {}", token);
        return Jwts.parser()
                .verifyWith(getKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    public boolean validateToken(String token, UserDetails userDetails) {
        final String userName = extractUserName(token);
        boolean isValid = (userName.equals(userDetails.getUsername()) && !isTokenExpired(token));
        logger.info("JWTService.validateToken(token, userDetails) => userName: {}, valid: {}", userName, isValid);
        return isValid;
    }

    public boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        logger.info("JWTService.isTokenExpired(token) => token: {}, expired: {}", token, expired);
        return expired;
    }

    private Date extractExpiration(String token) {
        logger.debug("JWTService.extractExpiration(token) => token: {}", token);
        return extractClaim(token, Claims::getExpiration);
    }

}
