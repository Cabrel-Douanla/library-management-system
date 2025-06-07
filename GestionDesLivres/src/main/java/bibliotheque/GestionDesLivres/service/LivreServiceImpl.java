package bibliotheque.GestionDesLivres.service;

import bibliotheque.GestionDesLivres.entites.Livre;
import bibliotheque.GestionDesLivres.entites.ExemplaireLivre;
import bibliotheque.GestionDesLivres.repository.LivreRepository;
import bibliotheque.GestionDesLivres.repository.ExemplaireLivreRepository;
import bibliotheque.GestionDesLivres.service.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class LivreServiceImpl implements LivreService {

    @Autowired
    private LivreRepository livreRepository;
    
    @Autowired
    private ExemplaireLivreRepository exemplaireLivreRepository;

    @Override
    public Livre ajouterLivre(Livre livre, int nombreExemplaires) {
        if (livre == null) {
            throw new IllegalArgumentException("Le livre ne peut pas être null");
        }
        
        if (nombreExemplaires <= 0) {
            throw new IllegalArgumentException("Le nombre d'exemplaires doit être supérieur à 0");
        }
        
        if (livre.getTitre() == null || livre.getTitre().trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre du livre est obligatoire");
        }
        
        if (livre.getAuteur() == null || livre.getAuteur().trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur du livre est obligatoire");
        }
        
        // Sauvegarder d'abord le livre
        Livre livreSauvegarde = livreRepository.save(livre);
        
        // Créer les exemplaires
        for (int i = 0; i < nombreExemplaires; i++) {
            ExemplaireLivre exemplaire = new ExemplaireLivre(true, livreSauvegarde);
            exemplaireLivreRepository.save(exemplaire);
        }
        
        return livreSauvegarde;
    }

    @Override
    public Livre ajouterLivre(Livre livre) {
        // Par défaut, créer 1 exemplaire
        return ajouterLivre(livre, 1);
    }

    @Override
    public void supprimerLivre(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        
        if (!livreRepository.existsById(id)) {
            throw new RuntimeException("Livre avec l'ID " + id + " introuvable");
        }
        
        livreRepository.deleteById(id);
    }

    @Override
    public Livre modifierLivre(Long id, Livre livre) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        
        if (livre == null) {
            throw new IllegalArgumentException("Le livre ne peut pas être null");
        }

        Optional<Livre> livreExistant = livreRepository.findById(id);
        if (livreExistant.isEmpty()) {
            throw new RuntimeException("Livre avec l'ID " + id + " introuvable");
        }

        Livre livreAModifier = livreExistant.get();
        
        // Mise à jour des champs
        if (livre.getTitre() != null && !livre.getTitre().trim().isEmpty()) {
            livreAModifier.setTitre(livre.getTitre());
        }
        
        if (livre.getAuteur() != null && !livre.getAuteur().trim().isEmpty()) {
            livreAModifier.setAuteur(livre.getAuteur());
        }
        
        if (livre.getEdition() != null) {
            livreAModifier.setEdition(livre.getEdition());
        }

        return livreRepository.save(livreAModifier);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livre> obtenirTousLesLivres() {
        return livreRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Livre> obtenirLivreParId(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("L'ID ne peut pas être null");
        }
        return livreRepository.findById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livre> rechercherParTitre(String titre) {
        if (titre == null || titre.trim().isEmpty()) {
            throw new IllegalArgumentException("Le titre de recherche ne peut pas être vide");
        }
        return livreRepository.findByTitreContainingIgnoreCase(titre.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livre> rechercherParAuteur(String auteur) {
        if (auteur == null || auteur.trim().isEmpty()) {
            throw new IllegalArgumentException("L'auteur de recherche ne peut pas être vide");
        }
        return livreRepository.findByAuteurContainingIgnoreCase(auteur.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<Livre> rechercherParTitreEtAuteur(String titre, String auteur) {
        if ((titre == null || titre.trim().isEmpty()) && 
            (auteur == null || auteur.trim().isEmpty())) {
            throw new IllegalArgumentException("Au moins un critère de recherche est requis");
        }
        
        if (titre != null && !titre.trim().isEmpty() && 
            auteur != null && !auteur.trim().isEmpty()) {
            return livreRepository.findByTitreContainingIgnoreCaseAndAuteurContainingIgnoreCase(
                titre.trim(), auteur.trim());
        } else if (titre != null && !titre.trim().isEmpty()) {
            return rechercherParTitre(titre);
        } else {
            return rechercherParAuteur(auteur);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public boolean livreExiste(Long id) {
        if (id == null) {
            return false;
        }
        return livreRepository.existsById(id);
    }
}