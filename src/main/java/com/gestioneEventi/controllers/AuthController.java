package com.gestioneEventi.controllers;

import java.util.Collections;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.RoleRepository;
import com.gestioneEventi.repositories.UserRepository;
import com.gestioneEventi.services.EmployeeDirectoryClient;
import com.gestioneEventi.services.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;


@RestController
@RequestMapping("/auth")
public class AuthController {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final EmployeeDirectoryClient employeeClient;

    public AuthController(JwtService jwtService, UserRepository userRepository,
            RoleRepository roleRepository,
            EmployeeDirectoryClient employeeClient) {
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.employeeClient = employeeClient;
    }

    @PostMapping("/firebase")
    public ResponseEntity<?> authWithFirebase(@RequestBody Map<String, String> body) {
        String idToken = body.get("idToken");
        if (idToken == null)
            return ResponseEntity.badRequest().body("idToken required");

        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(idToken);
            String email = decoded.getEmail();
            if (email == null)
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("no email in token");

            boolean allowed = employeeClient.isEmployee(email);
            if (!allowed) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("email not authorized");
            }

            User user = userRepository.findByEmail(email).orElseGet(() -> {
                User u = new User();
                u.setEmail(email);
                u.setName(decoded.getName());
                u.setOauthProvider("firebase");
                Role defaultRole = roleRepository.findByName("ROLE_EMPLOYEE")
                        .orElseThrow(() -> new IllegalStateException("ROLE_EMPLOYEE missing"));
                u.getRoles().add(defaultRole);
                return u;
            });
            user.setName(decoded.getName());
            userRepository.save(user);

            String appJwt = jwtService.generateToken(user);
            Map<String, String> resp = Collections.singletonMap("token", appJwt);
            return ResponseEntity.ok(resp);

        } catch (FirebaseAuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("invalid firebase token");
        }
    }
}
