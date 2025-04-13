package com.ntu.fdae.group1.bto.services.booking;

import java.util.List;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;

/**
 * Interface defining the service operations for handling booking functionality
 * in the BTO Management System.
 * <p>
 * This service provides methods to:
 * - Create new bookings for applicants
 * - Retrieve bookings by various criteria
 * - Cancel existing bookings
 * </p>
 * <p>
 * The service acts as part of the business logic layer in the application's
 * architecture,
 * positioned between controllers and repositories, implementing all
 * booking-related
 * business rules and validation.
 * </p>
 */
public interface IBookingService {
    /**
     * Performs a booking operation for an applicant by an HDB officer.
     * <p>
     * This method handles the complete booking process including:
     * 1. Validating the officer's authorization to perform bookings
     * 2. Checking the applicant's eligibility for the specified flat type
     * 3. Creating and persisting the booking record
     * </p>
     * 
     * @param officer       The HDB officer performing the booking operation
     * @param applicantNric The NRIC of the applicant for whom the booking is being
     *                      made
     * @param flatType      The type of flat being booked
     * @return The newly created Booking object
     * @throws BookingException      If there is an issue with the booking process
     * @throws InvalidInputException If the provided input parameters are invalid
     */
    Booking performBooking(HDBOfficer officer, String applicantNric, FlatType flatType)
            throws BookingException, InvalidInputException;

    /**
     * Retrieves all bookings associated with a specific project.
     * <p>
     * This method allows for retrieving and analyzing all bookings that have been
     * made for a particular HDB project.
     * </p>
     * 
     * @param projectId The unique identifier of the project
     * @return A list of Booking objects associated with the specified project
     * @throws BookingException If there is an error retrieving the bookings
     */
    List<Booking> getBookingsByProject(String projectId) throws BookingException;
}
