package com.gestioneEventi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.Ferie;

import java.util.List;


@Repository
public interface FerieRepository extends JpaRepository<Ferie, Long> {
    List<Ferie> findByCreatedByEmail(String email);
}