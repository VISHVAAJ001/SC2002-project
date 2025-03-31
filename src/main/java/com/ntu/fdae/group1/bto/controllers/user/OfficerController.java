package com.ntu.fdae.group1.bto.controllers.user;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IBookingService;
import com.ntu.fdae.group1.bto.services.booking.IReceiptService;
import com.ntu.fdae.group1.bto.services.project.IOfficerRegistrationService;

/**
 * Controller for officer-related operations
 */
public class OfficerController {
    private final IOfficerRegistrationService registrationService;
    private final IBookingService bookingService;
    private final IReceiptService receiptService;

    /**
     * Constructs a new OfficerController
     * 
     * @param registrationService The registration service to use
     * @param bookingService      The booking service to use
     * @param receiptService      The receipt service to use
     */
    public OfficerController(IOfficerRegistrationService registrationService,
            IBookingService bookingService,
            IReceiptService receiptService) {
        this.registrationService = registrationService;
        this.bookingService = bookingService;
        this.receiptService = receiptService;
    }

    /**
     * Requests registration for a project
     * 
     * @param officer   The officer requesting registration
     * @param projectId ID of the project to register for
     * @return true if request was successful, false otherwise
     */
    public boolean requestProjectRegistration(HDBOfficer officer, String projectId) {
        try {
            registrationService.requestProjectRegistration(officer, projectId);
            return true;
        } catch (RegistrationException e) {
            return false;
        }
    }

    /**
     * Approves a registration request
     * 
     * @param manager        The manager approving the request
     * @param registrationId ID of the registration to approve
     * @return true if approval was successful, false otherwise
     */
    public boolean approveRegistration(HDBManager manager, String registrationId) {
        return registrationService.reviewRegistration(manager, registrationId, true);
    }

    /**
     * Rejects a registration request
     * 
     * @param manager        The manager rejecting the request
     * @param registrationId ID of the registration to reject
     * @return true if rejection was successful, false otherwise
     */
    public boolean rejectRegistration(HDBManager manager, String registrationId) {
        return registrationService.reviewRegistration(manager, registrationId, false);
    }

    /**
     * Handles flat booking for an applicant
     * 
     * @param officer       The officer handling the booking
     * @param applicantNRIC NRIC of the applicant
     * @param flatType      Type of flat to book
     * @return Receipt as a formatted string, or error message if booking fails
     */
    public String handleFlatBooking(HDBOfficer officer, String applicantNRIC, String flatType) {
        try {
            return bookingService.performBooking(officer, applicantNRIC, flatType).getBookingId();
        } catch (BookingException e) {
            return "Booking failed: " + e.getMessage();
        }
    }

    /**
     * Gets the status of an officer's registration for a project
     * 
     * @param officer   The officer
     * @param projectId ID of the project
     * @return The registration status, or null if not found
     */
    public OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId) {
        return registrationService.getRegistrationStatus(officer, projectId);
    }

    /**
     * Gets all pending registrations
     * 
     * @return List of pending registrations
     */
    public List<OfficerRegistration> getPendingRegistrations() {
        return registrationService.getPendingRegistrations();
    }

    /**
     * Gets all registrations for a specific project
     * 
     * @param projectId ID of the project
     * @return List of registrations for the project
     */
    public List<OfficerRegistration> getRegistrationsByProject(String projectId) {
        return registrationService.getRegistrationsByProject(projectId);
    }
}
