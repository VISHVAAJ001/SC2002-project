package com.ntu.fdae.group1.bto.services.project;

import java.util.Map;

import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

public class ReportService implements IReportService {
    private final IApplicationRepository applicationRepo;
    private final IBookingRepository bookingRepo;
    private final IProjectRepository projectRepo;
    private final IUserRepository userRepo;

    public ReportService(IApplicationRepository appRepo, IBookingRepository bookRepo, IProjectRepository projRepo,
            IUserRepository userRepo) {
        this.applicationRepo = appRepo;
        this.bookingRepo = bookRepo;
        this.projectRepo = projRepo;
        this.userRepo = userRepo;
    }

    @Override
    public String generateBookingReport(Map<String, String> filters) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'generateBookingReport'");
    }
}
