package com.ntu.fdae.group1.bto.controllers.project;

import java.util.List;
import java.util.Objects;

// Adjust imports as needed for your project structure
import com.ntu.fdae.group1.bto.exception.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.models.enums.FlatType;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBStaff; // Needed for updated methods
import com.ntu.fdae.group1.bto.services.project.IApplicationService;

/**
 * Controller for application-related operations, aligned with UML diagram.
 */
public class ApplicationController {
    private final IApplicationService applicationService;

    /**
     * Constructs a new ApplicationController
     *
     * @param applicationService The application service to use
     */
    public ApplicationController(IApplicationService applicationService) {
        this.applicationService = Objects.requireNonNull(applicationService, "Application Service cannot be null");
    }

    /**
     * Submits a new application.
     * Matches UML: submitApplication(applicant: Applicant, projectId: String, preferredFlatType: FlatType <<nullable>>) : Application <<throws ApplicationException>>
     *
     * @param applicant         The applicant submitting the application
     * @param projectId         ID of the project to apply for
     * @param preferredFlatType The applicant's preferred flat type (can be null)
     * @return The created application
     * @throws ApplicationException if submission fails (e.g., eligibility, project not found, already applied)
     */
    public Application submitApplication(Applicant applicant, String projectId, FlatType preferredFlatType)
            throws ApplicationException {
        if (applicant == null) {
            throw new ApplicationException("Applicant cannot be null for submission.");
        }
        if (projectId == null || projectId.isBlank()) {
            throw new ApplicationException("Project ID cannot be empty for submission.");
        }
        // Service layer handles preferredFlatType nullability and other business rules
        return applicationService.submitApplication(applicant, projectId, preferredFlatType);
    }

    /**
     * Requests withdrawal of the applicant's current application.
     * Matches UML: requestWithdrawal(applicant: Applicant) : boolean <<throws ApplicationException>>
     *
     * @param applicant The applicant requesting withdrawal
     * @return true if the request was successfully logged by the service
     * @throws ApplicationException if the applicant is null, has no application, or withdrawal is not allowed
     */
    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        if (applicant == null) {
            throw new ApplicationException("Applicant cannot be null for withdrawal request.");
        }
        return applicationService.requestWithdrawal(applicant);
    }

    /**
     * Reviews a pending application (Approve/Reject).
     * Matches UML: reviewApplication(manager: HDBManager, applicationId: String, approve: boolean) : boolean <<throws ApplicationException>>
     *
     * @param manager       The manager reviewing the application
     * @param applicationId ID of the application to review
     * @param approve       true to approve, false to reject
     * @return true if the review status was successfully updated by the service
     * @throws ApplicationException if manager/appId is null, app not found, manager lacks permission, app not pending, or business rules fail (e.g., no flats)
     */
    public boolean reviewApplication(HDBManager manager, String applicationId, boolean approve) throws ApplicationException {
        if (manager == null) {
            throw new ApplicationException("Manager cannot be null for reviewing application.");
        }
        if (applicationId == null || applicationId.isBlank()) {
            throw new ApplicationException("Application ID cannot be empty for review.");
        }
        return applicationService.reviewApplication(manager, applicationId, approve);
    }

    /**
     * Reviews a pending withdrawal request (Approve/Reject).
     * Matches UML: reviewWithdrawal(manager: HDBManager, applicationId: String, approve: boolean) : boolean <<throws ApplicationException>>
     *
     * @param manager       The manager reviewing the withdrawal
     * @param applicationId ID of the application with the withdrawal request
     * @param approve       true to approve withdrawal, false to reject it
     * @return true if the withdrawal review status was successfully updated by the service
     * @throws ApplicationException if manager/appId is null, app not found, manager lacks permission, no pending withdrawal, or withdrawal not allowed (e.g., already booked)
     */
    public boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve) throws ApplicationException {
        if (manager == null) {
            throw new ApplicationException("Manager cannot be null for reviewing withdrawal.");
        }
        if (applicationId == null || applicationId.isBlank()) {
            throw new ApplicationException("Application ID cannot be empty for withdrawal review.");
        }
        return applicationService.reviewWithdrawal(manager, applicationId, approve);
    }

    /**
     * Gets the application submitted by a specific applicant.
     * Matches UML: getMyApplication(applicant: Applicant) : Application <<nullable>>
     *
     * @param applicant The applicant whose application is requested
     * @return The applicant's Application object, or null if not found or applicant is null.
     */
    public Application getMyApplication(Applicant applicant) {
        if (applicant == null) {
            System.err.println("Controller Warning: Applicant object is required to get their application.");
            return null;
        }
        // Delegate to the service method that likely takes NRIC
        return applicationService.getApplicationForUser(applicant.getNric());
    }

    /**
     * Gets all applications submitted for a specific project. Requires staff context.
     * Matches UML: getProjectApplications(staff: HDBStaff, projectId: String) : List<Application>
     *
     * @param staff     The HDB staff member requesting the list (for context/potential authorization)
     * @param projectId ID of the project
     * @return List of applications for the specified project. Returns an empty list if project ID is invalid or no applications found.
     */
    public List<Application> getProjectApplications(HDBStaff staff, String projectId) {
        if (staff == null) {
             System.err.println("Controller Warning: Staff context is required to get project applications.");
             return List.of(); // Or throw exception if required
        }
        if (projectId == null || projectId.isBlank()) {
            System.err.println("Controller Warning: Project ID is required to get applications.");
            return List.of();
        }
        return applicationService.getApplicationsByProject(projectId);
    }

    /**
     * Gets all applications matching a specific status. Requires staff context.
     * Matches UML: getApplicationsByStatus(staff: HDBStaff, status: ApplicationStatus) : List<Application>
     *
     * @param staff  The HDB staff member requesting the list (for context/potential authorization)
     * @param status The application status to filter by
     * @return List of applications matching the status. Returns an empty list if status is null or no applications found.
     */
    public List<Application> getApplicationsByStatus(HDBStaff staff, ApplicationStatus status) {
         if (staff == null) {
             System.err.println("Controller Warning: Staff context is required to get applications by status.");
             return List.of(); 
         }
         if (status == null) {
             System.err.println("Controller Warning: Application status is required.");
             return List.of();
         }
         // The service method currently doesn't use staff, but we include it for UML alignment
         return applicationService.getApplicationsByStatus(status);
    }

}