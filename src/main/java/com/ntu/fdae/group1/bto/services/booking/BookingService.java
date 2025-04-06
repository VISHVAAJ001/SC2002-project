package com.ntu.fdae.group1.bto.services.booking;

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
import com.ntu.fdae.group1.bto.utilities.IdGenerator;
import java.time.LocalDate;

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
            throw new BookingException("HDB Officer details are required.");
        }
        // 2. Validate applicantNRIC and flatType inputs
        if (applicantNRIC == null || applicantNRIC.trim().isEmpty()) {
            throw new BookingException("Applicant NRIC cannot be empty.");
        }
        if (flatType == null) {
            throw new BookingException("Flat type must be specified for booking.");
        }
        
        // 3. Verify that the applicant exists and has the correct role
        User applicantUser = userRepo.findById(applicantNRIC);
        if (applicantUser == null || !applicantUser.getRole().equals(UserRole.APPLICANT)) {
            throw new BookingException("Applicant with NRIC " + applicantNRIC + " not found or is not an applicant.");
        }
        
        // 4. Retrieve the applicant's application and verify its status is SUCCESSFUL
        Application application = applicationRepo.findByApplicantNric(applicantNRIC);
        if (application == null) {
            throw new BookingException("No application found for applicant " + applicantNRIC);
        }
        if (!application.getStatus().equals(ApplicationStatus.SUCCESSFUL)) {
            throw new BookingException("Application status must be SUCCESSFUL to book. Current status: " + application.getStatus());
        }
        
        // 5. Fetch the project associated with the application
        Project project = projectRepo.findById(application.getProjectId());
        if (project == null) {
            throw new BookingException("Project associated with the application not found (Project ID: " + application.getProjectId() + ").");
        }
        
        // 6. Check flat availability in the project
        ProjectFlatInfo flatInfo = project.getFlatInfo(flatType);
        if (flatInfo == null) {
            throw new BookingException("Project '" + project.getProjectName() + "' does not offer flat type: " + flatType.name());
        }
        if (flatInfo.getRemainingUnits() <= 0) {
            throw new BookingException("No remaining units available for " + flatType.name() + " in project '" + project.getProjectName() + "'.");
        }
        
        // 7. Decrease available units and update the project
        boolean decreased = flatInfo.decreaseRemainingUnits();
        if (!decreased) {
            throw new BookingException("Failed to decrease remaining units for " + flatType.name() + ". Possibly due to concurrent access.");
        }
        projectRepo.save(project);
        
        // 8. Create and save the new booking record
        String bookingId = IdGenerator.generateBookingId();
        LocalDate bookingDate = LocalDate.now();
        Booking newBooking = new Booking(bookingId, application.getApplicationId(), applicantNRIC, project.getProjectId(), flatType, bookingDate);
        bookingRepo.save(newBooking);
        
        // 9. Update the application status to BOOKED and save the application
        application.setStatus(ApplicationStatus.BOOKED);
        applicationRepo.save(application);
        
        return newBooking;
    }

	public Booking performBooking1(HDBOfficer officer, String applicantNRIC, FlatType flatType)
			throws BookingException {
		// TODO Auto-generated method stub
		return null;
	}

	public Booking performBooking(HDBOfficer officer, String applicantNRIC, String flatType) throws BookingException {
		// TODO Auto-generated method stub
		return null;
	}
}
