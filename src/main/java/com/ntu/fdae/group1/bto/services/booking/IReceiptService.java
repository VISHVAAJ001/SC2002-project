package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;

public interface IReceiptService {
    public String generateBookingReceipt(Booking booking, User applicant, Project project);
}
