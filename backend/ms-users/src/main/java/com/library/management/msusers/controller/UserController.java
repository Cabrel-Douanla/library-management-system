package com.library.management.msusers.controller;

import com.library.management.msusers.dto.UserResponse;
import com.library.management.msusers.dto.UserUpdateRequest;
import com.library.management.msusers.enums.Role;
import com.library.management.msusers.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping
    // Permet aux ADMINs et BIBLIOTHÉCAIREs de lister tous les utilisateurs
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<List<UserResponse>> getAllUsers() {
        List<UserResponse> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    // Permet aux ADMINs et BIBLIOTHÉCAIREs de voir n'importe quel user.
    // Un ADHÉRENT peut voir son propre profil (à ajouter une logique plus fine ici si nécessaire)
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable UUID id) {
        UserResponse user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @PutMapping("/{id}")
    // Permet aux ADMINs et BIBLIOTHÉCAIREs de mettre à jour un utilisateur (sauf le rôle)
    @PreAuthorize("hasAnyRole('ADMIN', 'BIBLIOTHÉCAIRE')")
    public ResponseEntity<UserResponse> updateUserDetails(@PathVariable UUID id, @Valid @RequestBody UserUpdateRequest request) {
        UserResponse updatedUser = userService.updateUserDetails(id, request);
        return ResponseEntity.ok(updatedUser);
    }

    @PutMapping("/{id}/role")
    // Seul l'ADMIN peut changer les rôles
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUserRole(@PathVariable UUID id, @RequestParam Role newRole) {
        UserResponse updatedUser = userService.updateUserRole(id, newRole);
        return ResponseEntity.ok(updatedUser);
    }

    @DeleteMapping("/{id}")
    // Seul l'ADMIN peut supprimer des utilisateurs
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }
}