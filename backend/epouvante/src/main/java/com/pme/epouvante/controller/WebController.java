package com.pme.epouvante.controller;

import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.service.AnnonceService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

@Controller
public class WebController {

    private final AnnonceService service;

    public WebController(AnnonceService service) {
        this.service = service;
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/web/annonces")
    public String annonces(Model model, Authentication auth) {
        model.addAttribute("annonces", service.getAll());
        addUserInfo(model, auth);
        return "annonces";
    }

    @GetMapping("/web/annonces/{id}")
    public String detail(@PathVariable Long id, Model model, Authentication auth) {
        model.addAttribute("annonce", service.getById(id));
        addUserInfo(model, auth);
        return "detail";
    }

    @GetMapping("/web/create")
    public String createForm(Model model, Authentication auth) {
        if (auth == null || isAdmin(auth)) {
            return "redirect:/web/annonces";
        }

        model.addAttribute("annonce", new Annonce());
        return "create";
    }

    @PostMapping("/web/create")
    public String createAnnonce(@ModelAttribute Annonce annonce,
                                @RequestParam("imageFile") MultipartFile imageFile,
                                Authentication auth) throws IOException {

        if (auth == null || isAdmin(auth)) {
            return "redirect:/web/annonces";
        }

        if (!imageFile.isEmpty()) {
            annonce.setImageUrl(saveImage(imageFile));
        }

        service.create(annonce, auth.getName());
        return "redirect:/web/annonces";
    }

    @GetMapping("/web/edit/{id}")
    public String editForm(@PathVariable Long id, Model model, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        Annonce annonce = service.getById(id);

        if (!isAdmin(auth) && !annonce.getProprietaire().equals(auth.getName())) {
            return "redirect:/web/annonces/" + id;
        }

        model.addAttribute("annonce", annonce);
        return "edit";
    }

    @PostMapping("/web/edit/{id}")
    public String editAnnonce(@PathVariable Long id,
                              @ModelAttribute Annonce annonce,
                              @RequestParam("imageFile") MultipartFile imageFile,
                              Authentication auth) throws IOException {

        if (auth == null) {
            return "redirect:/login";
        }

        Annonce existing = service.getById(id);

        if (!imageFile.isEmpty()) {
            annonce.setImageUrl(saveImage(imageFile));
        } else {
            annonce.setImageUrl(existing.getImageUrl());
        }

        service.update(id, annonce, auth.getName(), isAdmin(auth));
        return "redirect:/web/annonces/" + id;
    }

    @PostMapping("/web/delete/{id}")
    public String delete(@PathVariable Long id, Authentication auth) {
        if (auth == null) {
            return "redirect:/login";
        }

        service.delete(id, auth.getName(), isAdmin(auth));
        return "redirect:/web/annonces";
    }

    private String saveImage(MultipartFile imageFile) throws IOException {
        Path uploadDir = Paths.get("uploads").toAbsolutePath().normalize();
        Files.createDirectories(uploadDir);

        String filename = UUID.randomUUID() + "-" + imageFile.getOriginalFilename();
        Path filePath = uploadDir.resolve(filename);

        imageFile.transferTo(filePath.toFile());

        return "/uploads/" + filename;
    }

    private void addUserInfo(Model model, Authentication auth) {
        if (auth != null) {
            model.addAttribute("username", auth.getName());
            model.addAttribute("isAdmin", isAdmin(auth));
        } else {
            model.addAttribute("username", null);
            model.addAttribute("isAdmin", false);
        }
    }

    private boolean isAdmin(Authentication auth) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));
    }
}