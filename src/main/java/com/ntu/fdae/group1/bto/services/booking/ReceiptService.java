package com.ntu.fdae.group1.bto.services.booking;

import java.util.Objects;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;

import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

/**
 * Service for generating booking receipts in the BTO Management System.
 * <p>
 * This service is responsible for assembling booking receipt information by:
 * - Retrieving applicant details from the user repository
 * - Retrieving project details from the project repository
 * - Combining booking, applicant, and project information into a receipt
 * </p>
 * <p>
 * The service follows the Service Layer pattern, encapsulating business logic
 * related to receipt generation and providing a clean API for controllers.
 * </p>
 */
public class ReceiptService implements IReceiptService {
    /**
     * Repository for accessing user data, particularly applicant information.
     */
    private final IUserRepository userRepo;

    /**
     * Repository for accessing project data related to bookings.
     */
    private final IProjectRepository projectRepo;

    /**
     * Constructs a ReceiptService with required repositories.
     *
     * @param userRepo Repository for accessing user data
     * @param projRepo Repository for accessing project data
     * @throws NullPointerException if either repository is null
     */
    public ReceiptService(IUserRepository userRepo, IProjectRepository projRepo) {
        // this.bookingRepo = Objects.requireNonNull(bookRepo);
        this.userRepo = Objects.requireNonNull(userRepo, "UserRepository cannot be null");
        this.projectRepo = Objects.requireNonNull(projRepo, "ProjectRepository cannot be null");
    }

    /**
     * Gathers necessary information (Applicant, Project) related to the given
     * Booking
     * and constructs a BookingReceiptInfo DTO.
     *
     * @param booking The core Booking object containing IDs for related entities.
     * @return A populated BookingReceiptInfo DTO.
     * @throws DataAccessException      If required User or Project data associated
     *                                  with the Booking cannot be found.
     * @throws NullPointerException     If the input booking is null.
     * @throws IllegalArgumentException If the booking object is missing required
     *                                  IDs.
     */
    @Override
    public BookingReceiptInfo generateBookingReceipt(Booking booking) throws DataAccessException {
        // 1. Validate Input Booking Object
        Objects.requireNonNull(booking, "Booking object cannot be null for receipt generation.");
        if (booking.getApplicantNric() == null || booking.getProjectId() == null
                || booking.getBookedFlatType() == null) {
            throw new IllegalArgumentException("Booking object is incomplete (missing NRIC, ProjectID, or FlatType).");
        }

        // 2. Fetch Related Data using Repositories
        User applicant = userRepo.findById(booking.getApplicantNric());
        if (applicant == null) {
            throw new DataAccessException("Receipt generation failed: Could not find Applicant details for NRIC "
                    + booking.getApplicantNric(), null);
        }

        Project project = projectRepo.findById(booking.getProjectId());
        if (project == null) {
            throw new DataAccessException("Receipt generation failed: Could not find Project details for ID "
                    + booking.getProjectId(), null);
        }

        // 3. Assemble the DTO
        // Assuming BookingReceiptInfo has appropriate setters or a constructor
        BookingReceiptInfo receiptInfo = new BookingReceiptInfo(
                applicant.getName(),
                applicant.getNric(),
                applicant.getAge(),
                applicant.getMaritalStatus(),
                booking.getBookedFlatType().name(), // Assuming bookedFlatType is FlatType enum in Booking
                project.getProjectName(),
                project.getNeighborhood(),
                booking.getBookingId(),
                booking.getBookingDate());

        // 4. Return the Populated DTO
        return receiptInfo;
    }
}
