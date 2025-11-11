package com.gestioneEventi.models;

/**
 * Enumeration representing user roles in the system.
 * Each role defines different levels of permissions and access rights.
 *
 */
public enum Role {

    /**
     * Administrator role with full system access.
     * Can manage users, events, and system settings.
     */
    ADMIN,

    /**
     * Editor role with content management permissions.
     * Can create and modify events and activities.
     */
    EDITOR,

    /**
     * Standard user role with basic access.
     * Can view events and participate in activities.
     */
    USER
}