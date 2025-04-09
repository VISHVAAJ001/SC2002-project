package com.ntu.fdae.group1.bto.controllers.project;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
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
     * @param user              The applicant submitting the application
     * @param projectId         ID of the project to apply for
     * @param preferredFlatType The preferred flat type
     * @return The created application
     * @throws ApplicationException if submission fails
     */
    public Application submitApplication(User user, String projectId, FlatType preferredFlatType)
            throws ApplicationException {
        return applicationService.submitApplication(user, projectId, preferredFlatType);
    }

    /**
     * Requests withdrawal of an application
     * 
     * @param user The applicant requesting withdrawal
     * @return true if request was successful, false otherwise
     * @throws ApplicationException if withdrawal request fails
     */
    public boolean requestWithdrawal(User user) throws ApplicationException {
        if (user == null)
            throw new ApplicationException("Applicant cannot be null for withdrawal.");
        return applicationService.requestWithdrawal(user);
    }

    /**
     * Reviews an application
     * 
     * @param manager       The manager reviewing the application
     * @param applicationId ID of the application to review
     * @param approve       true to approve, false to reject
     * @return true if review was successful, false otherwise
     * @throws ApplicationException if review fails
     */
    public boolean reviewApplication(HDBManager manager, String applicationId, boolean approve)
            throws ApplicationException {
        if (manager == null)
            throw new ApplicationException("Manager context required for review.");
        if (applicationId == null || applicationId.trim().isEmpty())
            throw new ApplicationException("Application ID required for review.");
        return applicationService.reviewApplication(manager, applicationId, approve);
    }

    /**
     * Reviews a withdrawal request
     * 
     * @param manager       The manager reviewing the withdrawal
     * @param applicationId ID of the application to withdraw
     * @param approve       true to approve, false to reject withdrawal
     * @return true if review was successful, false otherwise
     * @throws ApplicationException if review fails
     */
    public boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve)
            throws ApplicationException {
        if (manager == null)
            throw new ApplicationException("Manager context required for withdrawal review.");
        if (applicationId == null || applicationId.trim().isEmpty())
            throw new ApplicationException("Application ID required for withdrawal review.");
        return applicationService.reviewWithdrawal(manager, applicationId, approve);
    }

    /**
     * Gets the application for a specific applicant
     * 
     * @param user The applicant
     * @return The application, or null if not found
     */
    public Application getMyApplication(User user) {
        return applicationService.getApplicationForUser(user.getNric());
    }

    /**
     * Gets all applications for a specific project
     * 
     * @param staff     HDB staff member requesting the information
     * @param projectId ID of the project
     * @return List of applications for the project
     */
    public List<Application> getProjectApplications(HDBStaff staff, String projectId) throws ApplicationException {
        if (staff == null)
            throw new ApplicationException("Staff context required.");
        if (projectId == null || projectId.trim().isEmpty())
            throw new ApplicationException("Project ID required.");
        // Add authorization logic here or in service if needed
        return applicationService.getApplicationsByProject(projectId);
    }

    /**
     * Gets all applications with a specific status
     * 
     * @param staff  HDB staff member requesting the information
     * @param status Status to filter by
     * @return List of applications with the specified status
     */
    public List<Application> getApplicationsByStatus(HDBStaff staff, ApplicationStatus status)
            throws ApplicationException {
        if (staff == null)
            throw new ApplicationException("Staff context required.");
        if (status == null)
            throw new ApplicationException("Status required.");
        // Add authorization logic here or in service if needed
        return applicationService.getApplicationsByStatus(status);
    }

}
