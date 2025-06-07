package bibliotheque.GestionDesLivres.service;

import bibliotheque.GestionDesLivres.entites.Livre;
import java.util.List;
import java.util.Optional;

public interface LivreService {
    
    // Ajouter un livre avec nombre d'exemplaires
    Livre ajouterLivre(Livre livre, int nombreExemplaires);
    
    // Ajouter un livre (1 exemplaire par défaut)
    Livre ajouterLivre(Livre livre);
    
    // Supprimer un livre par ID
    void supprimerLivre(Long id);
    
    // Mettre à jour un livre
    Livre modifierLivre(Long id, Livre livre);
    
    // Afficher tous les livres
    List<Livre> obtenirTousLesLivres();
    
    // Trouver un livre par ID
    Optional<Livre> obtenirLivreParId(Long id);
    
    // Rechercher par titre
    List<Livre> rechercherParTitre(String titre);
    
    // Rechercher par auteur
    List<Livre> rechercherParAuteur(String auteur);
    
    // Rechercher par titre ET auteur
    List<Livre> rechercherParTitreEtAuteur(String titre, String auteur);
    
    // Vérifier si un livre existe
    boolean livreExiste(Long id);
}
