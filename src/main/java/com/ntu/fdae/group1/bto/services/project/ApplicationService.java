package com.ntu.fdae.group1.bto.services.project;

import java.util.Collections;
import java.util.List;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
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
    public Application submitApplication(Applicant applicant, String projectId, FlatType preferredFlatType)
            throws ApplicationException {
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

    /**
     * Retrieves all applications submitted for a specific project.
     * Performs basic validation on the projectId.
     *
     * @param projectId The ID of the project whose applications are to be
     *                  retrieved.
     * @return A List of Application objects for the specified project. Returns an
     *         empty list if
     *         the projectId is invalid or no applications are found.
     * @throws DataAccessException if an error occurs during data retrieval from the
     *                             repository.
     */
    @Override
    public List<Application> getApplicationsByProject(String projectId) throws DataAccessException {
        // 1. Input Validation
        if (projectId == null || projectId.trim().isEmpty()) {
            System.err.println("Warning: getApplicationsByProject called with invalid projectId.");
            // Decide behaviour: throw InvalidInputException or return empty list?
            // Returning empty list might be simpler for calling UI code.
            return Collections.emptyList();
        }

        // 2. Delegate to Repository
        try {
            List<Application> applications = applicationRepo.findByProjectId(projectId);
            // Return the result, ensuring null is converted to empty list
            return applications != null ? applications : Collections.emptyList();
        } catch (DataAccessException e) {
            // Log the error (optional but recommended)
            System.err.println(
                    "Data access error fetching applications for project " + projectId + ": " + e.getMessage());
            // Re-throw the specific exception for the controller/UI to handle if needed
            throw e;
        } catch (Exception e) {
            // Catch any other unexpected runtime exceptions from the repository layer
            System.err
                    .println("Unexpected error fetching applications for project " + projectId + ": " + e.getMessage());
            // Wrap in a runtime exception or a specific service exception
            throw new RuntimeException("An unexpected error occurred while fetching applications.", e);
        }
    }

    // --- Implementation for getApplicationsByStatus (Example) ---
    /**
     * Retrieves all applications matching a specific status.
     * NOTE: This might return applications across multiple projects. Authorization
     * based on who is calling (e.g., Manager vs Officer) might be needed in the
     * Controller.
     *
     * @param status The ApplicationStatus to filter by.
     * @return A List of Application objects with the specified status.
     * @throws DataAccessException if data retrieval fails.
     */
    @Override // Assuming this method exists in IApplicationService
    public List<Application> getApplicationsByStatus(ApplicationStatus status) throws DataAccessException {
        if (status == null) {
            System.err.println("Warning: getApplicationsByStatus called with null status.");
            return Collections.emptyList();
        }
        try {
            List<Application> applications = applicationRepo.findByStatus(status); // You need this method in
                                                                                   // IApplicationRepository
            return applications != null ? applications : Collections.emptyList();

        } catch (DataAccessException e) {
            System.err.println("Data access error fetching applications by status " + status + ": " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error fetching applications by status " + status + ": " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while fetching applications.", e);
        }
    }

}
