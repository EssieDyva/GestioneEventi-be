package com.gestioneEventi.exceptions;

/**
 * Eccezione lanciata quando un utente non ha i permessi necessari
 * per eseguire un'operazione specifica
 */
public class InsufficientPermissionException extends RuntimeException {
    
    public InsufficientPermissionException(String message) {
        super(message);
    }
    
    public InsufficientPermissionException(String resource, String action) {
        super(String.format("Permessi insufficienti per %s su: %s", action, resource));
    }
}