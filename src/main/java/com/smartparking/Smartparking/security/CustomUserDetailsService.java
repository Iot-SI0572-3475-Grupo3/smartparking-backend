package com.smartparking.Smartparking.security;

import com.smartparking.Smartparking.entity.iam.User;
import com.smartparking.Smartparking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.UUID;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {

        User user = userRepository.findByUserId(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with identifier: " + identifier
                ));

        // AQUÍ ESTABA EL PROBLEMA → no pasabas el rol con "ROLE_"
        return org.springframework.security.core.userdetails.User
                .withUsername(user.getEmail())
                .password(user.getPasswordHash())
                .authorities("ROLE_" + user.getRole().name())  // ← ¡¡ESTO ES CLAVE!!
                .accountExpired(false)
                .accountLocked(false)
                .credentialsExpired(false)
                .disabled("SUSPENDED".equals(user.getStatus()))
                .build();
    }

    private boolean isValidUUID(String str) {
        try {
            UUID.fromString(str);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}