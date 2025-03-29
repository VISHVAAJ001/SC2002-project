package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.project.Booking;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.exceptions.BookingException;

import java.util.Map;

public class BookingService implements IBookingService {

    private Map<String, Application> applicationRepo;
    private Map<String, Project> projectRepo;
    private Map<String, Booking> bookingRepo;
    private Map<String, User> userRepo;
    private IDataManager dataManager;

    public BookingService(Map<String, Application> applicationRepo,
                          Map<String, Project> projectRepo,
                          Map<String, Booking> bookingRepo,
                          Map<String, User> userRepo,
                          IDataManager dataManager) {
        this.applicationRepo = applicationRepo;
        this.projectRepo = projectRepo;
        this.bookingRepo = bookingRepo;
        this.userRepo = userRepo;
        this.dataManager = dataManager;
    }

    @Override
    public Booking performBooking(HDBOfficer officer, String applicantNRIC, String flatType) throws BookingException {
        User user = userRepo.get(applicantNRIC);
        if (!(user instanceof Applicant)) {
            throw new BookingException("Applicant not found or invalid.");
        }
        Applicant applicant = (Applicant) user;

        // Get officer’s assigned project
        Project project = officer.getAssignedProject();
        if (project == null) {
            throw new BookingException("Officer has no assigned project.");
        }

        // Check eligibility
        EligibilityService eligibilityService = new EligibilityService();
        if (!eligibilityService.canApplicantApply(applicant, project)) {
            throw new BookingException("Applicant not eligible for this project.");
        }

        // Create booking
        Booking booking = new Booking(flatType, java.time.LocalDate.now().toString());

        // Create and save application
        Application application = new Application();
        applicationRepo.put(applicantNRIC, application);
        bookingRepo.put(applicantNRIC, booking);

        // Persist changes
        dataManager.saveApplications(applicationRepo);
        dataManager.saveBookings(bookingRepo);

        return booking;
    }

}
