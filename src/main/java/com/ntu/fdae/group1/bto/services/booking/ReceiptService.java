package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.project.Booking;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;

public class ReceiptService implements IReceiptService {

    public String generateBookingReceipt(Booking booking, User applicant, Project project) {
        return "----- Booking Receipt ------\n"
                + "Applicant Name: " + applicant.getName() + "\n"
                + "Project: " + project.getProjectName() + "\n"
                + "Flat Type: " + booking.getBookedFlatType() + "\n"
                + "Booking Date: " + booking.getBookingDate() + "\n"
                + "-----------------------------";
    }
}
