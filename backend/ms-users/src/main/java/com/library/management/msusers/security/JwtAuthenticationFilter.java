package com.library.management.msusers.security;

import com.library.management.msusers.service.JwtService;
import com.library.management.msusers.service.UserDetailsServiceImpl;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken; // Import crucial
import org.springframework.security.core.context.SecurityContextHolder; // Import crucial
import org.springframework.security.core.userdetails.UserDetails; // Import crucial
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource; // Import crucial
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List; // Import List
import java.util.Map; // Import Map

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;
    private final UserDetailsServiceImpl userDetailsService;

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        final String authHeader = request.getHeader("Authorization");
        final String jwt;
        final String userEmail;

        // Vérifie si l'en-tête Authorization est présent et commence par "Bearer "
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response); // Passe au filtre suivant
            return;
        }

        jwt = authHeader.substring(7); // Extrait le token JWT (après "Bearer ")
        userEmail = jwtService.extractUsername(jwt); // Extrait l'email de l'utilisateur du token

        // Si l'email est extrait et qu'il n'y a pas déjà d'authentification dans le contexte de sécurité
        if (userEmail != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Dans ms-users, nous chargeons l'utilisateur depuis la base de données
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(userEmail);

            // Vérifie si le token est valide pour l'utilisateur
            if (jwtService.isTokenValid(jwt, userDetails)) {
                // Crée un objet d'authentification pour Spring Security
                UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                        userDetails,
                        null, // Les identifiants sont null car l'utilisateur est authentifié par le JWT
                        userDetails.getAuthorities() // Les rôles de l'utilisateur
                );
                // Définit les détails de l'authentification (comme l'adresse IP du client)
                authToken.setDetails(
                        new WebAuthenticationDetailsSource().buildDetails(request)
                );
                // Place l'objet d'authentification dans le SecurityContext de Spring Security
                // Cela indique à Spring Security que l'utilisateur est authentifié pour la requête courante
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }
        filterChain.doFilter(request, response); // Passe la requête au filtre suivant dans la chaîne
    }
}