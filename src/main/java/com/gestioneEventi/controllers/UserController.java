package com.gestioneEventi.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gestioneEventi.dto.UpdateUserRoleRequest;
import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.models.User;
import com.gestioneEventi.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    @PreAuthorize("hasAuthority('EDITOR') or hasAuthority('ADMIN')")
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> usersDTO = userService.getAllUsersWithGroups()
                .stream()
                .map(UserDTO::new)
                .toList();
        return ResponseEntity.ok(usersDTO);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long id,
            @RequestBody UpdateUserRoleRequest request) {
        try {
            User updatedUser = userService.updateUserRole(id, request.getRole());
            return ResponseEntity.ok(new UserDTO(updatedUser));
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }
}