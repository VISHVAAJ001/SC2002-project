package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown when a password does not meet the required strength criteria.
 */
public class WeakPasswordException extends RuntimeException {
    /** 
     * Constructs a new WeakPasswordException with the specified detail message.
     * @param message the detail message. 
     */
    public WeakPasswordException(String message) { 
        super(message);
    }
}

