package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;

import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

public class ReceiptService implements IReceiptService {
    private final IBookingRepository bookingRepository;
    private final IUserRepository userRepository;
    private final IProjectRepository projectRepository;

    public ReceiptService(IBookingRepository bookingRepository, IUserRepository userRepository,
            IProjectRepository projectRepository) {
        this.bookingRepository = bookingRepository;
        this.userRepository = userRepository;
        this.projectRepository = projectRepository;
    }

    @Override
    public String generateBookingReceipt(Booking booking, User applicant, Project project) {
        return "----- Booking Receipt ------\n"
                + "Applicant Name: " + applicant.getName() + "\n"
                + "Project: " + project.getProjectName() + "\n"
                + "Flat Type: " + booking.getBookedFlatType() + "\n"
                + "Booking Date: " + booking.getBookingDate() + "\n"
                + "-----------------------------";
    }
}
