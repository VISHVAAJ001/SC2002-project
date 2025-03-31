package com.ntu.fdae.group1.bto.controllers.project;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.services.project.IApplicationService;

/**
 * Controller for application-related operations
 */
public class ApplicationController {
    private final IApplicationService applicationService;

    /**
     * Constructs a new ApplicationController
     * 
     * @param applicationService The application service to use
     */
    public ApplicationController(IApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Submits a new application
     * 
     * @param applicant The applicant submitting the application
     * @param projectId ID of the project to apply for
     * @return The created application
     * @throws ApplicationException if submission fails
     */
    public Application submitApplication(Applicant applicant, String projectId) throws ApplicationException {
        return applicationService.submitApplication(applicant, projectId);
    }

    /**
     * Requests withdrawal of an application
     * 
     * @param applicant The applicant requesting withdrawal
     * @return true if request was successful, false otherwise
     * @throws ApplicationException if withdrawal request fails
     */
    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        return applicationService.requestWithdrawal(applicant);
    }

    /**
     * Reviews an application
     * 
     * @param manager       The manager reviewing the application
     * @param applicationId ID of the application to review
     * @param approve       true to approve, false to reject
     * @return true if review was successful, false otherwise
     */
    public boolean reviewApplication(HDBManager manager, String applicationId, boolean approve) {
        return applicationService.reviewApplication(manager, applicationId, approve);
    }

    /**
     * Reviews a withdrawal request
     * 
     * @param manager       The manager reviewing the withdrawal
     * @param applicationId ID of the application to withdraw
     * @param approve       true to approve, false to reject withdrawal
     * @return true if review was successful, false otherwise
     */
    public boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve) {
        return applicationService.reviewWithdrawal(manager, applicationId, approve);
    }

    /**
     * Gets the application for a specific applicant
     * 
     * @param applicantNRIC NRIC of the applicant
     * @return The application, or null if not found
     */
    public Application getApplicationForUser(String applicantNRIC) {
        return applicationService.getApplicationForUser(applicantNRIC);
    }

    /**
     * Gets all applications for a specific project
     * 
     * @param projectId ID of the project
     * @return List of applications for the project
     */
    public List<Application> getApplicationsByProject(String projectId) {
        return applicationService.getApplicationsByProject(projectId);
    }

    /**
     * Gets all applications with a specific status
     * 
     * @param status Status to filter by
     * @return List of applications with the specified status
     */
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return applicationService.getApplicationsByStatus(status);
    }
}
