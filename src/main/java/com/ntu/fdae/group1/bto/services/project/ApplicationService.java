package com.ntu.fdae.group1.bto.services.project;

import java.util.List;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;

public class ApplicationService implements IApplicationService {
    private final IApplicationRepository applicationRepo;
    private final IProjectRepository projectRepo;
    private final IEligibilityService eligibilityService;
    private final IUserRepository userRepo;
    private final IBookingRepository bookingRepo;

    public ApplicationService(IApplicationRepository appRepo, IProjectRepository projRepo,
            IEligibilityService eligSvc, IUserRepository userRepo, IBookingRepository bookingRepo) {
        this.applicationRepo = appRepo;
        this.projectRepo = projRepo;
        this.eligibilityService = eligSvc;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
    }

    @Override
    public Application submitApplication(Applicant applicant, String projectId) throws ApplicationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'submitApplication'");
    }

    @Override
    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestWithdrawal'");
    }

    @Override
    public boolean reviewApplication(HDBManager manager, String applicationId, boolean approve) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reviewApplication'");
    }

    @Override
    public boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reviewWithdrawal'");
    }

    @Override
    public Application getApplicationForUser(String applicantNRIC) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getApplicationForUser'");
    }

    @Override
    public List<Application> getApplicationsByProject(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getApplicationsByProject'");
    }

    @Override
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getApplicationsByStatus'");
    }

}
