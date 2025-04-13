package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown for errors related to user authentication in the BTO system.
 * <p>
 * This exception is used to indicate problems that occur during the login
 * process,
 * credential verification, or session management. It encapsulates
 * security-related
 * errors that may affect a user's ability to access the system.
 * </p>
 * 
 * Common scenarios where this exception might be thrown include:
 * <ul>
 * <li>Invalid username or password</li>
 * </ul>
 * 
 */
public class AuthenticationException extends Exception {
    /**
     * Constructs a new AuthenticationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public AuthenticationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthenticationException with the specified detail message
     * and cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). A {@code null} value is
     *                permitted,
     *                and indicates that the cause is nonexistent or unknown.
     */
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
