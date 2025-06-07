package com.library.management.msusers.service;

import com.library.management.msusers.dto.LoginRequest;
import com.library.management.msusers.dto.LoginResponse;
import com.library.management.msusers.dto.UserRegistrationRequest;
import com.library.management.msusers.enums.Role;
import com.library.management.msusers.exception.ResourceNotFoundException;
import com.library.management.msusers.model.User;
import com.library.management.msusers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager; // Import crucial
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager; // Injecté par ApplicationConfig

    /**
     * Enregistre un nouvel utilisateur.
     * @param request Les données d'enregistrement de l'utilisateur.
     * @return L'utilisateur enregistré.
     * @throws IllegalArgumentException si l'email ou le code existe déjà.
     */
    @Transactional
    public User register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("L'email '" + request.getEmail() + "' est déjà enregistré.");
        }

        String userCode = request.getCode();
        if (userCode == null || userCode.isEmpty()) {
            userCode = generateUniqueUserCode(request.getRole());
        } else if (userRepository.existsByCode(userCode)) {
            throw new IllegalArgumentException("Le code utilisateur '" + userCode + "' existe déjà.");
        }

        var user = User.builder()
                .code(userCode)
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .sexe(request.getSexe())
                .adresse(request.getAdresse())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hacher le mot de passe avant de le sauvegarder
                .role(request.getRole() != null ? request.getRole() : Role.ADHÉRENT) // Rôle par défaut: ADHÉRENT
                .etat("actif")
                .build();

        return userRepository.save(user);
    }

    /**
     * Authentifie un utilisateur et génère un token JWT.
     * @param request Les identifiants de connexion (email, mot de passe).
     * @return Un DTO de réponse contenant le token JWT, le rôle et l'email de l'utilisateur.
     * @throws BadCredentialsException si les identifiants sont incorrects.
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé (ne devrait pas arriver après l'authentification réussie).
     */
    public LoginResponse login(LoginRequest request) {
        try {
            // Tente d'authentifier l'utilisateur avec le gestionnaire d'authentification de Spring Security
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            // L'exception BadCredentialsException est gérée par GlobalExceptionHandler pour un message d'erreur cohérent
            throw ex;
        }

        // Si l'authentification réussit, récupère l'utilisateur et génère le token JWT
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé après l'authentification réussie : " + request.getEmail()));

        var jwtToken = jwtService.generateToken(user); // Génère le token JWT pour l'utilisateur
        return LoginResponse.builder()
                .jwtToken(jwtToken)
                .userRole(user.getRole().name())
                .userEmail(user.getEmail())
                .build();
    }

    /**
     * Génère un code utilisateur unique basé sur le rôle.
     * @param role Le rôle de l'utilisateur.
     * @return Un code utilisateur unique (ex: ADH001, BIB001).
     */
    private String generateUniqueUserCode(Role role) {
        String prefix;
        switch (role) {
            case ADHÉRENT: prefix = "ADH"; break;
            case BIBLIOTHÉCAIRE: prefix = "BIB"; break;
            case ADMIN: prefix = "ADM"; break;
            default: prefix = "USR";
        }
        String code;
        do {
            // Utilise une partie aléatoire d'UUID pour assurer l'unicité
            code = prefix + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (userRepository.existsByCode(code)); // Vérifie que le code généré n'existe pas déjà
        return code;
    }
}