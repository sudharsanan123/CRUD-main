package com.example.spring.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.example.spring.service.JWTService;
import com.example.spring.service.MyUserDetailsService;

import java.io.IOException;
import java.util.Collection;

@Component
public class JwtFilter extends OncePerRequestFilter {

    private static final Logger LOG = LoggerFactory.getLogger(JwtFilter.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private MyUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String path = request.getRequestURI();
        
        if (path.equals("/users/register") || path.equals("/users/login")) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = extractToken(request.getHeader("Authorization"));
        if (token == null) {
            LOG.warn("Missing or malformed Authorization header.");
            filterChain.doFilter(request, response);
            return;
        }

        String username = getUsernameFromToken(token, response);
        if (username == null) return;

        authenticateUserIfNecessary(request, response, token, username);
        filterChain.doFilter(request, response);
    }
    

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private String getUsernameFromToken(String token, HttpServletResponse response) {
        try {
            String username = jwtService.extractUserName(token);
            LOG.debug("JwtFilter.getUsernameFromToken(token, response) => token: {}, username: {}", token, username);
            return username;
        } catch (Exception e) {
            LOG.error("Error extracting username from token. Cause: {}", e.getMessage());
            try {
                handleUnauthorized(response, "Invalid or expired token.");
            } catch (IOException ioException) {
                LOG.error("Failed to handle unauthorized response. Cause: {}", ioException.getMessage());
            }
            return null; // Indicate failure to extract username
        }
    }
   
    private void authenticateUserIfNecessary(HttpServletRequest request, HttpServletResponse response, String token, String username) {
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            LOG.debug("Attempting to authenticate user: {}", username);
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
    
            if (userDetails == null) {
                LOG.warn("User details not found for username: {}", username);
                try {
                    handleUnauthorized(response, "User not found.");
                } catch (IOException ioException) {
                    LOG.error("Failed to handle unauthorized response. Cause: {}", ioException.getMessage());
                }
                return;
            }
    
            if (jwtService.validateToken(token, userDetails)) {
                setAuthentication(request, userDetails);
            } else {
                LOG.warn("Token validation failed for user: {}", username);
                try {
                    handleUnauthorized(response, "Invalid token.");
                } catch (IOException ioException) {
                    LOG.error("Failed to handle unauthorized response. Cause: {}", ioException.getMessage());
                }
            }
        }
    }
    

    private void setAuthentication(HttpServletRequest request, UserDetails userDetails) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
        SecurityContextHolder.getContext().setAuthentication(authToken);

        LOG.info("User {} authenticated successfully.", userDetails.getUsername());
        logUserRoles(userDetails);
    }

    private void logUserRoles(UserDetails userDetails) {
        Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();
        authorities.forEach(authority -> 
            LOG.debug("User has role: {}", authority.getAuthority())
        );
    }

    private void handleUnauthorized(HttpServletResponse response, String message) throws IOException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write(message);
        LOG.debug("JwtFilter.handleUnauthorized(response, message) => message: {}", message);
    }
}
 