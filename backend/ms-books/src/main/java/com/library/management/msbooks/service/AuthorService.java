package com.library.management.msbooks.service;

import com.library.management.msbooks.dto.AuthorRequest;
import com.library.management.msbooks.dto.AuthorResponse;
import com.library.management.msbooks.exception.ResourceNotFoundException;
import com.library.management.msbooks.model.Author;
import com.library.management.msbooks.repository.AuthorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AuthorService {

    private final AuthorRepository authorRepository;

    @Transactional
    public AuthorResponse createAuthor(AuthorRequest request) {
        Author author = Author.builder()
                .nom(request.getNom())
                .prenom(request.getPrenom())
                .dateNaissance(request.getDateNaissance())
                .profil(request.getProfil())
                .bibliographie(request.getBibliographie())
                .build();
        Author savedAuthor = authorRepository.save(author);
        return mapToAuthorResponse(savedAuthor);
    }

    public List<AuthorResponse> getAllAuthors() {
        return authorRepository.findAll().stream()
                .map(this::mapToAuthorResponse)
                .collect(Collectors.toList());
    }

    public AuthorResponse getAuthorById(UUID id) {
        Author author = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auteur non trouvé avec l'ID : " + id));
        return mapToAuthorResponse(author);
    }

    @Transactional
    public AuthorResponse updateAuthor(UUID id, AuthorRequest request) {
        Author existingAuthor = authorRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Auteur non trouvé avec l'ID : " + id));

        existingAuthor.setNom(request.getNom());
        existingAuthor.setPrenom(request.getPrenom());
        existingAuthor.setDateNaissance(request.getDateNaissance());
        existingAuthor.setProfil(request.getProfil());
        existingAuthor.setBibliographie(request.getBibliographie());

        Author updatedAuthor = authorRepository.save(existingAuthor);
        return mapToAuthorResponse(updatedAuthor);
    }

    @Transactional
    public void deleteAuthor(UUID id) {
        if (!authorRepository.existsById(id)) {
            throw new ResourceNotFoundException("Auteur non trouvé avec l'ID : " + id);
        }
        // Note: La suppression en cascade des BookAuthor est gérée par l'annotation @OneToMany(orphanRemoval = true) dans Author
        authorRepository.deleteById(id);
    }

    private AuthorResponse mapToAuthorResponse(Author author) {
        return AuthorResponse.builder()
                .id(author.getId())
                .nom(author.getNom())
                .prenom(author.getPrenom())
                .dateNaissance(author.getDateNaissance())
                .profil(author.getProfil())
                .bibliographie(author.getBibliographie())
                .build();
    }
}