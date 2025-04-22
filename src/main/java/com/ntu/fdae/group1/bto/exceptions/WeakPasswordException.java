package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown when a password does not meet the required strength criteria.
 */
public class WeakPasswordException extends Exception {
    public WeakPasswordException(String message) {
        super(message);
    }
}
