package com.gestioneEventi.services;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.UserRepository;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;

    @Transactional
    public Optional<User> updateUserRole(Long id, Role role) {
        return userRepository.findById(id).map(user -> {
            user.setRole(role);
            return userRepository.save(user);
        });
    }
}