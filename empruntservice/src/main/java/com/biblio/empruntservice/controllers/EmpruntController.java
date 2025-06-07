package com.biblio.empruntservice.controllers;

//package com.biblio.empruntservice.controllers;

import com.biblio.empruntservice.entities.Emprunt;
import com.biblio.empruntservice.services.EmpruntService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/emprunts")
public class EmpruntController {

    @Autowired
    private EmpruntService empruntService;

    @GetMapping
    public List<Emprunt> getAll() {
        return empruntService.findAll();
    }

    @GetMapping("/{id}")
    public Optional<Emprunt> getById(@PathVariable Long id) {
        return empruntService.findById(id);
    }

    // @PostMapping
    // public Emprunt create(@RequestBody Emprunt emprunt) {
    //     return empruntService.save(emprunt);
    // }

    @PostMapping
    public ResponseEntity<Emprunt> creerEmprunt(@RequestParam Long utilisateurId, @RequestParam Long livreId) {
    Emprunt emprunt = empruntService.creerEmprunt(utilisateurId, livreId);
    return ResponseEntity.status(HttpStatus.CREATED).body(emprunt);
}


    @PutMapping("/{id}")
    public Emprunt update(@PathVariable Long id, @RequestBody Emprunt updatedEmprunt) {
        updatedEmprunt.setId(id);
        return empruntService.save(updatedEmprunt);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        empruntService.deleteById(id);
    }

    
}

