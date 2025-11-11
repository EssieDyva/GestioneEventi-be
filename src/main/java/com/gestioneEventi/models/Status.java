package com.gestioneEventi.models;

/**
 * Enumeration representing approval statuses for requests.
 * Used primarily for vacation requests and other approval-based workflows.
 *
 */
public enum Status {

    /**
     * Request has been approved.
     * The requested action or time off has been granted.
     */
    APPROVED,

    /**
     * Request has been rejected.
     * The requested action or time off has been denied.
     */
    REJECTED
}