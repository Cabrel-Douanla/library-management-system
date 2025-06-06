package com.library.management.msusers.service;

import com.library.management.msusers.dto.UserResponse;
import com.library.management.msusers.dto.UserUpdateRequest;
import com.library.management.msusers.enums.Role;
import com.library.management.msusers.exception.ResourceNotFoundException;
import com.library.management.msusers.model.User;
import com.library.management.msusers.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder; // Pourrait être utilisé pour une réinitialisation de mot de passe par admin

    public List<UserResponse> getAllUsers() {
        return userRepository.findAll().stream()
                .map(this::mapToUserResponse)
                .collect(Collectors.toList());
    }

    public UserResponse getUserById(UUID id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));
        return mapToUserResponse(user);
    }

    public UserResponse getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with email: " + email));
        return mapToUserResponse(user);
    }

    public UserResponse updateUserDetails(UUID id, UserUpdateRequest request) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        // Mise à jour des champs si présents dans la requête
        if (request.getNom() != null) existingUser.setNom(request.getNom());
        if (request.getPrenom() != null) existingUser.setPrenom(request.getPrenom());
        if (request.getDateNaissance() != null) existingUser.setDateNaissance(request.getDateNaissance());
        if (request.getSexe() != null) existingUser.setSexe(request.getSexe());
        if (request.getAdresse() != null) existingUser.setAdresse(request.getAdresse());
        if (request.getEmail() != null && !request.getEmail().equals(existingUser.getEmail())) {
            if (userRepository.existsByEmail(request.getEmail())) {
                throw new IllegalArgumentException("Email already taken.");
            }
            existingUser.setEmail(request.getEmail());
        }
        if (request.getEtat() != null) existingUser.setEtat(request.getEtat());
        // Le rôle doit être mis à jour via une méthode dédiée ou un endpoint séparé pour un contrôle strict
        // if (request.getRole() != null) existingUser.setRole(request.getRole());

        User updatedUser = userRepository.save(existingUser);
        return mapToUserResponse(updatedUser);
    }

    public UserResponse updateUserRole(UUID id, Role newRole) {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + id));

        existingUser.setRole(newRole);
        User updatedUser = userRepository.save(existingUser);
        return mapToUserResponse(updatedUser);
    }

    public void deleteUser(UUID id) {
        if (!userRepository.existsById(id)) {
            throw new ResourceNotFoundException("User not found with ID: " + id);
        }
        userRepository.deleteById(id);
    }

    // Helper pour mapper l'entité User au DTO UserResponse
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
}