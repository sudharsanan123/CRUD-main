package com.example.spring.model;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.example.spring.config.Role;

import java.util.Collection;

public class UserPrincipal implements UserDetails {
    private final int id; 
    private final String username;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;
    private final Role role; // Role field to differentiate user types

    // Constructor for User
    public UserPrincipal(User user) { // Use a common User class
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword();
        this.authorities = AuthorityUtils.createAuthorityList(user.getRole().name());
        this.role = user.getRole(); // Set role based on user type
    }

    // Implement UserDetails methods
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    // Method to get role
    public Role getRole() {
        return role;
    }

    // Add other methods if necessary, such as getId()
    public int getId() {
        return id;
    }
}
