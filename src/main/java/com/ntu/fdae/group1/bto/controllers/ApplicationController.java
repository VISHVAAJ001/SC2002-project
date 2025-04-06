package com.ntu.fdae.group1.bto.controllers;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.services.application.IApplicationService;
import java.util.List;

public class ApplicationController {

    private final IApplicationService applicationService;

    public ApplicationController(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    public Application submitApplication(Applicant applicant, String projectId, String preferredFlatType)
            throws ApplicationException {
        // Convert string to enum
        FlatType flatType;
        try {
            flatType = FlatType.valueOf(preferredFlatType.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new ApplicationException("Invalid flat type provided: " + preferredFlatType);
        }
        return applicationService.submitApplication(applicant, projectId, flatType);
    }

    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        return applicationService.requestWithdrawal(applicant);
    }

    public boolean reviewApplication(String applicationId, boolean approve) throws ApplicationException {
        return applicationService.reviewApplication(applicationId, approve);
    }

    public boolean reviewWithdrawal(String applicationId, boolean approve) throws ApplicationException {
        return applicationService.reviewWithdrawal(applicationId, approve);
    }

    public Application getMyApplication(String applicantNRIC) throws ApplicationException {
        return (Application) applicationService.getApplicationsByProject(applicantNRIC);
    }

    public List<Application> getProjectApplications(String projectId) throws ApplicationException {
        return applicationService.getApplicationsByProject(projectId);
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) throws ApplicationException {
        return applicationService.getApplicationsByProject(status);
    }
}
