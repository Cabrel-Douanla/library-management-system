package com.library.management.msusers.repository;

import com.library.management.msusers.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email); // Pour l'authentification
    Optional<User> findByCode(String code);   // Pour la recherche par code utilisateur

    boolean existsByEmail(String email); // Pour vérifier l'unicité de l'email à l'inscription
    boolean existsByCode(String code);   // Pour vérifier l'unicité du code à l'inscription
}