package com.library.management.msusers.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail; // Nécessite Spring Boot 3+
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException; // Import crucial
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.net.URI;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice // Permet de gérer les exceptions globalement pour tous les contrôleurs REST
public class GlobalExceptionHandler {

    // Gère les exceptions de ressource non trouvée
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ProblemDetail> handleResourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
        problemDetail.setTitle("Resource Not Found");
        problemDetail.setType(URI.create("https://api.library.com/errors/not-found"));
        problemDetail.setProperty("timestamp", Instant.now());
        return new ResponseEntity<>(problemDetail, HttpStatus.NOT_FOUND);
    }

    // Gère les erreurs de validation des arguments de méthode (ex: @Valid échoue)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ProblemDetail> handleValidationErrors(MethodArgumentNotValidException ex, WebRequest request) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));

        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, "Validation failed");
        problemDetail.setTitle("Invalid Input");
        problemDetail.setType(URI.create("https://api.library.com/errors/invalid-input"));
        problemDetail.setProperty("timestamp", Instant.now());
        problemDetail.setProperty("errors", errors); // Ajoute les détails des erreurs de validation
        return new ResponseEntity<>(problemDetail, HttpStatus.BAD_REQUEST);
    }

    // Gère les violations d'intégrité des données (ex: contrainte UNIQUE violée)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ProblemDetail> handleDataIntegrityViolationException(DataIntegrityViolationException ex, WebRequest request) {
        String errorMessage = "Une ressource avec cet identifiant unique existe déjà.";
        // Tenter d'extraire un message plus spécifique de l'exception sous-jacente
        if (ex.getCause() != null && ex.getCause().getMessage() != null) {
            if (ex.getCause().getMessage().toLowerCase().contains("email")) {
                errorMessage = "Cet email est déjà enregistré.";
            } else if (ex.getCause().getMessage().toLowerCase().contains("code")) {
                errorMessage = "Un utilisateur avec ce code existe déjà.";
            }
            // Autres cas de violation d'intégrité à gérer ici
        }
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.CONFLICT, errorMessage);
        problemDetail.setTitle("Conflit de données");
        problemDetail.setType(URI.create("https://api.library.com/errors/data-conflict"));
        problemDetail.setProperty("timestamp", Instant.now());
        return new ResponseEntity<>(problemDetail, HttpStatus.CONFLICT);
    }

    // Gère les erreurs d'authentification (mauvais identifiants)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ProblemDetail> handleBadCredentialsException(BadCredentialsException ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED, "Email ou mot de passe invalide.");
        problemDetail.setTitle("Échec de l'authentification");
        problemDetail.setType(URI.create("https://api.library.com/errors/authentication-failed"));
        problemDetail.setProperty("timestamp", Instant.now());
        return new ResponseEntity<>(problemDetail, HttpStatus.UNAUTHORIZED);
    }

    // Gère toutes les autres exceptions non capturées
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ProblemDetail> handleGenericException(Exception ex, WebRequest request) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR, "Une erreur inattendue est survenue : " + ex.getMessage());
        problemDetail.setTitle("Erreur Interne du Serveur");
        problemDetail.setType(URI.create("https://api.library.com/errors/internal-server-error"));
        problemDetail.setProperty("timestamp", Instant.now());
        // Il est crucial de logger l'exception complète pour le débogage en production
        ex.printStackTrace();
        return new ResponseEntity<>(problemDetail, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}