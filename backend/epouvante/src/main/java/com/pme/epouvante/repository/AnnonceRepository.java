package com.pme.epouvante.repository;

import com.pme.epouvante.entity.Annonce;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AnnonceRepository extends JpaRepository<Annonce, Long> {
}