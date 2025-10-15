package com.payitforward.platform.security;

import org.springframework.stereotype.Component;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class JwtUtil {
    
    private String jwtSecret = "mySecretKey";
    private long jwtExpiration = 86400000; // 24 hours
    
    public String generateToken(String username) {
        return createToken(username);
    }
    
    private String createToken(String username) {
        // Simple token format: username:expiration:encoded
        long now = System.currentTimeMillis();
        long expirationTime = now + jwtExpiration;
        
        String tokenData = username + ":" + expirationTime;
        return Base64.getEncoder().encodeToString(tokenData.getBytes());
    }
    
    public String getUsernameFromToken(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length >= 2) {
                return parts[0];
            }
        } catch (Exception e) {
            // Token is invalid
        }
        return null;
    }
    
    public boolean validateToken(String token) {
        try {
            String username = getUsernameFromToken(token);
            return username != null && !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
    
    public boolean isTokenExpired(String token) {
        try {
            String decoded = new String(Base64.getDecoder().decode(token));
            String[] parts = decoded.split(":");
            if (parts.length >= 2) {
                long expirationTime = Long.parseLong(parts[1]);
                return System.currentTimeMillis() > expirationTime;
            }
        } catch (Exception e) {
            // Token is invalid
        }
        return true;
    }
}
