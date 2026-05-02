package com.pme.epouvante.service;

import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.exception.ForbiddenException;
import com.pme.epouvante.repository.AnnonceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnnonceServiceTest {

    private AnnonceRepository repository;
    private AnnonceService service;

    @BeforeEach
    void setUp() {
        repository = mock(AnnonceRepository.class);
        service = new AnnonceService(repository);
    }

    @Test
    void user1PeutModifierSaPropreAnnonce() {

        Annonce annonce = new Annonce();
        annonce.setProprietaire("user1");
        annonce.setTitre("Ancien");

        when(repository.findById(1L)).thenReturn(Optional.of(annonce));
        when(repository.save(any())).thenReturn(annonce);

        Annonce modif = new Annonce();
        modif.setTitre("Nouveau");
        modif.setDescription("Desc");
        modif.setType("DON");

        Annonce resultat = service.update(1L, modif, "user1", false);

        assertEquals("Nouveau", resultat.getTitre());
    }

    @Test
    void user1NePeutPasModifierAnnonceDeUser2() {

        Annonce annonce = new Annonce();
        annonce.setProprietaire("user2");

        when(repository.findById(1L)).thenReturn(Optional.of(annonce));

        Annonce modif = new Annonce();

        assertThrows(
                ForbiddenException.class,
                () -> service.update(1L, modif, "user1", false)
        );
    }

    @Test
    void user2NePeutPasModifierAnnonceDeUser1() {

        Annonce annonce = new Annonce();
        annonce.setProprietaire("user1");

        when(repository.findById(1L)).thenReturn(Optional.of(annonce));

        Annonce modif = new Annonce();

        assertThrows(
                ForbiddenException.class,
                () -> service.update(1L, modif, "user2", false)
        );
    }

    @Test
    void adminPeutModifierAnnonceDeUser1() {

        Annonce annonce = new Annonce();
        annonce.setProprietaire("user1");
        annonce.setTitre("Titre initial");

        when(repository.findById(1L)).thenReturn(Optional.of(annonce));
        when(repository.save(any())).thenReturn(annonce);

        Annonce modif = new Annonce();
        modif.setTitre("Titre admin");
        modif.setDescription("Desc");
        modif.setType("DON");

        Annonce resultat = service.update(1L, modif, "admin", true);

        assertEquals("Titre admin", resultat.getTitre());
    }

    @Test
    void adminPeutSupprimerAnnonceDeUser2() {

        Annonce annonce = new Annonce();
        annonce.setProprietaire("user2");

        when(repository.findById(2L)).thenReturn(Optional.of(annonce));

        service.delete(2L, "admin", true);

        verify(repository).delete(annonce);
    }
}