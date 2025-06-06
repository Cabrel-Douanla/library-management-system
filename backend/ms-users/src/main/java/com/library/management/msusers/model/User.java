package com.library.management.msusers.model;

import com.library.management.msusers.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateurs") // Nom de la table dans la base de données
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 20)
    private String code; // Code unique pour l'utilisateur, ex: ADH001, BIB001

    @Column(nullable = false, length = 100)
    private String nom;

    @Column(nullable = false, length = 100)
    private String prenom;

    private LocalDate dateNaissance;

    @Column(length = 10)
    private String sexe;

    @Column(columnDefinition = "TEXT")
    private String adresse;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(name = "mot_de_passe_hash", nullable = false, columnDefinition = "TEXT")
    private String password; // mot_de_passe_hash dans la DB

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(nullable = false, length = 20)
    private String etat; // "actif", "inactif", "suspendu"

    // --- Implémentation de UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        return email; // L'email est utilisé comme nom d'utilisateur pour l'authentification
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Pour l'instant, pas de gestion d'expiration de compte
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pour l'instant, pas de gestion de verrouillage de compte
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Pour l'instant, pas de gestion d'expiration des credentials
    }

    @Override
    public boolean isEnabled() {
        return "actif".equalsIgnoreCase(etat); // Le compte est actif si son état est "actif"
    }

    // Le getter pour le mot de passe est déjà fourni par Lombok (@Data)
    // @Override
    // public String getPassword() { return password; }
}