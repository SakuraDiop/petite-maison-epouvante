package com.pme.epouvante.config;

import com.pme.epouvante.entity.Annonce;
import com.pme.epouvante.repository.AnnonceRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(AnnonceRepository repository) {
        return args -> {
            if (repository.count() == 0) {
                Annonce annonce1 = new Annonce();
                annonce1.setTitre("Poupée ancienne");
                annonce1.setDescription("Une poupée ancienne de collection, parfaite pour les amateurs d’objets étranges.");
                annonce1.setType("DON");
                annonce1.setStatut("ACTIVE");
                annonce1.setProprietaire("user1");
                annonce1.setContact("user1@petitemaisonepouvante.local");
                annonce1.setImageUrl("/images/poupee.jpg");
                repository.save(annonce1);

                Annonce annonce2 = new Annonce();
                annonce2.setTitre("Lampe mystérieuse");
                annonce2.setDescription("Vieille lampe au style gothique proposée en échange contre un objet de collection.");
                annonce2.setType("ECHANGE");
                annonce2.setStatut("ACTIVE");
                annonce2.setProprietaire("user2");
                annonce2.setContact("user2@petitemaisonepouvante.local");
                annonce2.setImageUrl("/images/lampe.jpg");
                repository.save(annonce2);
            }
        };
    }
}