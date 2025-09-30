package com.gestioneEventi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;

import com.gestioneEventi.models.UserGroup;

public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    
}