package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown when there are issues accessing or manipulating data in
 * repositories.
 * <p>
 * This exception serves as a wrapper for various data-related errors that might
 * occur
 * during repository operations, such as:
 * - File I/O errors when reading from or writing to persistent storage
 * - Data format errors when parsing stored data
 * - Consistency errors when data violates integrity constraints
 * - Network errors when accessing remote data sources
 * </p>
 * <p>
 * By using this exception consistently across repositories, the application can
 * handle data access errors in a uniform way in higher layers.
 * </p>
 */
public class DataAccessException extends RuntimeException {

    /**
     * Constructs a new DataAccessException with a detailed message.
     * 
     * @param message The detail message explaining the error
     */
    public DataAccessException(String message) {
        super(message);
    }

    /**
     * Constructs a new DataAccessException with a detailed message and cause.
     * <p>
     * This constructor is useful when wrapping lower-level exceptions
     * that occurred during data access operations.
     * </p>
     * 
     * @param message The detail message explaining the error
     * @param cause   The underlying cause of this exception
     */
    public DataAccessException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new DataAccessException with a cause.
     * <p>
     * This constructor is useful when rethrowing an existing exception
     * that doesn't fit the standard exception hierarchy.
     * </p>
     * 
     * @param cause The underlying cause of this exception
     */
    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
