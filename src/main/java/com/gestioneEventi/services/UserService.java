package com.gestioneEventi.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.UserRepository;

/**
 * Service class for managing user-related operations.
 * Provides methods for retrieving users, managing user roles, and handling user-group relationships.
 *
 */
@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    /**
     * Retrieves all users from the database.
     * Note: This method does not fetch user groups for performance reasons.
     *
     * @return List of all users
     */
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    /**
     * Retrieves all users with their associated groups.
     * Uses a custom query to fetch users along with their group memberships.
     *
     * @return List of users with their groups eagerly loaded
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsersWithGroups() {
        return userRepository.findAllWithGroups();
    }

    /**
     * Retrieves users by their email addresses, including group information.
     * Useful for bulk operations where specific users need to be fetched with their groups.
     *
     * @param emails List of email addresses to search for
     * @return List of users matching the provided emails, with groups loaded
     */
    @Transactional(readOnly = true)
    public List<User> getUsersByEmailsWithGroups(List<String> emails) {
        return userRepository.findAllByEmailInWithGroups(emails);
    }

    /**
     * Updates the role of a specific user.
     * This operation requires appropriate permissions and is transactional.
     *
     * @param id The ID of the user to update
     * @param roleDetail The new role to assign to the user
     * @return The updated user entity
     * @throws IllegalArgumentException if the user with the given ID is not found
     */
    @Transactional
    public User updateUserRole(Long id, Role roleDetail) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        user.setRole(roleDetail);
        return userRepository.save(user);
    }
}