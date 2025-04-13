package com.ntu.fdae.group1.bto.exceptions;

/**
 * Exception thrown when errors occur during the booking process.
 * <p>
 * This exception indicates problems specific to booking operations, such as:
 * - Eligibility issues that prevent booking completion
 * - Conflicts with existing bookings
 * - Invalid booking states or transitions
 * - Quota or availability constraints
 * - Timing issues related to booking windows
 * </p>
 * <p>
 * BookingException provides specific error information for the UI layer
 * to present appropriate messages to users.
 * </p>
 */
public class BookingException extends Exception {

    /**
     * Constructs a new BookingException with a detailed message.
     * 
     * @param message The detail message explaining the booking error
     */
    public BookingException(String message) {
        super(message);
    }

    /**
     * Constructs a new BookingException with a detailed message and cause.
     * <p>
     * This constructor is useful when wrapping lower-level exceptions
     * that occurred during the booking process.
     * </p>
     * 
     * @param message The detail message explaining the booking error
     * @param cause   The underlying cause of this exception
     */
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}
