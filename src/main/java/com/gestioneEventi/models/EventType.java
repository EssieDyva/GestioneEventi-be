package com.gestioneEventi.models;

/**
 * Enumeration representing different types of events in the system.
 * Each event type defines the category and behavior of events.
 *
 */
public enum EventType {

    /**
     * Vacation or leave event type.
     * Used for employee vacation requests and time off.
     */
    FERIE,

    /**
     * Team building event type.
     * Used for team building activities and workshops.
     */
    TEAM_BUILDING,

    /**
     * Generic event type.
     * Used for general company events and activities.
     */
    GENERICO
}