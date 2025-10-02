package com.gestioneEventi.exceptions;

/**
 * Eccezione lanciata per errori di validazione delle regole di business
 * Es: date invalide, stato non valido, vincoli di business non rispettati
 */
public class BusinessValidationException extends RuntimeException {
    
    public BusinessValidationException(String message) {
        super(message);
    }
    
    public BusinessValidationException(String message, Throwable cause) {
        super(message, cause);
    }
}