package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;

/**
 * Service interface defining operations for generating booking receipts in the
 * BTO Management System.
 * <p>
 * This service provides functionality to generate receipt information for
 * bookings,
 * consolidating all the necessary details from various entities (bookings,
 * applicants,
 * projects) into a cohesive receipt format.
 * </p>
 * <p>
 * The service acts as part of the business logic layer in the application's
 * architecture,
 * positioned between the controllers and repositories, implementing receipt
 * generation
 * logic and coordinating data access across multiple repositories.
 * </p>
 */
public interface IReceiptService {

    /**
     * Generates comprehensive receipt information for a given booking.
     * <p>
     * This method retrieves and consolidates all the information needed for a
     * booking receipt,
     * including:
     * 1. Booking details (date, flat type, etc.)
     * 2. Applicant information (name, contact details, etc.)
     * 3. Project information (name, location, etc.)
     * </p>
     *
     * @param booking The booking for which to generate receipt information
     * @return A BookingReceiptInfo object containing all receipt details
     * @throws DataAccessException If there is an error accessing required data from
     *                             repositories
     */
    BookingReceiptInfo generateBookingReceipt(Booking booking) throws DataAccessException;
}