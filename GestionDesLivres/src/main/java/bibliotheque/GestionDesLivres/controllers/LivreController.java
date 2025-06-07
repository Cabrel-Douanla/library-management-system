package bibliotheque.GestionDesLivres.controllers;

import bibliotheque.GestionDesLivres.entites.Livre;
import bibliotheque.GestionDesLivres.service.LivreService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/livres")
public class LivreController {

    @Autowired
    private LivreService livreService;

    // Afficher tous les livres
    @GetMapping
    public String afficherTousLesLivres(Model model) {
        List<Livre> livres = livreService.obtenirTousLesLivres();
        model.addAttribute("livres", livres);
        return "exemplaires/liste"; // templates/livres/liste.html
    }

    // Afficher le formulaire d'ajout
    @GetMapping("/nouveau")
    public String afficherFormulaireAjout(Model model) {
        model.addAttribute("livre", new Livre());
        return "livres/formulaire"; // templates/livres/formulaire.html
    }

    // Traiter l'ajout d'un livre
    @PostMapping("/ajouter")
    public String ajouterLivre(@ModelAttribute Livre livre, 
                              @RequestParam(defaultValue = "1") int nombreExemplaires,
                              RedirectAttributes redirectAttributes) {
        try {
            livreService.ajouterLivre(livre, nombreExemplaires);
            redirectAttributes.addFlashAttribute("message", 
                "Livre ajouté avec succès avec " + nombreExemplaires + " exemplaire(s)!");
            redirectAttributes.addFlashAttribute("typeMessage", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                "Erreur lors de l'ajout : " + e.getMessage());
            redirectAttributes.addFlashAttribute("typeMessage", "error");
        }
        return "redirect:/livres";
    }

    // Afficher les détails d'un livre
    @GetMapping("/{id}")
    public String afficherDetailsLivre(@PathVariable Long id, Model model) {
        Optional<Livre> livre = livreService.obtenirLivreParId(id);
        if (livre.isPresent()) {
            model.addAttribute("livre", livre.get());
            return "livres/details"; // templates/livres/details.html
        }
        return "redirect:/livres";
    }

    // Afficher le formulaire de modification
    @GetMapping("/{id}/modifier")
    public String afficherFormulaireModification(@PathVariable Long id, Model model) {
        Optional<Livre> livre = livreService.obtenirLivreParId(id);
        if (livre.isPresent()) {
            model.addAttribute("livre", livre.get());
            return "livres/formulaire-modification"; // templates/livres/formulaire-modification.html
        }
        return "redirect:/livres";
    }

    // Traiter la modification
    @PostMapping("/{id}/modifier")
    public String modifierLivre(@PathVariable Long id, 
                               @ModelAttribute Livre livre,
                               RedirectAttributes redirectAttributes) {
        try {
            livreService.modifierLivre(id, livre);
            redirectAttributes.addFlashAttribute("message", "Livre modifié avec succès!");
            redirectAttributes.addFlashAttribute("typeMessage", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                "Erreur lors de la modification : " + e.getMessage());
            redirectAttributes.addFlashAttribute("typeMessage", "error");
        }
        return "redirect:/livres";
    }

    // Supprimer un livre
    @PostMapping("/{id}/supprimer")
    public String supprimerLivre(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            livreService.supprimerLivre(id);
            redirectAttributes.addFlashAttribute("message", "Livre supprimé avec succès!");
            redirectAttributes.addFlashAttribute("typeMessage", "success");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("message", 
                "Erreur lors de la suppression : " + e.getMessage());
            redirectAttributes.addFlashAttribute("typeMessage", "error");
        }
        return "redirect:/livres";
    }

    // Rechercher des livres
    @GetMapping("/rechercher")
    public String rechercherLivres(@RequestParam(required = false) String titre,
                                  @RequestParam(required = false) String auteur,
                                  Model model) {
        List<Livre> livres;
        
        if ((titre == null || titre.trim().isEmpty()) && 
            (auteur == null || auteur.trim().isEmpty())) {
            livres = livreService.obtenirTousLesLivres();
        } else {
            livres = livreService.rechercherParTitreEtAuteur(titre, auteur);
        }
        
        model.addAttribute("livres", livres);
        model.addAttribute("titre", titre);
        model.addAttribute("auteur", auteur);
        return "livres/recherche"; // templates/livres/recherche.html
    }
}