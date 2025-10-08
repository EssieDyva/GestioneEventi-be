package com.gestioneEventi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.UserGroup;

@Repository
public interface UserGroupRepository extends JpaRepository<UserGroup, Long> {
    @Override
    @Query("SELECT g FROM UserGroup g WHERE g.id = :id")
    Optional<UserGroup> findById(Long id);

    @Query("""
            SELECT g FROM UserGroup g
            JOIN g.members u
            WHERE u.id=:userId
            """)

    List<UserGroup> findAllByUserId(@Param("userId") Long userId);
}