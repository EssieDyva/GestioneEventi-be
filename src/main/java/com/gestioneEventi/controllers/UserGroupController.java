package com.gestioneEventi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestioneEventi.dto.CreateGroupRequest;
import com.gestioneEventi.dto.UserGroupDTO;
import com.gestioneEventi.models.UserGroup;
import com.gestioneEventi.services.UserGroupService;

@RestController
@RequestMapping("/api/groups")
public class UserGroupController {

    @Autowired
    private UserGroupService userGroupService;

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<UserGroupDTO>> getAllGroups() {
        List<UserGroup> groups = userGroupService.getAllGroups();
        List<UserGroupDTO> dtoList = groups.stream()
                .map(UserGroupDTO::new)
                .toList();
        return ResponseEntity.ok(dtoList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<?> getGroup(@PathVariable Long id) {
        try {
            UserGroup group = userGroupService.getGroupById(id);
            return ResponseEntity.ok(group);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<?> createGroup(@RequestBody CreateGroupRequest request) {
        try {
            UserGroup group = userGroupService.createGroup(
                    request.getGroupName(),
                    request.getMemberEmails());
            return ResponseEntity.ok(group);
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(ex.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<?> deleteGroup(@PathVariable Long id) {
        try {
            userGroupService.deleteGroup(id);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException ex) {
            return ResponseEntity.notFound().build();
        }
    }
}