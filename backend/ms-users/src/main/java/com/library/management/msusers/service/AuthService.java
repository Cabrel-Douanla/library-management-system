package com.library.management.msusers.service;

import com.library.management.msusers.dto.LoginRequest;
import com.library.management.msusers.dto.LoginResponse;
import com.library.management.msusers.dto.UserRegistrationRequest;
import com.library.management.msusers.enums.Role;
import com.library.management.msusers.exception.ResourceNotFoundException;
import com.library.management.msusers.model.User;
import com.library.management.msusers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    public User register(UserRegistrationRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new IllegalArgumentException("Email is already registered.");
        }
        if (userRepository.existsByCode(request.getCode())) { // Assuming code is passed or generated here
             throw new IllegalArgumentException("User code is already registered.");
        }

        // Simple code generation (can be more sophisticated for real world)
        String userCode = generateUniqueUserCode(request.getRole());

        var user = User.builder()
                .code(userCode)
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .sexe(request.getSexe())
                .adresse(request.getAdresse())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword())) // Hacher le mot de passe
                .role(request.getRole() != null ? request.getRole() : Role.ADHÉRENT) // Rôle par défaut
                .etat("actif")
                .build();

        return userRepository.save(user);
    }

    public LoginResponse login(LoginRequest request) {
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getEmail(),
                            request.getPassword()
                    )
            );
        } catch (BadCredentialsException ex) {
            // Cette exception est gérée par GlobalExceptionHandler
            throw ex;
        }

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found")); // Ne devrait pas arriver après l'authentification réussie

        var jwtToken = jwtService.generateToken(user);
        return LoginResponse.builder()
                .jwtToken(jwtToken)
                .userRole(user.getRole().name())
                .userEmail(user.getEmail())
                .build();
    }

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
            code = prefix + UUID.randomUUID().toString().substring(0, 5).toUpperCase(); // Génère une partie aléatoire
        } while (userRepository.existsByCode(code)); // Vérifie l'unicité
        return code;
    }
}