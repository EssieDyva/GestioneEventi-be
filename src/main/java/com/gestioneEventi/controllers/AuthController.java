package com.gestioneEventi.controllers;

import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.UserRepository;
import com.gestioneEventi.services.EmployeeDirectoryClient;
import com.gestioneEventi.services.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    @Autowired
    private EmployeeDirectoryClient directoryClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/firebase")
    public ResponseEntity<?> authenticate(@RequestBody TokenRequest request) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String email = decoded.getEmail();

            if (email == null || !directoryClient.isEmployee(email)) {
                return ResponseEntity.status(403).body("Access denied: not in employee directory");
            }

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new RuntimeException("User not found in DB"));

            String jwt = jwtService.generateToken(user);
            return ResponseEntity.ok(new TokenResponse(jwt));

        } catch (Exception e) {
            return ResponseEntity.status(401).body("Invalid token");
        }
    }

    public static class TokenRequest {
        private String idToken;
        public String getIdToken() { return idToken; }
        public void setIdToken(String idToken) { this.idToken = idToken; }
    }

    public static class TokenResponse {
        private String token;
        public TokenResponse(String token) { this.token = token; }
        public String getToken() { return token; }
    }
}