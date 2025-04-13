package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown when user input validation fails.
 * <p>
 * This exception indicates that input provided by a user or passed between
 * system
 * components fails validation criteria. Common validation issues include:
 * - Missing required fields
 * - Values outside of acceptable ranges
 * - Malformed data (e.g., improperly formatted NRIC)
 * - Logically inconsistent combinations of values
 * </p>
 * <p>
 * InvalidInputException helps separate input validation concerns from business
 * logic
 * exceptions, allowing the UI layer to handle and display validation errors
 * appropriately.
 * </p>
 */
public class InvalidInputException extends Exception {

    /**
     * Constructs a new InvalidInputException with a detailed message.
     * 
     * @param message The detail message explaining the validation error
     */
    public InvalidInputException(String message) {
        super(message);
    }

    /**
     * Constructs a new InvalidInputException with a detailed message and cause.
     * 
     * @param message The detail message explaining the validation error
     * @param cause   The underlying cause of this exception
     */
    public InvalidInputException(String message, Throwable cause) {
        super(message, cause);
    }
}
