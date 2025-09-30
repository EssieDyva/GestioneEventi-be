package com.gestioneEventi.controllers;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestioneEventi.dto.CreateGroupRequest;
import com.gestioneEventi.models.User;
import com.gestioneEventi.models.UserGroup;
import com.gestioneEventi.repositories.UserGroupRepository;
import com.gestioneEventi.repositories.UserRepository;

@RestController
@RequestMapping("/api/groups")
public class UserGroupController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserGroupRepository userGroupRepository;

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest request) {
        UserGroup group = new UserGroup();
        group.setName(request.getGroupName());

        // Aggiungo i membri in base alle email
        Set<User> members = request.getMemberEmails().stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new RuntimeException("Utente non trovato: " + email)))
                .collect(Collectors.toSet());

        group.setMembers(members);
        userGroupRepository.save(group);

        for (User user : members) {
            user.setGroup(group);
            userRepository.save(user);
        }

        return ResponseEntity.ok("Gruppo creato con successo");
    }
}