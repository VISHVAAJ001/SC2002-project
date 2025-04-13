package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown when a user attempts to access a resource or perform an
 * action
 * for which they do not have proper authorization.
 * 
 * This exception is typically thrown during security checks when:
 * <ul>
 * <li>A user attempts to access a restricted feature</li>
 * <li>A user with insufficient privileges attempts to modify protected
 * data</li>
 * </ul>
 * 
 * <p>
 * Handlers for this exception should typically redirect users to an appropriate
 * error page or prompt for authentication as needed.
 * </p>
 */
public class AuthorizationException extends Exception {
    /**
     * Constructs a new AuthorizationException with the specified detail message.
     *
     * @param message The detail message explaining the reason for the exception
     */
    public AuthorizationException(String message) {
        super(message);
    }

    /**
     * Constructs a new AuthorizationException with the specified detail message and
     * cause.
     *
     * @param message The detail message explaining the reason for the exception
     * @param cause   The cause of the exception (a null value is permitted if the
     *                cause is nonexistent or unknown)
     */
    public AuthorizationException(String message, Throwable cause) {
        super(message, cause);
    }
}
