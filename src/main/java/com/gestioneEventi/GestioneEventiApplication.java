package com.gestioneEventi;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootApplication
public class GestioneEventiApplication {

	public static void main(String[] args) {
		// Carica .env solo se esiste
		try {
			Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
			String jwtSecret = dotenv.get("JWT_SECRET");
			if (jwtSecret != null) {
				System.setProperty("JWT_SECRET", jwtSecret);
			}
		} catch (Exception e) {
			System.out.println("⚠️ Nessun file .env trovato, usando configurazioni di default");
		}

		SpringApplication.run(GestioneEventiApplication.class, args);
	}
}