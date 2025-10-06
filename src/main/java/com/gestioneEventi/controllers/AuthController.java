package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.UserRepository;
import com.gestioneEventi.services.EmployeeDirectoryClient;
import com.gestioneEventi.services.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
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

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody TokenRequest request) {
        try {
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(request.getIdToken());
            String email = decoded.getEmail();

            if (email == null || !directoryClient.isEmployee(email)) {
                return ResponseEntity.status(403).body("Access denied: not in employee directory");
            }

            User user = userRepository.findByEmail(email)
                    .orElseGet(() -> {
                        User newUser = new User();
                        newUser.setEmail(email);
                        newUser.setName(decoded.getName());
                        newUser.setRole(Role.USER);
                        return userRepository.save(newUser);
                    });

            String jwt = jwtService.generateToken(user);
            ResponseCookie cookie = ResponseCookie.from("access_token", jwt)
                    .httpOnly(true)
                    .secure(false)
                    .sameSite("Strict")
                    .path("/")
                    .maxAge(86400)
                    .build();

            return ResponseEntity.ok()
                    .header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(new UserDTO(user));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Token Firebase non valido: " + e.getMessage());
        }
    }

    @GetMapping("/me")
    public ResponseEntity<?> getCurrentUser(@AuthenticationPrincipal UserDetails user) {
        if (user == null)
            return ResponseEntity.status(401).build();
        return ResponseEntity.ok(new UserDTO((User) user));
    }

    public static class TokenRequest {
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    public static class TokenResponse {
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}