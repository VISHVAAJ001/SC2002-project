package com.ntu.fdae.group1.bto.controllers.booking;

import java.util.List;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IBookingService;

/**
 * Controller class responsible for managing booking operations in the BTO
 * Management System.
 * <p>
 * This controller acts as an intermediary between the UI layer and the booking
 * service,
 * handling booking-related operations such as creating new bookings and
 * retrieving
 * booking information for specific projects.
 * </p>
 * <p>
 * The BookingController implements the MVC architecture pattern by:
 * 1. Receiving booking requests from the UI
 * 2. Validating and processing these requests through the booking service
 * 3. Returning the results back to the UI
 * </p>
 */
public class BookingController {
    /**
     * The booking service instance used to perform booking operations.
     * This service contains the business logic for managing bookings.
     */
    private final IBookingService bookingService;

    /**
     * Constructs a new BookingController with the specified booking service.
     * 
     * @param bookingService The booking service to be used for booking operations
     */
    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    /**
     * Creates a new booking for an applicant by an HDB officer.
     * <p>
     * This method delegates the booking creation process to the booking service,
     * which handles the validation and business logic for creating a booking.
     * </p>
     * 
     * @param officer       The HDB officer creating the booking
     * @param applicantNric The NRIC of the applicant for whom the booking is being
     *                      created
     * @param flatType      The type of flat being booked
     * @return The newly created Booking object
     * @throws BookingException      If there is an issue with the booking process
     * @throws InvalidInputException If the provided input parameters are invalid
     */
    public Booking createBooking(HDBOfficer officer, String applicantNric, FlatType flatType)
            throws BookingException, InvalidInputException {
        return bookingService.performBooking(officer, applicantNric, flatType);
    }

    /**
     * Retrieves all bookings associated with a specific project.
     * <p>
     * This method allows for listing all bookings that have been made for a
     * particular HDB project, identified by its ID.
     * </p>
     * 
     * @param projectId The unique identifier of the project
     * @return A list of Booking objects associated with the specified project
     * @throws BookingException If there is an error retrieving the bookings
     */
    public List<Booking> getBookingsForProject(String projectId) throws BookingException {
        return bookingService.getBookingsByProject(projectId);
    }
}
