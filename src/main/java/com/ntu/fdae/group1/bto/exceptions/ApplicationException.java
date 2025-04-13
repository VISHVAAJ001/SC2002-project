package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown for errors related to BTO housing applications.
 * <p>
 * This exception is used to indicate problems that occur during application
 * submission,
 * processing, or status changes. It encapsulates specific application-related
 * errors
 * that may occur during the application lifecycle.
 * </p>
 * 
 * Common scenarios where this exception might be thrown include:
 * <ul>
 * <li>Invalid application data</li>
 * <li>Ineligible applicant attempting to apply</li>
 * <li>Invalid status transitions</li>
 * <li>Application processing failures</li>
 * </ul>
 * 
 */
public class ApplicationException extends Exception {
    /**
     * Constructs a new ApplicationException with the specified detail message.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     */
    public ApplicationException(String message) {
        super(message);
    }

    /**
     * Constructs a new ApplicationException with the specified detail message and
     * cause.
     *
     * @param message the detail message (which is saved for later retrieval
     *                by the {@link #getMessage()} method)
     * @param cause   the cause (which is saved for later retrieval by the
     *                {@link #getCause()} method). A {@code null} value is
     *                permitted,
     *                and indicates that the cause is nonexistent or unknown.
     */
    public ApplicationException(String message, Throwable cause) {
        super(message, cause);
    }
}
