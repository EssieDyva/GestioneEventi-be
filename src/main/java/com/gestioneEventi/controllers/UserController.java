package com.gestioneEventi.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import com.gestioneEventi.dto.UpdateUserRoleRequest;
import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.services.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
    
    @Autowired
    private UserService userService;

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('ADMIN')")
    public ResponseEntity<UserDTO> updateUserRole(
            @PathVariable Long id, 
            @RequestBody UpdateUserRoleRequest request) {
        return userService.updateUserRole(id, request.getRole())
                .map(user -> ResponseEntity.ok(new UserDTO(user)))
                .orElse(ResponseEntity.notFound().build());
    }
}