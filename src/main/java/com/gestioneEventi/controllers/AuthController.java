package com.gestioneEventi.controllers;

import com.gestioneEventi.dto.AuthResponse;
import com.gestioneEventi.dto.UserDTO;
import com.gestioneEventi.models.Role;
import com.gestioneEventi.models.User;
import com.gestioneEventi.repositories.UserRepository;
import com.gestioneEventi.services.EmployeeDirectoryClient;
import com.gestioneEventi.services.JwtService;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@Tag(name = "Autenticazione", description = "API per l'autenticazione tramite Firebase")
public class AuthController {

    @Autowired
    private EmployeeDirectoryClient directoryClient;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtService jwtService;

    @PostMapping("/firebase")
    @Operation(
        summary = "Autenticazione Firebase",
        description = "Autentica l'utente tramite token Firebase e restituisce un JWT per le API"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200", 
            description = "Autenticazione riuscita",
            content = @Content(schema = @Schema(implementation = AuthResponse.class))
        ),
        @ApiResponse(responseCode = "401", description = "Token Firebase non valido", content = @Content),
        @ApiResponse(responseCode = "403", description = "Accesso negato: utente non nel directory aziendale", content = @Content)
    })
    public ResponseEntity<?> authenticate(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                description = "Token ID Firebase",
                required = true,
                content = @Content(schema = @Schema(implementation = TokenRequest.class))
            )
            @RequestBody TokenRequest request) {
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
            return ResponseEntity.ok(new AuthResponse(jwt, new UserDTO(user)));

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(401).body("Token Firebase non valido: " + e.getMessage());
        }
    }

    @Schema(description = "Richiesta di autenticazione con token Firebase")
    public static class TokenRequest {
        @Schema(description = "Token ID ottenuto da Firebase Authentication", example = "eyJhbGciOiJSUzI1NiIsImtpZCI6...")
        private String idToken;

        public String getIdToken() {
            return idToken;
        }

        public void setIdToken(String idToken) {
            this.idToken = idToken;
        }
    }

    @Schema(description = "Risposta con token JWT")
    public static class TokenResponse {
        @Schema(description = "Token JWT per le API", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
        private String token;

        public TokenResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }
    }
}