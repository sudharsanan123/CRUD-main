package com.example.spring.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.spring.model.UserPrincipal;
import com.example.spring.model.User; 
import com.example.spring.repo.UserRepo; 

import java.util.Optional;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepo userRepo; 

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> userOptional = userRepo.findByUsername(username);
        if (userOptional.isPresent()) {
            return new UserPrincipal(userOptional.get());
        }
        throw new UsernameNotFoundException("User not found: " + username);
    }
}
