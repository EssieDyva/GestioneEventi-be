package com.gestioneEventi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.groups WHERE u.email IN :emails")
    List<User> findAllByEmailInWithGroups(@Param("emails") List<String> emails);

    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.groups")
    List<User> findAllWithGroups();

}