package com.ntu.fdae.group1.bto.exceptions;

/**
 * Custom exception class for handling registration-related errors in the BTO
 * system.
 * <p>
 * This exception is thrown when there are issues during user registration
 * processes,
 * such as validation failures, duplicate registrations, or system errors that
 * prevent
 * successful registration.
 * </p>
 */
public class RegistrationException extends Exception {
    /**
     * Constructs a new registration exception with the specified detail message.
     *
     * @param message the detail message describing the exception
     */
    public RegistrationException(String message) {
        super(message);
    }

    /**
     * Constructs a new registration exception with the specified detail message and
     * cause.
     *
     * @param message the detail message describing the exception
     * @param cause   the underlying cause of the exception
     */
    public RegistrationException(String message, Throwable cause) {
        super(message, cause);
    }
}
