package com.gestioneEventi.repositories;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Override
    @Query("SELECT g FROM UserGroup g WHERE g.id = :id")
    Optional<UserGroup> findById(Long id);
}