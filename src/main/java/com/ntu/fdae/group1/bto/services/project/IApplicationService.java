package com.ntu.fdae.group1.bto.services.project;

import java.util.List;

import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.User;

public interface IApplicationService {
    /**
     * Submits a new application for a project
     * 
     * @param user      The applicant submitting the application
     * @param projectId ID of the project to apply for
     * @return The created application
     * @throws ApplicationException if application submission fails
     */
    Application submitApplication(User user, String projectId, FlatType preferredFlatType)
            throws ApplicationException;

    /**
     * Requests withdrawal of an existing application
     * 
     * @param user The applicant requesting withdrawal
     * @return true if request was successful, false otherwise
     * @throws ApplicationException if withdrawal request fails
     */
    boolean requestWithdrawal(User user) throws ApplicationException;

    /**
     * Reviews an application
     * 
     * @param manager       The manager reviewing the application
     * @param applicationId ID of the application to review
     * @param approve       true to approve, false to reject
     * @return true if review was successful, false otherwise
     */
    boolean reviewApplication(HDBManager manager, String applicationId, boolean approve) throws ApplicationException;

    /**
     * Reviews a withdrawal request
     * 
     * @param manager       The manager reviewing the withdrawal
     * @param applicationId ID of the application to withdraw
     * @param approve       true to approve, false to reject withdrawal
     * @return true if review was successful, false otherwise
     */
    boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve) throws ApplicationException;

    /**
     * Gets the application for a specific applicant
     * 
     * @param applicantNRIC NRIC of the applicant
     * @return The application, or null if not found
     */
    Application getApplicationForUser(String applicantNRIC);

    /**
     * Gets all applications for a specific project
     * 
     * @param projectId ID of the project
     * @return List of applications for the project
     */
    List<Application> getApplicationsByProject(String projectId);

    /**
     * Gets all applications with a specific status
     * 
     * @param status Status to filter by
     * @return List of applications with the specified status
     */
    List<Application> getApplicationsByStatus(ApplicationStatus status);
}
