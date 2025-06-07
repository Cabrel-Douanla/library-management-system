package com.library.management.msbooks.config;

import com.library.management.msbooks.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final AuthenticationProvider authenticationProvider;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(auth -> auth
                        // Actuator et Swagger sont publics pour la surveillance et la documentation
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Authors Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/authors").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.PUT, "/api/authors/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/authors/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/authors", "/api/authors/**").permitAll() // Lecture pour tous

                        // Books Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/books").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.PUT, "/api/books/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/books/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.GET, "/api/books", "/api/books/**").permitAll() // Lecture pour tous
                        // Endpoint spécifique pour la mise à jour de stock (appelé par ms-loans)
                        // Peut être rendu plus permissif ou avec une clé API spécifique si pas de JWT de ms-loans
                        .requestMatchers(HttpMethod.PATCH, "/api/books/{bookId}/stock").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE") // ms-loans fera l'appel avec un JWT

                        .anyRequest().authenticated() // Toutes les autres requêtes nécessitent une authentification
                )
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:4200")); // Mettre l'URL de votre frontend Angular
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}