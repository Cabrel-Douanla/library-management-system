package bibliotheque.GestionDesLivres.entites;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "livres")
@Data
public class Livre {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titre;
    private String auteur;
    private String edition;

    public Livre(){

    }

    public Livre(Long id, String titre, String auteur, String edition){
        super();
        this.id = id;
        this.titre = titre;
        this.auteur = auteur;
        this.edition = edition;

    }

    public Long getId() { 
        return id; 
    }

    public void setId(Long id) { 
        this.id = id; 
    }

    public String getTitre() { 
        return titre; 
    }

    public void setTitre(String titre) { 
        this.titre = titre; 
    }

    public String getAuteur() { 
        return auteur; 
    }

    public void setAuteur(String auteur) { 
        this.auteur = auteur; 
    }

    public String getEdition() { 
        return edition; 
    }

    public void setEdition(String edition) {
        this.edition = edition;
    }
}

