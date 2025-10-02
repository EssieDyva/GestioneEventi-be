package com.gestioneEventi.exceptions;

/**
 * Eccezione lanciata quando si verifica un conflitto con lo stato attuale
 * Es: risorsa già esistente, vincoli di unicità violati, operazione incompatibile
 */
public class ConflictException extends RuntimeException {
    
    public ConflictException(String message) {
        super(message);
    }
    
    public ConflictException(String resourceName, String field, Object value) {
        super(String.format("Conflitto: %s con %s '%s' già esistente", resourceName, field, value));
    }
}