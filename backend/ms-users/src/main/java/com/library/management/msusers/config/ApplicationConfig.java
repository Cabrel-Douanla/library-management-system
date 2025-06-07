package com.library.management.msusers.config;

import com.library.management.msusers.repository.UserRepository;
import com.library.management.msusers.service.UserDetailsServiceImpl; // Import crucial
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager; // Import crucial
import org.springframework.security.authentication.AuthenticationProvider; // Import crucial
import org.springframework.security.authentication.dao.DaoAuthenticationProvider; // Import crucial
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration; // Import crucial
import org.springframework.security.core.userdetails.UserDetailsService; // Import crucial
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor // Génère un constructeur avec les champs 'final' (ici userRepository)
public class ApplicationConfig {

    private final UserRepository userRepository;

    /**
     * Fournit une implémentation de UserDetailsService.
     * @return L'implémentation de UserDetailsService.
     */
    @Bean
    public UserDetailsService userDetailsService() {
        // Instancie UserDetailsServiceImpl en lui passant UserRepository
        return new UserDetailsServiceImpl(userRepository);
    }

    /**
     * Fournit un AuthenticationProvider qui utilise notre UserDetailsService et PasswordEncoder.
     * @return L'AuthenticationProvider configuré.
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService()); // Utilise notre UserDetailsService
        authProvider.setPasswordEncoder(passwordEncoder()); // Utilise notre PasswordEncoder
        return authProvider;
    }

    /**
     * Fournit le gestionnaire d'authentification.
     * @param config La configuration de l'authentification.
     * @return Le gestionnaire d'authentification.
     * @throws Exception Si une erreur de configuration se produit.
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Fournit l'encodeur de mot de passe (BCrypt).
     * @return Le PasswordEncoder.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}