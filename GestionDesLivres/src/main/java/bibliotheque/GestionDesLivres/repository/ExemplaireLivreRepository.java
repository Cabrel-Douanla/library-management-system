package bibliotheque.GestionDesLivres.repository;

import bibliotheque.GestionDesLivres.entites.ExemplaireLivre;
import bibliotheque.GestionDesLivres.entites.Livre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ExemplaireLivreRepository extends JpaRepository<ExemplaireLivre, Long> {

    List<ExemplaireLivre> findByLivre(Livre livre);

    List<ExemplaireLivre> findByLivreAndDisponibleTrue(Livre livre);

    List<ExemplaireLivre> findByLivreAndDisponibleFalse(Livre livre);

    @Query("SELECT COUNT(e) FROM ExemplaireLivre e WHERE e.livre.id = :livreId")
    long countByLivreId(@Param("livreId") Long livreId);

    @Query("SELECT COUNT(e) FROM ExemplaireLivre e WHERE e.livre.id = :livreId AND e.disponible = true")
    long countByLivreIdAndDisponibleTrue(@Param("livreId") Long livreId);

    // 👉 Méthode manquante ajoutée ici
    Optional<ExemplaireLivre> findFirstByLivre_IdAndDisponibleTrue(Long livreId);
}
