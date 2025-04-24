package com.ntu.fdae.group1.bto.services.booking;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

/**
 * Implementation of the IBookingService interface that provides booking
 * functionality
 * for the BTO Management System.
 * <p>
 * This service is responsible for managing the booking process, including
 * creating
 * new bookings, retrieving booking information, and handling booking
 * cancellations.
 * It implements all the business logic related to the booking process.
 * </p>
 */
public class BookingService implements IBookingService {

    /**
     * Repository for accessing and manipulating Application entities.
     */
    private final IApplicationRepository applicationRepo;

    /**
     * Repository for accessing and manipulating Project entities.
     */
    private final IProjectRepository projectRepo;

    /**
     * Repository for accessing and manipulating Booking entities.
     */
    private final IBookingRepository bookingRepo;

    /**
     * Repository for accessing and manipulating User entities.
     */
    private final IUserRepository userRepo;

    /**
     * Constructs a new BookingService with the specified repositories.
     *
     * @param appRepo     Repository for Application entities
     * @param projRepo    Repository for Project entities
     * @param bookingRepo Repository for Booking entities
     * @param userRepo    Repository for User entities
     */
    public BookingService(IApplicationRepository appRepo, IProjectRepository projRepo,
            IBookingRepository bookingRepo, IUserRepository userRepo) {
            this.applicationRepo = Objects.requireNonNull(appRepo);
            this.projectRepo = Objects.requireNonNull(projRepo);
            this.bookingRepo = Objects.requireNonNull(bookingRepo);
            this.userRepo = Objects.requireNonNull(userRepo);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Implementation details:
     * - Validates that the officer has permission to create bookings
     * - Validates the applicant exists and has an application for the project
     * - Checks if the flat type requested is available
     * - Creates and saves a booking record
     * </p>
     */
    @Override
    public Booking performBooking(HDBOfficer officer, String applicantNRIC, FlatType flatType) throws BookingException {

        // 1. Validate officer details
        if (officer == null) {
            throw new BookingException("HDB Officer details are required for booking.");
        }
        // 2. Validate applicantNRIC and flatType inputs
        if (applicantNRIC == null || applicantNRIC.trim().isEmpty()) {
            throw new BookingException("Applicant NRIC cannot be empty.");
        }
        Objects.requireNonNull(flatType, "Flat type must be specified for booking.");

        // 3. Verify that the applicant exists and has the correct role (or is generally
        // a valid user)
        User applicantUser = userRepo.findById(applicantNRIC);
        if (applicantUser == null) { // Removed role check here, focus is on finding the application next
            throw new BookingException("User with NRIC " + applicantNRIC + " not found.");
        }

        // 4. Retrieve the applicant's application for *any* project first to check
        // existing booking status
        // and then find the specific successful application.
        // **(Constraint Check: Single Booking Rule)**
        // Check if user already has a booking via the booking repository
        Booking existingBooking = bookingRepo.findByApplicantNric(applicantNRIC);
        if (existingBooking != null) {
            throw new BookingException("Applicant " + applicantNRIC + " already has an existing booking (ID: "
                    + existingBooking.getBookingId() + "). Cannot book another flat.");
        }
        
        // 5. Retrieve the applicant's specific application that should be in SUCCESSFUL state
        // (Assuming an applicant can only have one non-terminal application active at a time)
        Application application = applicationRepo.findByApplicantNric(applicantNRIC); // Re-fetch or use from above if available
        if (application == null) {
            // This case might indicate data inconsistency if the previous booking check passed
            throw new BookingException("No active application found for applicant " + applicantNRIC
                    + " eligible for booking.");
        }

        if (application.getStatus() != ApplicationStatus.SUCCESSFUL) {
            throw new BookingException("Application status must be SUCCESSFUL to book. Current status for App ID "
                    + application.getApplicationId() + ": " + application.getStatus());
        }

        // 6. Fetch the project associated with the application
        Project project = projectRepo.findById(application.getProjectId());
        if (project == null) {
            throw new BookingException("Project associated with the application not found (Project ID: "
                    + application.getProjectId() + ").");
        }

        // 7. **(Authorization Check: Officer Handling Project)**
        // Verify the officer performing the action is approved for this project
        if (project.getApprovedOfficerNrics() == null
                || !project.getApprovedOfficerNrics().contains(officer.getNric())) {
            throw new BookingException("Officer " + officer.getNric()
                    + " is not authorized to handle bookings for project " + project.getProjectId());
        }

        // 8. Check if requested flat type matches application preference
        FlatType preferredType = application.getPreferredFlatType();
        if (preferredType == null) {
            // This indicates a data integrity issue if an application is SUCCESSFUL but has no preferred type set.
            throw new BookingException(
                    "Booking failed: Applicant's preferred flat type is missing from the successful application (ID: " + application.getApplicationId() + "). Cannot proceed.");
        }
        if (!preferredType.equals(flatType)) {
            throw new BookingException(
                    String.format("Booking failed: The selected flat type (%s) does not match the applicant's approved preference (%s) on the application (ID: %s).",
                            flatType.name(), preferredType.name(), application.getApplicationId()));
        }

        // 9. Check flat availability in the project
        // Use getFlatTypes().get(flatType) for Map access or keep getFlatInfo if preferred
        ProjectFlatInfo flatInfo = project.getFlatTypes().get(flatType);
        // Should pass check since preferredType validation passed
        // Covers the case where the project configuration might be inconsistent.
        if (flatInfo == null) {
            throw new BookingException(
                    "Project '" + project.getProjectName() + "' does not offer flat type: " + flatType.name());
        }

        // 10. Check REMAINING UNITS for the specific flat type
        if (flatInfo.getRemainingUnits() <= 0) {
            throw new BookingException(
                String.format("Booking failed: No remaining units available for the required flat type (%s) in project '%s'.",
                              flatType.name(), project.getProjectName())
            );
        }

        // 11. Create and save the new booking record
        String bookingId = IdGenerator.generateBookingId(); // Assuming static utility method
        LocalDate bookingDate = LocalDate.now();
        Booking newBooking = new Booking(bookingId, application.getApplicationId(), applicantNRIC,
                project.getProjectId(), flatType, bookingDate);
        bookingRepo.save(newBooking);

        // 12. Update the application status to BOOKED and save the application
        // (FAQ clarifies Officer manually sets status, system handles consequences -
        // this service method represents the whole transaction triggered by Officer UI
        // action)
        application.setStatus(ApplicationStatus.BOOKED);
        applicationRepo.save(application);

        // 13. Return the newly created booking object
        return newBooking;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Booking> getBookingsByProject(String projectId) throws BookingException {
        if (projectId == null || projectId.trim().isEmpty()) {
            // Or throw InvalidInputException
            return Collections.emptyList();
        }
        try {
            // Delegate directly to repository
            List<Booking> bookings = bookingRepo.findByProjectId(projectId);
            return bookings != null ? bookings : Collections.emptyList();
        } catch (DataAccessException e) {
            // Log error if needed
            System.err.println("Error fetching bookings for project " + projectId + ": " + e.getMessage());
            throw new BookingException("Error fetching bookings for project " + projectId);
        } catch (Exception e) {
            System.err.println("Unexpected error fetching bookings for project " + projectId + ": " + e.getMessage());
            throw new RuntimeException("Unexpected error fetching bookings", e); // Wrap unexpected errors
        }
    }
}
