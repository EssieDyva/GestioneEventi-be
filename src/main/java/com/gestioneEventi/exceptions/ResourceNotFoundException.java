package com.gestioneEventi.exceptions;

/**
 * Eccezione lanciata quando una risorsa non viene trovata
 */
public class ResourceNotFoundException extends RuntimeException {
    
    public ResourceNotFoundException(String message) {
        super(message);
    }
    
    public ResourceNotFoundException(String resourceName, Long id) {
        super(String.format("%s non trovato con id: %d", resourceName, id));
    }
    
    public ResourceNotFoundException(String resourceName, String identifier, Object value) {
        super(String.format("%s non trovato con %s: %s", resourceName, identifier, value));
    }
}