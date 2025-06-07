package bibliotheque.GestionDesLivres.service;

import bibliotheque.GestionDesLivres.entites.ExemplaireLivre;
import bibliotheque.GestionDesLivres.entites.Livre;
import bibliotheque.GestionDesLivres.repository.ExemplaireLivreRepository;
import bibliotheque.GestionDesLivres.repository.LivreRepository;
import bibliotheque.GestionDesLivres.service.ExemplaireLivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ExemplaireLivreServiceImpl implements ExemplaireLivreService {

    @Autowired
    private ExemplaireLivreRepository exemplaireLivreRepository;
    
    @Autowired
    private LivreRepository livreRepository;

    @Override
    public List<ExemplaireLivre> ajouterExemplaires(Long livreId, int nombreExemplaires) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }
        
        if (nombreExemplaires <= 0) {
            throw new IllegalArgumentException("Le nombre d'exemplaires doit être supérieur à 0");
        }

        Optional<Livre> livre = livreRepository.findById(livreId);
        if (livre.isEmpty()) {
            throw new RuntimeException("Livre avec l'ID " + livreId + " introuvable");
        }

        List<ExemplaireLivre> nouveauxExemplaires = new ArrayList<>();
        for (int i = 0; i < nombreExemplaires; i++) {
            ExemplaireLivre exemplaire = new ExemplaireLivre(true, livre.get());
            nouveauxExemplaires.add(exemplaireLivreRepository.save(exemplaire));
        }

        return nouveauxExemplaires;
    }

    @Override
    public void supprimerExemplaire(Long exemplaireId) {
        if (exemplaireId == null) {
            throw new IllegalArgumentException("L'ID de l'exemplaire ne peut pas être null");
        }

        Optional<ExemplaireLivre> exemplaire = exemplaireLivreRepository.findById(exemplaireId);
        if (exemplaire.isEmpty()) {
            throw new RuntimeException("Exemplaire avec l'ID " + exemplaireId + " introuvable");
        }

        if (!exemplaire.get().isDisponible()) {
            throw new RuntimeException("Impossible de supprimer un exemplaire emprunté");
        }

        exemplaireLivreRepository.deleteById(exemplaireId);
    }

    @Override
    public ExemplaireLivre emprunterExemplaire(Long exemplaireId) {
        if (exemplaireId == null) {
            throw new IllegalArgumentException("L'ID de l'exemplaire ne peut pas être null");
        }

        Optional<ExemplaireLivre> exemplaire = exemplaireLivreRepository.findById(exemplaireId);
        if (exemplaire.isEmpty()) {
            throw new RuntimeException("Exemplaire avec l'ID " + exemplaireId + " introuvable");
        }

        ExemplaireLivre exemplaireAEmprunter = exemplaire.get();
        if (!exemplaireAEmprunter.isDisponible()) {
            throw new RuntimeException("Cet exemplaire est déjà emprunté");
        }

        exemplaireAEmprunter.setDisponible(false);
        return exemplaireLivreRepository.save(exemplaireAEmprunter);
    }

    @Override
    public ExemplaireLivre retournerExemplaire(Long exemplaireId) {
        if (exemplaireId == null) {
            throw new IllegalArgumentException("L'ID de l'exemplaire ne peut pas être null");
        }

        Optional<ExemplaireLivre> exemplaire = exemplaireLivreRepository.findById(exemplaireId);
        if (exemplaire.isEmpty()) {
            throw new RuntimeException("Exemplaire avec l'ID " + exemplaireId + " introuvable");
        }

        ExemplaireLivre exemplaireARetourner = exemplaire.get();
        if (exemplaireARetourner.isDisponible()) {
            throw new RuntimeException("Cet exemplaire n'était pas emprunté");
        }

        exemplaireARetourner.setDisponible(true);
        return exemplaireLivreRepository.save(exemplaireARetourner);
    }

    @Override
    public Optional<ExemplaireLivre> emprunterPremierExemplaireDisponible(Long livreId) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }

        Optional<Livre> livre = livreRepository.findById(livreId);
        if (livre.isEmpty()) {
            throw new RuntimeException("Livre avec l'ID " + livreId + " introuvable");
        }

        List<ExemplaireLivre> exemplairesDisponibles = 
            exemplaireLivreRepository.findByLivreAndDisponibleTrue(livre.get());

        if (exemplairesDisponibles.isEmpty()) {
            return Optional.empty(); // Aucun exemplaire disponible
        }

        // Emprunter le premier exemplaire disponible
        ExemplaireLivre premierExemplaire = exemplairesDisponibles.get(0);
        premierExemplaire.setDisponible(false);
        return Optional.of(exemplaireLivreRepository.save(premierExemplaire));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExemplaireLivre> obtenirExemplairesParLivre(Long livreId) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }

        Optional<Livre> livre = livreRepository.findById(livreId);
        if (livre.isEmpty()) {
            throw new RuntimeException("Livre avec l'ID " + livreId + " introuvable");
        }

        return exemplaireLivreRepository.findByLivre(livre.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExemplaireLivre> obtenirExemplairesDisponibles(Long livreId) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }

        Optional<Livre> livre = livreRepository.findById(livreId);
        if (livre.isEmpty()) {
            throw new RuntimeException("Livre avec l'ID " + livreId + " introuvable");
        }

        return exemplaireLivreRepository.findByLivreAndDisponibleTrue(livre.get());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ExemplaireLivre> obtenirExemplairesEmpruntes(Long livreId) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }

        Optional<Livre> livre = livreRepository.findById(livreId);
        if (livre.isEmpty()) {
            throw new RuntimeException("Livre avec l'ID " + livreId + " introuvable");
        }

        return exemplaireLivreRepository.findByLivreAndDisponibleFalse(livre.get());
    }

    @Override
    @Transactional(readOnly = true)
    public long compterExemplairesParLivre(Long livreId) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }
        return exemplaireLivreRepository.countByLivreId(livreId);
    }

    @Override
    @Transactional(readOnly = true)
    public long compterExemplairesDisponibles(Long livreId) {
        if (livreId == null) {
            throw new IllegalArgumentException("L'ID du livre ne peut pas être null");
        }
        return exemplaireLivreRepository.countByLivreIdAndDisponibleTrue(livreId);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean exemplaireDisponible(Long exemplaireId) {
        if (exemplaireId == null) {
            return false;
        }

        Optional<ExemplaireLivre> exemplaire = exemplaireLivreRepository.findById(exemplaireId);
        return exemplaire.isPresent() && exemplaire.get().isDisponible();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<ExemplaireLivre> obtenirExemplaireParId(Long exemplaireId) {
        if (exemplaireId == null) {
            throw new IllegalArgumentException("L'ID de l'exemplaire ne peut pas être null");
        }
        return exemplaireLivreRepository.findById(exemplaireId);
    }
}
