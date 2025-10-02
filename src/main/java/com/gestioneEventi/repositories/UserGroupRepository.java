package com.gestioneEventi.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
}