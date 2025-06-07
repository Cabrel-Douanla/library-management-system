package com.biblio.empruntservice.services;

import com.biblio.empruntservice.clients.LivreClient;
import com.biblio.empruntservice.dto.ExemplaireDTO;

//package com.biblio.empruntservice.services;

import com.biblio.empruntservice.entities.Emprunt;
import com.biblio.empruntservice.repositories.EmpruntRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class EmpruntService {

    @Autowired
    private EmpruntRepository empruntRepository;

    public List<Emprunt> findAll() {
        return empruntRepository.findAll();
    }

    public Optional<Emprunt> findById(Long id) {
        return empruntRepository.findById(id);
    }

    public Emprunt save(Emprunt emprunt) {
        return empruntRepository.save(emprunt);
    }

    public void deleteById(Long id) {
        empruntRepository.deleteById(id);
    }

        @Autowired
    private LivreClient livreClient;

//     public Emprunt creerEmprunt(Long utilisateurId, Long livreId) {
//     // 1. Appeler livreservice pour vérifier disponibilité
//     ExemplaireDTO exemplaire = livreClient.getExemplaireDisponible(livreId);
//     if (exemplaire == null) throw new RuntimeException("Aucun exemplaire disponible");

//     // 2. Enregistrer l’emprunt
//     Emprunt emprunt = new Emprunt();
//     emprunt.setUtilisateurId(utilisateurId);
//     emprunt.setLivreId(livreId);
//     emprunt.setExemplaireId(exemplaire.getId());
//     emprunt.setDateEmprunt(LocalDate.now());
//     emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));
//     emprunt.setRetourne(false);

//     empruntRepository.save(emprunt);

//     // 3. Marquer exemplaire comme non disponible
//     livreClient.reserverExemplaire(exemplaire.getId());

//     return emprunt;
// }


public Emprunt creerEmprunt(Long utilisateurId, Long livreId) {
    // Vérifier que l'utilisateur n’a pas déjà emprunté ce livre (non retourné)
    Optional<Emprunt> dejaEmprunte = empruntRepository.findByUtilisateurIdAndLivreIdAndRetourneFalse(utilisateurId, livreId);
    if (dejaEmprunte.isPresent()) {
        throw new RuntimeException("L'utilisateur a déjà emprunté ce livre et ne l’a pas encore retourné.");
    }

    // 1. Vérifier disponibilité via LivreClient
    ExemplaireDTO exemplaire = livreClient.getExemplaireDisponible(livreId);
    if (exemplaire == null) {
        throw new RuntimeException("Aucun exemplaire disponible pour ce livre.");
    }

    // 2. Enregistrer l’emprunt
    Emprunt emprunt = new Emprunt();
    emprunt.setUtilisateurId(utilisateurId);
    emprunt.setLivreId(livreId);
    emprunt.setExemplaireId(exemplaire.getId());
    emprunt.setDateEmprunt(LocalDate.now());
    emprunt.setDateRetourPrevue(LocalDate.now().plusDays(14));
    emprunt.setRetourne(false);

    empruntRepository.save(emprunt);

    // 3. Réserver l’exemplaire (marquer comme non disponible)
    livreClient.reserverExemplaire(exemplaire.getId());

    return emprunt;
}




}

