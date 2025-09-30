package com.gestioneEventi.services;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        if (groupName == null || groupName.trim().isEmpty()) {
            throw new IllegalArgumentException("Il nome del gruppo è obbligatorio");
        }

        if (memberEmails == null || memberEmails.isEmpty()) {
            throw new IllegalArgumentException("Il gruppo deve avere almeno un membro");
        }

        // Crea il gruppo
        UserGroup group = new UserGroup();
        group.setName(groupName);

        // Salva prima il gruppo per ottenere l'ID
        group = userGroupRepository.save(group);

        // Trova gli utenti e associali al gruppo
        Set<User> members = findUsersByEmails(memberEmails);

        // Associa ogni utente al gruppo
        // NOTA: questo rimuoverà l'utente dal gruppo precedente se era già in uno
        for (User user : members) {
            user.setGroup(group);
        }
        userRepository.saveAll(members);

        // Aggiorna il gruppo con i membri
        group.setMembers(members);

        return userGroupRepository.save(group);
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
            user.setGroup(null);
        }
        userRepository.saveAll(group.getMembers());

        userGroupRepository.deleteById(id);
    }

    private Set<User> findUsersByEmails(List<String> emails) {
        return emails.stream()
                .map(email -> userRepository.findByEmail(email)
                        .orElseThrow(() -> new IllegalArgumentException("Utente non trovato: " + email)))
                .collect(Collectors.toSet());
    }
}