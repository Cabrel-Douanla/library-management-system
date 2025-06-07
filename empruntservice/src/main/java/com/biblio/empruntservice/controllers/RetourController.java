package com.biblio.empruntservice.controllers;


import com.biblio.empruntservice.entities.Retour;
import com.biblio.empruntservice.services.RetourService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/retours")
public class RetourController {

    private final RetourService retourService;

    public RetourController(RetourService retourService) {
        this.retourService = retourService;
    }

    @GetMapping
    public List<Retour> getAll() {
        return retourService.getAll();
    }

    @PostMapping
    public Retour create(@RequestBody Retour retour) {
        return retourService.create(retour);
    }
}
