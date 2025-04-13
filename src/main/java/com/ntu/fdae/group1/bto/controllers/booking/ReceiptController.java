package com.ntu.fdae.group1.bto.controllers.booking;

import java.util.Objects;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IReceiptService;

/**
 * Controller responsible for managing receipt generation operations in the BTO
 * Management System.
 * <p>
 * This controller acts as an intermediary between the UI layer and the receipt
 * service,
 * handling operations related to generating booking receipts. It performs basic
 * validation
 * on inputs before delegating the business logic to the receipt service.
 * </p>
 * <p>
 * The ReceiptController follows the MVC architecture pattern by:
 * 1. Receiving receipt generation requests from the UI
 * 2. Validating inputs before processing
 * 3. Delegating business logic to the receipt service
 * 4. Handling exceptions appropriately
 * 5. Returning the generated receipt information to the UI
 * </p>
 */
public class ReceiptController {
    /**
     * The receipt service used to generate booking receipts.
     * This service contains the business logic for creating receipt information.
     */
    private final IReceiptService receiptService;

    /**
     * Constructs a new ReceiptController with the specified receipt service.
     * 
     * @param receiptService The receipt service to use for receipt generation
     *                       operations
     */
    public ReceiptController(IReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * Retrieves the consolidated information needed to generate a booking receipt.
     * Performs basic validation and delegates the main logic to the ReceiptService.
     * <p>
     * This method validates that both the officer and booking objects are valid and
     * complete before requesting receipt generation from the service layer. It also
     * handles any exceptions that may occur during the process.
     * </p>
     *
     * @param officer The HDB Officer performing the action (used for
     *                authorization/context if needed by service).
     * @param booking The Booking object for which to generate the receipt info.
     * @return A BookingReceiptInfo object containing all necessary details.
     * @throws DataAccessException      if there's an error retrieving related data
     *                                  (e.g., Applicant or Project details).
     * @throws NullPointerException     if officer or booking is null.
     * @throws IllegalArgumentException if the booking object seems invalid
     *                                  (optional stricter check).
     */
    public BookingReceiptInfo getBookingReceiptInfo(HDBOfficer officer, Booking booking)
            throws DataAccessException { // Or a more specific ReceiptGenerationException

        // 1. Input Validation
        Objects.requireNonNull(officer, "HDBOfficer performing the action cannot be null.");
        Objects.requireNonNull(booking, "Booking object cannot be null for receipt generation.");

        // Optional: Add more validation for the booking object if needed
        if (booking.getApplicantNric() == null || booking.getProjectId() == null
                || booking.getBookedFlatType() == null) {
            // Or throw a custom exception
            throw new IllegalArgumentException("Booking object is incomplete (missing NRIC, ProjectID, or FlatType).");
        }

        // 2. Delegate to Service Layer
        // The service layer is responsible for fetching the associated Applicant and
        // Project
        // details and assembling the BookingReceiptInfo DTO.
        try {
            BookingReceiptInfo receiptInfo = receiptService.generateBookingReceipt(booking);
            // ReceiptService handles finding User by booking.getApplicantNric()
            // and Project by booking.getProjectId() and assembling the DTO.

            // Check if service returned null (might indicate data inconsistency)
            if (receiptInfo == null) {
                // This case suggests the service couldn't find related data (e.g.,
                // user/project)
                throw new DataAccessException(
                        "Failed to generate receipt info: Could not retrieve all required related data.", null);
            }

            return receiptInfo;

        } catch (DataAccessException dae) {
            // Re-throw data access exceptions from the service
            System.err.println(
                    "ReceiptController Error: Data access issue during receipt generation - " + dae.getMessage());
            throw dae;
        } catch (Exception e) {
            // Catch any other unexpected exceptions from the service layer
            System.err
                    .println("ReceiptController Error: Unexpected issue during receipt generation - " + e.getMessage());
            // Wrap in a DataAccessException or a custom ReceiptException
            throw new DataAccessException("An unexpected error occurred while generating the receipt.", e);
        }
    }

}
