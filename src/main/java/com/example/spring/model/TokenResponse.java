package com.example.spring.model;
import java.util.Date;

public class TokenResponse {
    private String token;
    private String username;
    private String role;
    private Date expiration;

    public TokenResponse(String token, String username, String role, Date expiration) {
        this.token = token;
        this.username = username;
        this.role = role;
        this.expiration = expiration;
    }
    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }
}
