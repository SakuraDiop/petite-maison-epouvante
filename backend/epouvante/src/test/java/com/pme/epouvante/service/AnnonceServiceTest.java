package com.pme.epouvante.service;

import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.exception.ForbiddenException;
import com.pme.epouvante.exception.ResourceNotFoundException;
import com.pme.epouvante.repository.AnnonceRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AnnonceServiceTest {

    private AnnonceRepository annonceRepository;
    private AnnonceService annonceService;

    @BeforeEach
    void setUp() {
        annonceRepository = Mockito.mock(AnnonceRepository.class);
        annonceService = new AnnonceService(annonceRepository);
    }

    @Test
    void doitRetournerToutesLesAnnonces() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Lampe ancienne");

        when(annonceRepository.findAll()).thenReturn(List.of(annonce));

        List<Annonce> result = annonceService.getAll();

        assertEquals(1, result.size());
        assertEquals("Lampe ancienne", result.get(0).getTitre());
    }

    @Test
    void userPeutCreerAnnonce() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Poupée ancienne");
        annonce.setDescription("Une poupée ancienne de collection");
        annonce.setType("DON");

        when(annonceRepository.save(any(Annonce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Annonce saved = annonceService.create(annonce, "user1");

        assertEquals("Poupée ancienne", saved.getTitre());
        assertEquals("user1", saved.getProprietaire());
        assertEquals("ACTIVE", saved.getStatut());
        assertNotNull(saved.getDateCreation());
    }

    @Test
    void adminNePeutPasCreerAnnonce() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce admin");
        annonce.setDescription("Description");
        annonce.setType("DON");

        assertThrows(
                ForbiddenException.class,
                () -> annonceService.create(annonce, "admin")
        );

        verify(annonceRepository, never()).save(any(Annonce.class));
    }

    @Test
    void userPeutModifierSaPropreAnnonce() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Ancien titre");
        annonce.setDescription("Ancienne description");
        annonce.setType("DON");
        annonce.setProprietaire("user1");

        Annonce updated = new Annonce();
        updated.setTitre("Titre modifié");
        updated.setDescription("Description modifiée");
        updated.setType("ECHANGE");
        updated.setImageUrl("/uploads/image.jpg");
        updated.setContact("user1@test.com");

        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));
        when(annonceRepository.save(any(Annonce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Annonce result = annonceService.update(1L, updated, "user1", false);

        assertEquals("Titre modifié", result.getTitre());
        assertEquals("Description modifiée", result.getDescription());
        assertEquals("ECHANGE", result.getType());
        assertEquals("/uploads/image.jpg", result.getImageUrl());
        assertEquals("user1@test.com", result.getContact());

        verify(annonceRepository).save(annonce);
    }

    @Test
    void userNePeutPasModifierAnnonceAutreUser() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce user2");
        annonce.setProprietaire("user2");

        Annonce updated = new Annonce();
        updated.setTitre("Tentative modification");

        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        assertThrows(
                ForbiddenException.class,
                () -> annonceService.update(1L, updated, "user1", false)
        );

        verify(annonceRepository, never()).save(any(Annonce.class));
    }

    @Test
    void adminPeutModifierAnnonceDeToutLeMonde() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce user2");
        annonce.setDescription("Description");
        annonce.setType("DON");
        annonce.setProprietaire("user2");

        Annonce updated = new Annonce();
        updated.setTitre("Annonce modifiée par admin");
        updated.setDescription("Nouvelle description");
        updated.setType("ECHANGE");

        when(annonceRepository.findById(2L)).thenReturn(Optional.of(annonce));
        when(annonceRepository.save(any(Annonce.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Annonce result = annonceService.update(2L, updated, "admin", true);

        assertEquals("Annonce modifiée par admin", result.getTitre());
        assertEquals("Nouvelle description", result.getDescription());
        assertEquals("ECHANGE", result.getType());

        verify(annonceRepository).save(annonce);
    }

    @Test
    void userPeutSupprimerSaPropreAnnonce() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce user1");
        annonce.setProprietaire("user1");

        when(annonceRepository.findById(1L)).thenReturn(Optional.of(annonce));

        annonceService.delete(1L, "user1", false);

        verify(annonceRepository).deleteById(1L);
    }

    @Test
    void userNePeutPasSupprimerAnnonceAutreUser() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce user2");
        annonce.setProprietaire("user2");

        when(annonceRepository.findById(2L)).thenReturn(Optional.of(annonce));

        assertThrows(
                ForbiddenException.class,
                () -> annonceService.delete(2L, "user1", false)
        );

        verify(annonceRepository, never()).deleteById(anyLong());
    }

    @Test
    void adminPeutSupprimerAnnonceDeUser2() {
        Annonce annonce = new Annonce();
        annonce.setTitre("Annonce user2");
        annonce.setProprietaire("user2");

        when(annonceRepository.findById(2L)).thenReturn(Optional.of(annonce));

        annonceService.delete(2L, "admin", true);

        verify(annonceRepository).deleteById(2L);
    }

    @Test
    void supprimerAnnonceInexistanteDoitLeverErreur() {
        when(annonceRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(
                ResourceNotFoundException.class,
                () -> annonceService.delete(99L, "admin", true)
        );

        verify(annonceRepository, never()).deleteById(anyLong());
    }
}