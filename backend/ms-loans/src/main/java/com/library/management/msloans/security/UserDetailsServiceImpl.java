package com.library.management.msloans.security;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    // Ce UserDetailsService est utilisé uniquement pour construire un UserDetails
    // à partir des claims du JWT après sa validation.
    // Il ne cherche pas un utilisateur dans une base de données locale.
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cette méthode ne doit normalement pas être appelée directement par le filtre JWT
        // mais plutôt par le fournisseur d'authentification si on avait des utilisateurs locaux.
        // Pour le JWT, le JwtAuthenticationFilter construira directement l'Authentication.
        // Cependant, Spring Security nécessite un UserDetailsService pour le DaoAuthenticationProvider.
        throw new UsernameNotFoundException("User loading not supported directly by ms-books's UserDetailsService.");
    }

    // Méthode utilitaire pour construire un UserDetails à partir d'un email et de rôles
    public UserDetails createUserDetailsFromJwt(String username, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        return new User(username, "", authorities); // Le mot de passe n'est pas nécessaire ici
    }
}