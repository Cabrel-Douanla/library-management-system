package com.biblio.empruntservice.services;


import com.biblio.empruntservice.entities.Emprunt;
import com.biblio.empruntservice.entities.Retour;
import com.biblio.empruntservice.repositories.RetourRepository;
import com.biblio.empruntservice.repositories.EmpruntRepository;
import com.biblio.empruntservice.clients.LivreClient;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class RetourService {
    private final EmpruntRepository empruntRepository;
    private final LivreClient livreClient;

public RetourService(RetourRepository retourRepository,
                     EmpruntRepository empruntRepository,
                     LivreClient livreClient) {
    this.retourRepository = retourRepository;
    this.empruntRepository = empruntRepository;
    this.livreClient = livreClient;
} 
    private final RetourRepository retourRepository;

    public RetourService(RetourRepository retourRepository) {
        this.retourRepository = retourRepository;
    }

    public List<Retour> getAll() {
        return retourRepository.findAll();
    }

    public Retour create(Retour retour) {
    // 1. Récupérer l'emprunt par ID
    Emprunt emprunt = empruntRepository.findById(retour.getEmpruntId())
            .orElseThrow(() -> new RuntimeException("Emprunt introuvable"));

    // 2. Mettre à jour les infos de retour
    emprunt.setDateRetour(retour.getDateRetour());
    emprunt.setRendu(true);
    empruntRepository.save(emprunt);

    // 3. Rendre l'exemplaire disponible
    livreClient.marquerExemplaireDisponible(emprunt.getExemplaireId());

    // 4. Déterminer si le retour est en retard
    retour.setEnRetard(retour.getDateRetour().isAfter(emprunt.getDateRetourPrevue()));

    return retourRepository.save(retour);
}

}
