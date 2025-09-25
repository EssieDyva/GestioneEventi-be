package com.gestioneEventi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestioneEventi.models.User;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}