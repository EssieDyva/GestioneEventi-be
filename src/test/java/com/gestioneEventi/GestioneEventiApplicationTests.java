package com.gestioneEventi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import io.github.cdimascio.dotenv.Dotenv;

@SpringBootTest
class GestioneEventiApplicationTests {

	static {
		Dotenv dotenv = Dotenv.configure().ignoreIfMissing().load();
		String jwtSecret = dotenv.get("JWT_SECRET");
		System.setProperty("JWT_SECRET", jwtSecret);
	}

	@Test
	void contextLoads() {
	}

}
