package com.library.management.msusers.model;

import com.library.management.msusers.enums.Role;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data; // Génère getters, setters, toString, equals, hashCode
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails; // Import crucial

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data // Va générer getPassword() et getUsername() si les champs sont nommés
      // `password` et `username`
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "utilisateurs")
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false, length = 20)
    private String code;

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
    private String password; // Ce champ est utilisé par getPassword()

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private Role role;

    @Column(nullable = false, length = 20)
    private String etat; // "actif", "inactif", "suspendu"

    // --- Implémentation des méthodes de l'interface UserDetails ---

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Retourne une liste d'autorités basées sur le rôle de l'utilisateur
        // Le préfixe "ROLE_" est une convention de Spring Security
        return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    @Override
    public String getUsername() {
        // L'email est utilisé comme nom d'utilisateur pour l'authentification
        return email;
    }

    @Override
    public String getPassword() {
        // Lombok génère déjà le getter pour `password` grâce à @Data,
        // mais pour l'implémentation de UserDetails, il est bon d'avoir l'override
        // explicite
        return password;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Pour l'instant, la gestion de l'expiration du compte n'est pas implémentée
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Pour l'instant, la gestion du verrouillage de compte n'est pas implémentée
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Pour l'instant, la gestion de l'expiration des identifiants n'est pas
                     // implémentée
    }

    @Override
    public boolean isEnabled() {
        // Le compte est considéré comme activé si son état est "actif"
        return "actif".equalsIgnoreCase(etat);
    }
}