package com.gestioneEventi.services;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;

@Service
public class EmployeeDirectoryClient {

    private static final Logger log = LoggerFactory.getLogger(EmployeeDirectoryClient.class);

    private final WebClient webClient;
    private final Cache<String, Boolean> emailCache;
    private final boolean fallbackAllow;

    public EmployeeDirectoryClient(
            @Value("${app.employee-directory.url}") String baseUrl,
            @Value("${app.employee-directory.fallback-allow-if-unreachable}") boolean fallbackAllow) {
        this.webClient = WebClient.builder().baseUrl(baseUrl).build();
        this.emailCache = Caffeine.newBuilder()
                .expireAfterWrite(Duration.ofMinutes(10))
                .maximumSize(10_000)
                .build();
        this.fallbackAllow = fallbackAllow;
    }

    public boolean isEmployee(String email) {
        Boolean cached = emailCache.getIfPresent(email.toLowerCase());
        if (cached != null) {
            return cached;
        }

        try {
            // supponiamo endpoint GET /employees?email={email} ritorni 200 con body
            // {exists:true}
            Boolean exists = webClient.get()
                    .uri(uriBuilder -> uriBuilder.path("/employees").queryParam("email", email).build())
                    .retrieve()
                    .bodyToMono(EmployeeExistResponse.class)
                    .map(EmployeeExistResponse::isExists)
                    .block(Duration.ofSeconds(5));

            boolean allowed = Boolean.TRUE.equals(exists);
            emailCache.put(email.toLowerCase(), allowed);
            return allowed;
        } catch (Exception ex) {
            log.warn("Employee directory unreachable, email={} fallbackAllow={}", email, fallbackAllow, ex);
            // se fallbackAllow=true permetti l'accesso temporaneamente, altrimenti deny
            return fallbackAllow;
        }
    }

    private static class EmployeeExistResponse {
        private boolean exists;

        public boolean isExists() {
            return exists;
        }
    }
}
