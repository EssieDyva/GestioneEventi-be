package com.gestioneEventi.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.gestioneEventi.models.User;

/**
 * Repository interface for User entity operations.
 * Provides standard CRUD operations and custom queries for user management.
 *
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Finds a user by their email address.
     *
     * @param email The email address to search for
     * @return Optional containing the user if found, empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Finds all users with the specified email addresses, including their groups.
     * Uses LEFT JOIN FETCH to eagerly load user groups.
     *
     * @param emails List of email addresses to search for
     * @return List of users matching the emails, with groups loaded
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.groups WHERE u.email IN :emails")
    List<User> findAllByEmailInWithGroups(@Param("emails") List<String> emails);

    /**
     * Finds all users with their associated groups.
     * Uses LEFT JOIN FETCH to eagerly load user groups for all users.
     *
     * @return List of all users with their groups loaded
     */
    @Query("SELECT DISTINCT u FROM User u LEFT JOIN FETCH u.groups")
    List<User> findAllWithGroups();
}