package com.ntu.fdae.group1.bto.services.booking;

import java.time.LocalDate;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
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

public class BookingService implements IBookingService {
    private IApplicationRepository applicationRepo;
    private IProjectRepository projectRepo;
    private IBookingRepository bookingRepo;
    private IUserRepository userRepo;

    public BookingService(IApplicationRepository appRepo, IProjectRepository projRepo,
            IBookingRepository bookingRepo, IUserRepository userRepo) {
        this.applicationRepo = appRepo;
        this.projectRepo = projRepo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
    }

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
        if (flatType == null) {
            throw new BookingException("Flat type must be specified for booking.");
        }

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
        // Optional: Double-check via application status (might be redundant but safer)
        // Requires applicationRepo to support finding all apps by NRIC
        /*
         * List<Application> userApplications =
         * applicationRepo.findAllByApplicantNric(applicantNRIC); // Need this method
         * boolean alreadyBooked = userApplications.stream().anyMatch(app ->
         * app.getStatus() == ApplicationStatus.BOOKED);
         * if (alreadyBooked) {
         * throw new BookingException("Applicant " + applicantNRIC +
         * " already has a booked application.");
         * }
         */

        // 5. Retrieve the applicant's specific application that should be in SUCCESSFUL
        // state
        // (Assuming an applicant can only have one non-terminal application active at a
        // time)
        Application application = applicationRepo.findByApplicantNric(applicantNRIC); // Re-fetch or use from above if
                                                                                      // available
        if (application == null) {
            // This case might indicate data inconsistency if the previous booking check
            // passed
            throw new BookingException("No active application found for applicant " + applicantNRIC
                    + " eligible for booking.");
        }

        if (!application.getStatus().equals(ApplicationStatus.SUCCESSFUL)) {
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

        // 8. Check flat availability in the project
        // Use getFlatTypes().get(flatType) for Map access or keep getFlatInfo if
        // preferred
        ProjectFlatInfo flatInfo = project.getFlatTypes().get(flatType);
        if (flatInfo == null) {
            throw new BookingException(
                    "Project '" + project.getProjectName() + "' does not offer flat type: " + flatType.name());
        }
        if (flatInfo.getRemainingUnits() <= 0) {
            throw new BookingException("No remaining units available for " + flatType.name() + " in project '"
                    + project.getProjectName() + "'.");
        }

        // 9. Decrease available units and update the project
        // Assuming decreaseRemainingUnits handles the decrement and returns true/false
        // Consider potential synchronization needs if this could be called concurrently
        boolean decreased = flatInfo.decreaseRemainingUnits();
        if (!decreased) {
            // This might happen if remaining units became 0 between check and decrease
            throw new BookingException("Failed to decrease remaining units for " + flatType.name()
                    + ". Units might have been booked concurrently. Please retry.");
        }
        // Save the updated project state (persisting the decreased unit count)
        projectRepo.save(project);

        // 10. Create and save the new booking record
        String bookingId = IdGenerator.generateBookingId(); // Assuming static utility method
        LocalDate bookingDate = LocalDate.now();
        Booking newBooking = new Booking(bookingId, application.getApplicationId(), applicantNRIC,
                project.getProjectId(), flatType, bookingDate);
        bookingRepo.save(newBooking);

        // 11. Update the application status to BOOKED and save the application
        // (FAQ clarifies Officer manually sets status, system handles consequences -
        // this service method represents the whole transaction triggered by Officer UI
        // action)
        application.setStatus(ApplicationStatus.BOOKED);
        applicationRepo.save(application);

        // 12. Return the newly created booking object
        return newBooking;
    }
}
