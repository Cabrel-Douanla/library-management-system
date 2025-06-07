package com.library.management.msusers.config;

import com.library.management.msusers.security.JwtAuthenticationFilter; // Import crucial
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider; // Import crucial
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity; // Import crucial
import org.springframework.security.config.annotation.web.builders.HttpSecurity; // Import crucial
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity; // Import crucial
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer; // Import crucial
import org.springframework.security.config.http.SessionCreationPolicy; // Import crucial
import org.springframework.security.web.SecurityFilterChain; // Import crucial
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter; // Import crucial
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity // Active la sécurité web de Spring Security
@EnableMethodSecurity // Permet d'utiliser les annotations @PreAuthorize, @PostAuthorize, etc.
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter; // Notre filtre JWT personnalisé
    private final AuthenticationProvider authenticationProvider; // Le fournisseur d'authentification configuré

    /**
     * Configure la chaîne de filtres de sécurité HTTP.
     * @param http L'objet HttpSecurity pour la configuration.
     * @return La chaîne de filtres de sécurité.
     * @throws Exception En cas d'erreur de configuration.
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // Désactive la protection CSRF pour les API REST sans état de session
                .cors(cors -> cors.configurationSource(corsConfigurationSource())) // Active CORS avec notre configuration
                .authorizeHttpRequests(auth -> auth
                        // Points d'accès publics pour l'authentification, Actuator et Swagger
                        .requestMatchers("/api/auth/**", "/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        // Gestion des utilisateurs (GET pour BIBLIOTHÉCAIRE, POST/PUT/DELETE pour ADMIN)
                        .requestMatchers(HttpMethod.GET, "/api/users/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.POST, "/api/users").hasRole("ADMIN") // Seul ADMIN peut créer
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        // Toute autre requête nécessite une authentification
                        .anyRequest().authenticated()
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Pas de gestion de session (API REST sans état)
                .authenticationProvider(authenticationProvider) // Utilise notre fournisseur d'authentification
                // Ajoute notre filtre JWT AVANT le filtre d'authentification par nom d'utilisateur/mot de passe de Spring Security
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    /**
     * Configure les règles CORS (Cross-Origin Resource Sharing).
     * Permet à l'application Angular (localhost:4200) de communiquer avec ce service.
     * @return La source de configuration CORS.
     */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Autorise les requêtes provenant de l'URL de votre frontend Angular
        configuration.setAllowedOrigins(List.of("http://localhost:4200"));
        // Autorise les méthodes HTTP standard pour les API REST
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        // Autorise tous les en-têtes HTTP
        configuration.setAllowedHeaders(List.of("*"));
        // Autorise l'envoi de cookies ou d'en-têtes d'autorisation (comme le JWT)
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        // Applique cette configuration CORS à tous les chemins d'accès (/**)
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}