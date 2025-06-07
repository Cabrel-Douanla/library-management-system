package bibliotheque.GestionDesLivres.repository;

import bibliotheque.GestionDesLivres.entites.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LivreRepository extends JpaRepository<Livre, Long> {
    
    // Recherche par titre (insensible à la casse)
    List<Livre> findByTitreContainingIgnoreCase(String titre);
    
    // Recherche par auteur (insensible à la casse)
    List<Livre> findByAuteurContainingIgnoreCase(String auteur);
    
    // Recherche par titre ET auteur (insensible à la casse)
    List<Livre> findByTitreContainingIgnoreCaseAndAuteurContainingIgnoreCase(
        String titre, String auteur);
}


