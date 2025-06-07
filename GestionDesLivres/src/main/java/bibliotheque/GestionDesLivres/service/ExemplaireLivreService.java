package bibliotheque.GestionDesLivres.service;

import bibliotheque.GestionDesLivres.entites.ExemplaireLivre;
import bibliotheque.GestionDesLivres.entites.Livre;
import java.util.List;
import java.util.Optional;

public interface ExemplaireLivreService {
    
    // Ajouter des exemplaires à un livre existant
    List<ExemplaireLivre> ajouterExemplaires(Long livreId, int nombreExemplaires);
    
    // Supprimer un exemplaire (si disponible)
    void supprimerExemplaire(Long exemplaireId);
    
    // Marquer un exemplaire comme emprunté
    ExemplaireLivre emprunterExemplaire(Long exemplaireId);
    
    // Marquer un exemplaire comme retourné
    ExemplaireLivre retournerExemplaire(Long exemplaireId);
    
    // Emprunter le premier exemplaire disponible d'un livre
    Optional<ExemplaireLivre> emprunterPremierExemplaireDisponible(Long livreId);
    
    // Obtenir tous les exemplaires d'un livre
    List<ExemplaireLivre> obtenirExemplairesParLivre(Long livreId);
    
    // Obtenir les exemplaires disponibles d'un livre
    List<ExemplaireLivre> obtenirExemplairesDisponibles(Long livreId);
    
    // Obtenir les exemplaires empruntés d'un livre
    List<ExemplaireLivre> obtenirExemplairesEmpruntes(Long livreId);
    
    // Compter le nombre total d'exemplaires d'un livre
    long compterExemplairesParLivre(Long livreId);
    
    // Compter les exemplaires disponibles d'un livre
    long compterExemplairesDisponibles(Long livreId);
    
    // Vérifier si un exemplaire existe et est disponible
    boolean exemplaireDisponible(Long exemplaireId);
    
    // Obtenir un exemplaire par ID
    Optional<ExemplaireLivre> obtenirExemplaireParId(Long exemplaireId);
}