package bibliotheque.GestionDesLivres.entites;

import jakarta.persistence.*;

@Entity
@Table(name = "exemplaireLivre")
public class ExemplaireLivre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean disponible = true;

    @ManyToOne
    @JoinColumn(name = "livre_id") // correspondance avec la clé étrangère
    private Livre livre;


    public ExemplaireLivre() {
    }


    public ExemplaireLivre(boolean disponible, Livre livre) {
        this.disponible = disponible;
        this.livre = livre;
    }


    public ExemplaireLivre(Long id, boolean disponible, Livre livre) {
        this.id = id;
        this.disponible = disponible;
        this.livre = livre;
    }


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public boolean isDisponible() {
        return disponible;
    }

    public void setDisponible(boolean disponible) {
        this.disponible = disponible;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }
}

