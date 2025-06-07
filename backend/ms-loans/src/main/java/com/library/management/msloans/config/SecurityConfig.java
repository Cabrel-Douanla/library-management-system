package com.library.management.msloans.config;

import com.library.management.msloans.security.JwtAuthenticationFilter;
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
                        .requestMatchers("/actuator/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()

                        // Loans Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/loans").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE") // Création d'emprunt
                        .requestMatchers(HttpMethod.GET, "/api/loans", "/api/loans/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE", "ADHÉRENT") // Consultation
                        .requestMatchers(HttpMethod.PUT, "/api/loans/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE") // Mise à jour du statut
                        .requestMatchers(HttpMethod.DELETE, "/api/loans/**").hasRole("ADMIN") // Suppression

                        // Returns Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/returns").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE") // Enregistrement de retour
                        .requestMatchers(HttpMethod.GET, "/api/returns", "/api/returns/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.PUT, "/api/returns/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/returns/**").hasRole("ADMIN")

                        // Reservations Endpoints
                        .requestMatchers(HttpMethod.POST, "/api/reservations").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE", "ADHÉRENT") // Réservation
                        .requestMatchers(HttpMethod.GET, "/api/reservations", "/api/reservations/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE", "ADHÉRENT")
                        .requestMatchers(HttpMethod.PUT, "/api/reservations/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.DELETE, "/api/reservations/**").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE")
                        .requestMatchers(HttpMethod.PATCH, "/api/reservations/{id}/convert-to-loan").hasAnyRole("ADMIN", "BIBLIOTHÉCAIRE") // Conversion

                        .anyRequest().authenticated()
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