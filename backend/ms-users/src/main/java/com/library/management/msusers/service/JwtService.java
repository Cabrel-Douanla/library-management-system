package com.library.management.msusers.service;

import io.jsonwebtoken.Claims; // Import crucial
import io.jsonwebtoken.Jwts;   // Import crucial
import io.jsonwebtoken.io.Decoders; // Import crucial
import io.jsonwebtoken.security.Keys; // Import crucial
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority; // Import crucial
import org.springframework.security.core.userdetails.UserDetails; // Import crucial
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtService {

    @Value("${jwt.secret}")
    private String SECRET_KEY; // Clé secrète JWT, injectée depuis la configuration (ms-users.properties)

    @Value("${jwt.expiration}")
    private long EXPIRATION_TIME; // Durée de validité du token, injectée

    /**
     * Extrait le nom d'utilisateur (subject, généralement l'email) du token JWT.
     * @param token Le token JWT.
     * @return Le nom d'utilisateur.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    /**
     * Extrait une claim spécifique du token JWT.
     * @param token Le token JWT.
     * @param claimsResolver Fonction pour résoudre la claim.
     * @param <T> Type de la claim.
     * @return La claim extraite.
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    /**
     * Génère un token JWT pour un UserDetails.
     * @param userDetails Les détails de l'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateToken(UserDetails userDetails) {
        return generateToken(new HashMap<>(), userDetails);
    }

    /**
     * Génère un token JWT avec des claims supplémentaires.
     * @param extraClaims Claims supplémentaires à inclure dans le token.
     * @param userDetails Les détails de l'utilisateur.
     * @return Le token JWT généré.
     */
    public String generateToken(
            Map<String, Object> extraClaims,
            UserDetails userDetails
    ) {
        // Ajoute les rôles de l'utilisateur comme claim personnalisée dans le token
        extraClaims.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority) // Map GrantedAuthority à String (ex: "ROLE_ADMIN")
                .toList());

        return Jwts // Utilise la classe Jwts de la bibliothèque jjwt
                .builder()
                .claims(extraClaims) // Ajoute les claims personnalisées
                .subject(userDetails.getUsername()) // Définit le sujet (nom d'utilisateur/email)
                .issuedAt(new Date(System.currentTimeMillis())) // Date d'émission du token
                .expiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME)) // Date d'expiration du token
                .signWith(getSigningKey()) // Signe le token avec la clé secrète
                .compact(); // Compacte le token en une chaîne de caractères
    }

    /**
     * Valide un token JWT par rapport aux détails d'un utilisateur.
     * @param token Le token JWT à valider.
     * @param userDetails Les détails de l'utilisateur.
     * @return Vrai si le token est valide pour l'utilisateur, faux sinon.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername())) && !isTokenExpired(token);
    }

    /**
     * Vérifie si le token JWT est expiré.
     * @param token Le token JWT.
     * @return Vrai si le token est expiré, faux sinon.
     */
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    /**
     * Extrait la date d'expiration du token JWT.
     * @param token Le token JWT.
     * @return La date d'expiration.
     */
    private Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    /**
     * Extrait toutes les claims (payload) du token JWT.
     * @param token Le token JWT.
     * @return L'objet Claims contenant toutes les claims.
     */
    private Claims extractAllClaims(String token) {
        return Jwts // Utilise la classe Jwts
                .parser()
                .verifyWith(getSigningKey()) // Vérifie la signature avec la clé secrète
                .build()
                .parseSignedClaims(token) // Parse le token signé
                .getPayload(); // Récupère le payload (les claims)
    }

    /**
     * Récupère la clé secrète pour la signature du token.
     * Décode la clé BASE64 configurée.
     * @return La clé secrète.
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY); // Utilise Decoders de io.jsonwebtoken.io
        return Keys.hmacShaKeyFor(keyBytes); // Utilise Keys de io.jsonwebtoken.security
    }
}