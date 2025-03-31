package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.repository.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.IProjectRepository;

import java.util.List;
import java.time.LocalDate;

public class ApplicationController {
    private IApplicationRepository applicationRepo;
    private IProjectRepository projectRepo;
    private IEligibilityService eligibilityService;

    public ApplicationController(IApplicationRepository appRepo, IProjectRepository projRepo,
            IEligibilityService eligSvc) {
        this.applicationRepo = appRepo;
        this.projectRepo = projRepo;
        this.eligibilityService = eligSvc;
    }

    public boolean submitApplication(Applicant applicant, String projectId) throws ApplicationException {
        // Implementation
        return true;
    }

    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        // Implementation
        return true;
    }

    public boolean approveApplication(HDBManager manager, String applicationId) {
        // Implementation
        return false;
    }

    public boolean rejectApplication(HDBManager manager, String applicationId) {
        // Implementation logic
        return false;
    }

    public boolean approveWithdrawal(HDBManager manager, String applicationId) {
        // Implementation
        return true;
    }

    public boolean rejectWithdrawal(HDBManager manager, String applicationId) {
        // Implementation
        return true;
    }

    public Application getApplicationForUser(String applicantNRIC) {
        // Implementation
        return null;
    }

    public List<Application> getApplicationsByProject(String projectId) {
        // Implementation
        return null;
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        // Implementation
        return null;
    }
}
