package com.library.management.msusers.service;

import com.library.management.msusers.dto.UserRegistrationRequest;
import com.library.management.msusers.dto.UserResponse;
import com.library.management.msusers.dto.UserUpdateRequest;
import com.library.management.msusers.enums.Role;
import com.library.management.msusers.exception.ResourceNotFoundException;
import com.library.management.msusers.model.User;
import com.library.management.msusers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * Crée un nouvel utilisateur (utilisé par l'administrateur ou pour l'inscription).
     * @param request Les données d'enregistrement de l'utilisateur.
     * @return L'utilisateur créé.
     * @throws IllegalArgumentException si l'email ou le code existe déjà.
     */
    @Transactional
    public UserResponse createUser(UserRegistrationRequest request) {
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
                .password(passwordEncoder.encode(request.getPassword()))
                .role(request.getRole() != null ? request.getRole() : Role.ADHÉRENT)
                .etat("actif")
                .build();

        User savedUser = userRepository.save(user);
        return mapToUserResponse(savedUser);
    }

    /**
     * Récupère tous les utilisateurs.
     * @return Une liste de DTOs d'utilisateurs.
     */
    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    /**
     * Récupère un utilisateur par son ID.
     * @param id L'ID de l'utilisateur.
     * @return Le DTO de l'utilisateur.
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé.
     */
    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));
        return mapToUserResponse(user);
    }

    /**
     * Récupère un utilisateur par son email.
     * @param email L'email de l'utilisateur.
     * @return Le DTO de l'utilisateur.
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé.
     */
    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email : " + email));
        return mapToUserResponse(user);
    }

    /**
     * Met à jour les détails d'un utilisateur existant.
     * @param id L'ID de l'utilisateur à mettre à jour.
     * @param request Les données de mise à jour.
     * @return Le DTO de l'utilisateur mis à jour.
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé.
     * @throws IllegalArgumentException si le nouvel email est déjà pris.
     */
    @Transactional
    public UserResponse updateUserDetails(UUID id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        if (request.getNom() != null) existingUser.setNom(request.getNom());
        if (request.getPrenom() != null) existingUser.setPrenom(request.getPrenom());
        if (request.getDateNaissance() != null) existingUser.setDateNaissance(request.getDateNaissance());
        if (request.getSexe() != null) existingUser.setSexe(request.getSexe());
        if (request.getAdresse() != null) existingUser.setAdresse(request.getAdresse());
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("L'email '" + request.getEmail() + "' est déjà pris.");
            }
            existingUser.setEmail(request.getEmail());
        }
        if (request.getEtat() != null) existingUser.setEtat(request.getEtat());
        if (request.getRole() != null) existingUser.setRole(request.getRole()); // Mettre à jour le rôle si fourni

        User updatedUser = userRepository.save(existingUser);
        return mapToUserResponse(updatedUser);
    }

    /**
     * Met à jour le rôle d'un utilisateur spécifique.
     * @param id L'ID de l'utilisateur.
     * @param newRole Le nouveau rôle.
     * @return Le DTO de l'utilisateur mis à jour.
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé.
     */
    @Transactional
    public UserResponse updateUserRole(UUID id, Role newRole) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id));

        existingUser.setRole(newRole);
        User updatedUser = userRepository.save(existingUser);
        return mapToUserResponse(updatedUser);
    }

    /**
     * Supprime un utilisateur par son ID.
     * @param id L'ID de l'utilisateur à supprimer.
     * @throws ResourceNotFoundException si l'utilisateur n'est pas trouvé.
     */
    @Transactional
    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("Utilisateur non trouvé avec l'ID : " + id);
        }
        userRepository.deleteById(id);
    }

    /**
     * Helper pour mapper l'entité User au DTO UserResponse.
     * @param user L'entité utilisateur.
     * @return Le DTO UserResponse.
     */
    private UserResponse mapToUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .code(user.getCode())
                .nom(user.getNom())
                .prenom(user.getPrenom())
                .dateNaissance(user.getDateNaissance())
                .sexe(user.getSexe())
                .adresse(user.getAdresse())
                .email(user.getEmail())
                .role(user.getRole())
                .etat(user.getEtat())
                .build();
    }

    /**
     * Génère un code utilisateur unique basé sur le rôle.
     * @param role Le rôle de l'utilisateur.
     * @return Un code utilisateur unique.
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
            code = prefix + UUID.randomUUID().toString().substring(0, 5).toUpperCase();
        } while (userRepository.existsByCode(code));
        return code;
    }
}