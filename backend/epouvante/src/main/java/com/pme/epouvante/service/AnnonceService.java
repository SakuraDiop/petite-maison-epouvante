package com.pme.epouvante.service;

import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.exception.ForbiddenException;
import com.pme.epouvante.exception.ResourceNotFoundException;
import com.pme.epouvante.repository.AnnonceRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class AnnonceService {

    private final AnnonceRepository repository;

    public AnnonceService(AnnonceRepository repository) {
        this.repository = repository;
    }

    public List<Annonce> getAll() {
        return repository.findAll();
    }

    public Annonce getById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));
    }

    public Annonce create(Annonce annonce, String username) {

        if (username.equals("admin")) {
            throw new ForbiddenException("L'admin ne peut pas créer d'annonces");
        }

        annonce.setProprietaire(username);
        annonce.setStatut("ACTIVE");
        annonce.setDateCreation(LocalDateTime.now());

        return repository.save(annonce);
    }

    public Annonce update(Long id, Annonce updated, String username, boolean isAdmin) {

        Annonce annonce = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));

        if (!isAdmin && !annonce.getProprietaire().equals(username)) {
            throw new ForbiddenException("Vous n'avez pas le droit de modifier cette annonce");
        }

        annonce.setTitre(updated.getTitre());
        annonce.setDescription(updated.getDescription());
        annonce.setType(updated.getType());
        annonce.setImageUrl(updated.getImageUrl());
        annonce.setContact(updated.getContact());

        return repository.save(annonce);
    }

    public void delete(Long id, String username, boolean isAdmin) {

        Annonce annonce = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce non trouvée"));

        if (!isAdmin && !annonce.getProprietaire().equals(username)) {
            throw new ForbiddenException("Vous n'avez pas le droit de supprimer cette annonce");
        }

        repository.deleteById(id);
    }
}