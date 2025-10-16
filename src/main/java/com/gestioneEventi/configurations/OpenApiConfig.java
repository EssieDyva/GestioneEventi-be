package com.gestioneEventi.configurations;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
    info = @Info(
        title = "Gestione Eventi API",
        version = "1.0",
        description = "API per la gestione di eventi, ferie, gruppi e utenti con autenticazione Firebase",
        contact = @Contact(
            name = "Team Gestione Eventi",
            email = "support@gestioneeventi.com"
        )
    ),
    servers = {
        @Server(
            description = "Local Server",
            url = "http://localhost:8080"
        )
    },
    security = @SecurityRequirement(name = "bearerAuth")
)
@SecurityScheme(
    name = "bearerAuth",
    type = SecuritySchemeType.HTTP,
    scheme = "bearer",
    bearerFormat = "JWT",
    description = "Inserisci il token JWT ottenuto dall'autenticazione Firebase"
)
public class OpenApiConfig {
}