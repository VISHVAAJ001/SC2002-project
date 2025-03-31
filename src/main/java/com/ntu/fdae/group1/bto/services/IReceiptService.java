package com.ntu.fdae.group1.bto.services;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;

public interface IReceiptService {
    /**
     * Generates a receipt text for a booking
     * 
     * @param booking   The booking to generate a receipt for
     * @param applicant The applicant who made the booking
     * @param project   The project for which the booking was made
     * @return The receipt as a formatted string
     */
    String generateBookingReceipt(Booking booking, User applicant, Project project);
}
