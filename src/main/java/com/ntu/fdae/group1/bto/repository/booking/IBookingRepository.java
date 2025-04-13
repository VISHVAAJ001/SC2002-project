package com.ntu.fdae.group1.bto.repository.booking;

import java.util.List;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.repository.IRepository;

/**
 * Repository interface for accessing and manipulating Booking entities in the
 * data store.
 * <p>
 * This interface defines the contract for booking data access operations,
 * including:
 * - Finding bookings by various criteria (ID, applicant, project)
 * - Saving new bookings
 * - Updating existing bookings
 * - Deleting bookings
 * </p>
 * <p>
 * The repository follows the Repository pattern, abstracting the data access
 * logic
 * from the rest of the application and providing a collection-like interface
 * for
 * booking objects.
 * </p>
 */
public interface IBookingRepository extends IRepository<Booking, String> {

    /**
     * Finds a booking by its unique identifier.
     *
     * @param bookingId The unique identifier of the booking to retrieve
     * @return The booking with the specified ID, or null if not found
     */
    Booking findById(String bookingId);

    /**
     * Finds all bookings made by a specific applicant.
     *
     * @param applicantNric The NRIC of the applicant
     * @return A booking associated with the specified applicant, or null if not
     *         found
     */
    Booking findByApplicantNric(String applicantNric);

    /**
     * Finds a booking by its associated application ID.
     *
     * @param applicationId The unique identifier of the application
     * @return The booking associated with the specified application ID, or null if
     *         not found
     */
    Booking findByApplicationId(String applicationId);

    /**
     * Finds all bookings associated with a specific project.
     *
     * @param projectId The unique identifier of the project
     * @return A list of bookings for the specified project
     */
    List<Booking> findByProjectId(String projectId);
}
