package com.gestioneEventi.services;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.gestioneEventi.exceptions.BusinessValidationException;
import com.gestioneEventi.exceptions.ResourceNotFoundException;
import com.gestioneEventi.models.User;
import com.gestioneEventi.models.UserGroup;
import com.gestioneEventi.repositories.UserGroupRepository;
import com.gestioneEventi.repositories.UserRepository;

@Service
public class UserGroupService {

    @Autowired
    private UserGroupRepository userGroupRepository;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public UserGroup createGroup(String groupName, List<String> memberEmails) {
        validateGroupCreationRequest(groupName, memberEmails);

        // Carica tutti gli utenti
        List<User> foundUsers = userRepository.findAllByEmailIn(memberEmails);

        // Verifica che tutti gli utenti esistano
        if (foundUsers.size() != memberEmails.size()) {
            Set<String> foundEmails = foundUsers.stream()
                    .map(User::getEmail)
                    .collect(Collectors.toSet());

            List<String> missingEmails = memberEmails.stream()
                    .filter(email -> !foundEmails.contains(email))
                    .toList();

            throw new ResourceNotFoundException(
                    "Utenti non trovati: " + String.join(", ", missingEmails));
        }

        // Crea il gruppo
        UserGroup group = new UserGroup();
        group.setName(groupName);
        group = userGroupRepository.save(group);

        // Associa gli utenti al gruppo
        final UserGroup savedGroup = group;
        foundUsers.forEach(user -> user.getGroups().add(savedGroup));

        userRepository.saveAll(foundUsers);

        group.setMembers(new HashSet<>(foundUsers));
        return group;
    }

    private void validateGroupCreationRequest(String groupName, List<String> memberEmails) {
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new BusinessValidationException("Il nome del gruppo è obbligatorio");
        }
        if (memberEmails == null || memberEmails.isEmpty()) {
            throw new BusinessValidationException("Il gruppo deve avere almeno un membro");
        }
        if (memberEmails.stream().anyMatch(email -> email == null || email.trim().isEmpty())) {
            throw new BusinessValidationException("Email non valide nella lista");
        }
    }

    public List<UserGroup> getAllGroups() {
        return userGroupRepository.findAll();
    }

    public UserGroup getGroupById(Long id) {
        return userGroupRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Gruppo non trovato"));
    }

    @Transactional
    public void deleteGroup(Long id) {
        UserGroup group = getGroupById(id);

        // Rimuovi l'associazione degli utenti al gruppo
        for (User user : group.getMembers()) {
            user.getGroups().remove(group);
        }
        userRepository.saveAll(group.getMembers());

        userGroupRepository.deleteById(id);
    }
}