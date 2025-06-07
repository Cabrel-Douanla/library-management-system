package bibliotheque.GestionDesLivres.controllers;

import bibliotheque.GestionDesLivres.entites.ExemplaireLivre;
import bibliotheque.GestionDesLivres.repository.ExemplaireLivreRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class ExemplaireController {

    @Autowired
    private ExemplaireLivreRepository exemplaireRepository;

    // ✅ Trouver un exemplaire disponible pour un livre donné
    @GetMapping("/livres/{livreId}/exemplaire-disponible")
    public ExemplaireLivre getExemplaireDisponible(@PathVariable Long livreId) {
        return exemplaireRepository
            .findFirstByLivre_IdAndDisponibleTrue(livreId)
            .orElseThrow(() -> new RuntimeException("Aucun exemplaire disponible pour ce livre."));
    }

    // ✅ Réserver un exemplaire (le rendre indisponible)
    @PutMapping("/exemplaires/{id}/reserver")
    public ExemplaireLivre reserverExemplaire(@PathVariable Long id) {
        ExemplaireLivre exemplaire = exemplaireRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        exemplaire.setDisponible(false);
        return exemplaireRepository.save(exemplaire);
    }

    // ✅ Libérer un exemplaire (le rendre disponible à nouveau)
    @PutMapping("/exemplaires/{id}/liberer")
    public ExemplaireLivre libererExemplaire(@PathVariable Long id) {
        ExemplaireLivre exemplaire = exemplaireRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Exemplaire non trouvé"));
        exemplaire.setDisponible(true);
        return exemplaireRepository.save(exemplaire);
    }
}
