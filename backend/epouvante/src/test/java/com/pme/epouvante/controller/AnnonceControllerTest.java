package com.pme.epouvante.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.service.AnnonceService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AnnonceController.class)
@AutoConfigureMockMvc(addFilters = false)
class AnnonceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AnnonceService annonceService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void doitRetournerToutesLesAnnonces() throws Exception {
        Annonce annonce = new Annonce();
        annonce.setTitre("Lampe hantée");
        annonce.setDescription("Lampe ancienne proposée au don");
        annonce.setType("DON");
        annonce.setStatut("ACTIVE");
        annonce.setProprietaire("user1");
        annonce.setDateCreation(LocalDateTime.now());

        Mockito.when(annonceService.getAll()).thenReturn(List.of(annonce));

        mockMvc.perform(get("/annonces"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].titre").value("Lampe hantée"))
                .andExpect(jsonPath("$[0].type").value("DON"))
                .andExpect(jsonPath("$[0].statut").value("ACTIVE"))
                .andExpect(jsonPath("$[0].proprietaire").value("user1"));
    }

    @Test
    void doitRetournerUneAnnonceParId() throws Exception {
        Annonce annonce = new Annonce();
        annonce.setTitre("Poupée maudite");
        annonce.setDescription("Objet de collection");
        annonce.setType("ECHANGE");
        annonce.setStatut("ACTIVE");
        annonce.setProprietaire("user2");

        Mockito.when(annonceService.getById(1L)).thenReturn(annonce);

        mockMvc.perform(get("/annonces/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Poupée maudite"))
                .andExpect(jsonPath("$.type").value("ECHANGE"))
                .andExpect(jsonPath("$.proprietaire").value("user2"));
    }

    @Test
    void userAuthentifiePeutCreerAnnonce() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user1",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Annonce input = new Annonce();
        input.setTitre("Blu-ray horreur");
        input.setDescription("Blu-ray collector à donner");
        input.setType("DON");
        input.setContact("user1@test.com");

        Annonce saved = new Annonce();
        saved.setTitre("Blu-ray horreur");
        saved.setDescription("Blu-ray collector à donner");
        saved.setType("DON");
        saved.setStatut("ACTIVE");
        saved.setProprietaire("user1");
        saved.setContact("user1@test.com");

        Mockito.when(annonceService.create(any(Annonce.class), eq("user1")))
                .thenReturn(saved);

        mockMvc.perform(post("/annonces")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Blu-ray horreur"))
                .andExpect(jsonPath("$.statut").value("ACTIVE"))
                .andExpect(jsonPath("$.proprietaire").value("user1"));
    }

    @Test
    void userAuthentifiePeutModifierAnnonce() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "user1",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_USER"))
        );

        Annonce input = new Annonce();
        input.setTitre("Titre modifié");
        input.setDescription("Description modifiée");
        input.setType("ECHANGE");
        input.setContact("user1@test.com");

        Annonce updated = new Annonce();
        updated.setTitre("Titre modifié");
        updated.setDescription("Description modifiée");
        updated.setType("ECHANGE");
        updated.setStatut("ACTIVE");
        updated.setProprietaire("user1");
        updated.setContact("user1@test.com");

        Mockito.when(annonceService.update(eq(1L), any(Annonce.class), eq("user1"), eq(false)))
                .thenReturn(updated);

        mockMvc.perform(put("/annonces/1")
                        .principal(auth)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(input)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.titre").value("Titre modifié"))
                .andExpect(jsonPath("$.type").value("ECHANGE"))
                .andExpect(jsonPath("$.proprietaire").value("user1"));
    }

    @Test
    void adminPeutSupprimerAnnonce() throws Exception {
        Authentication auth = new UsernamePasswordAuthenticationToken(
                "admin",
                "password",
                List.of(new SimpleGrantedAuthority("ROLE_ADMIN"))
        );

        mockMvc.perform(delete("/annonces/1")
                        .principal(auth))
                .andExpect(status().isOk());

        Mockito.verify(annonceService).delete(1L, "admin", true);
    }
}