package com.library.management.msusers.service;

import com.library.management.msusers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority; // Import crucial
import org.springframework.security.core.userdetails.User; // Import crucial pour la classe User de Spring Security
import org.springframework.security.core.userdetails.UserDetails; // Import crucial
import org.springframework.security.core.userdetails.UserDetailsService; // Import crucial
import org.springframework.security.core.userdetails.UsernameNotFoundException; // Import crucial
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor // Génère un constructeur avec les champs 'final' (ici userRepository)
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository; // Injecté par Lombok

    /**
     * Charge les détails d'un utilisateur par son nom d'utilisateur (email dans notre cas).
     * Utilisé par Spring Security pour l'authentification.
     * @param email L'email de l'utilisateur.
     * @return Les détails de l'utilisateur.
     * @throws UsernameNotFoundException si l'utilisateur n'est pas trouvé.
     */
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Utilisateur non trouvé avec l'email : " + email));
    }

    /**
     * Crée un objet UserDetails de Spring Security à partir d'informations (email, rôles) extraites d'un JWT.
     * Cette méthode est utilisée par JwtAuthenticationFilter pour reconstruire l'objet d'authentification.
     * @param username L'email de l'utilisateur.
     * @param roles La liste des rôles de l'utilisateur (ex: "ROLE_ADMIN").
     * @return Un objet UserDetails.
     */
    public UserDetails createUserDetailsFromJwt(String username, List<String> roles) {
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
        // La classe User de Spring Security est utilisée ici, le mot de passe n'est pas nécessaire
        // car l'authentification a déjà été faite via le JWT
        return new User(username, "", authorities);
    }
}