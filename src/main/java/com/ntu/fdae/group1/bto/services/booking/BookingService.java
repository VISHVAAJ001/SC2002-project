package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

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
    public Booking performBooking(HDBOfficer officer, String applicantNRIC, String flatType) throws BookingException {
        // Implementation logic for booking process
        // 1. Validate the officer has permission
        // 2. Check if applicant exists
        // 3. Verify flat type is available
        // 4. Create and store the booking
        // 5. Return the booking object

        // This is a placeholder implementation
        return null;
    }
}
