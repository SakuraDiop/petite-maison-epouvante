package com.pme.epouvante.controller;

import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.service.AnnonceService;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/annonces")
public class AnnonceController {

    private final AnnonceService service;

    public AnnonceController(AnnonceService service) {
        this.service = service;
    }

    @GetMapping
    public List<Annonce> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Annonce getById(@PathVariable Long id) {
        return service.getById(id);
    }

    @PostMapping
    public Annonce create(@Valid @RequestBody Annonce annonce, Authentication auth) {
        return service.create(annonce, auth.getName());
    }

    @PutMapping("/{id}")
    public Annonce update(@PathVariable Long id,
                          @Valid @RequestBody Annonce annonce,
                          Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        return service.update(id, annonce, auth.getName(), isAdmin);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id, Authentication auth) {

        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        service.delete(id, auth.getName(), isAdmin);
    }
}