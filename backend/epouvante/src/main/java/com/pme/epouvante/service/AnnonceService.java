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
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable avec l'id : " + id));
    }

    public Annonce create(Annonce annonce, String username) {
        annonce.setDateCreation(LocalDateTime.now());
        annonce.setStatut("ACTIVE");
        annonce.setProprietaire(username);
        return repository.save(annonce);
    }

    public Annonce update(Long id, Annonce nouvelleAnnonce, String username, boolean isAdmin) {

        Annonce annonce = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable avec l'id : " + id));

        if (!isAdmin && !annonce.getProprietaire().equals(username)) {
            throw new ForbiddenException("Vous n'avez pas le droit de modifier cette annonce");
        }

        annonce.setTitre(nouvelleAnnonce.getTitre());
        annonce.setDescription(nouvelleAnnonce.getDescription());
        annonce.setType(nouvelleAnnonce.getType());

        return repository.save(annonce);
    }

    public void delete(Long id, String username, boolean isAdmin) {

        Annonce annonce = repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Annonce introuvable avec l'id : " + id));

        if (!isAdmin && !annonce.getProprietaire().equals(username)) {
            throw new ForbiddenException("Vous n'avez pas le droit de supprimer cette annonce");
        }

        repository.delete(annonce);
    }
}