package com.biblio.empruntservice.repositories;

//package com.biblio.empruntservice.repositories;

import com.biblio.empruntservice.entities.Emprunt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;


@Repository
public interface EmpruntRepository extends JpaRepository<Emprunt, Long> {
    // Tu peux ajouter des méthodes personnalisées ici si besoin
    Optional<Emprunt> findByUtilisateurIdAndLivreIdAndRetourneFalse(Long utilisateurId, Long livreId);

}

