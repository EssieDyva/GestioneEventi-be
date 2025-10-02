package com.gestioneEventi.services;

import java.util.List;

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

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional
    public User updateUserRole(Long id, Role roleDetail) {
        User user = userRepository.findById(id)
        .orElseThrow(() -> new IllegalArgumentException("Utente non trovato"));
        user.setRole(roleDetail);
        return userRepository.save(user);
    }
}