package com.ntu.fdae.group1.bto.controllers.booking;

import java.util.Objects;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IReceiptService;

public class ReceiptController {
    private final IReceiptService receiptService;

    public ReceiptController(IReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    /**
     * Retrieves the consolidated information needed to generate a booking receipt.
     * Performs basic validation and delegates the main logic to the ReceiptService.
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
