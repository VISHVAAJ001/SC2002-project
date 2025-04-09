package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IOfficerRegistrationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

public class ApplicationService implements IApplicationService {
    private final IApplicationRepository applicationRepo;
    private final IProjectRepository projectRepo;
    private final IEligibilityService eligibilityService;
    private final IUserRepository userRepo;
    private final IBookingRepository bookingRepo;
    private final IOfficerRegistrationRepository officerRegRepo;

    public ApplicationService(IApplicationRepository appRepo, IProjectRepository projRepo,
            IEligibilityService eligSvc, IUserRepository userRepo, IBookingRepository bookingRepo,
            IOfficerRegistrationRepository officerRegRepo) {
        this.applicationRepo = appRepo;
        this.projectRepo = projRepo;
        this.eligibilityService = eligSvc;
        this.userRepo = userRepo;
        this.bookingRepo = bookingRepo;
        this.officerRegRepo = officerRegRepo;
    }

    @Override
    public Application submitApplication(Applicant applicant, String projectId, FlatType preferredFlatType)
            throws ApplicationException {

        // --- 1. Input Validation ---
        Objects.requireNonNull(applicant, "Applicant cannot be null.");
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new ApplicationException("Project ID cannot be empty.");
        }

        // --- 2. Fetch Project ---
        Project project = projectRepo.findById(projectId);
        if (project == null) {
            throw new ApplicationException("Project with ID '" + projectId + "' not found.");
        }

        // --- 3. Check Project Status ---
        if (!project.isVisible()) {
            throw new ApplicationException(
                    "Project '" + project.getProjectName() + "' is not currently visible for application.");
        }
        LocalDate currentDate = LocalDate.now();
        if (currentDate.isBefore(project.getOpeningDate()) || currentDate.isAfter(project.getClosingDate())) {
            throw new ApplicationException("Project '" + project.getProjectName() + "' application period ("
                    + project.getOpeningDate() + " to " + project.getClosingDate() + ") is not active.");
        }

        // --- 4. Check Existing Application ---
        Application existingApp = applicationRepo.findByApplicantNric(applicant.getNric());
        if (existingApp != null) {
            ApplicationStatus currentStatus = existingApp.getStatus();
            if (currentStatus == ApplicationStatus.PENDING ||
                    currentStatus == ApplicationStatus.SUCCESSFUL ||
                    currentStatus == ApplicationStatus.BOOKED) {
                throw new ApplicationException("You already have an active application (ID: "
                        + existingApp.getApplicationId() + ", Status: " + currentStatus
                        + "). You cannot submit a new one until it is concluded.");
            }
        }

        // --- 5. Check Applicant Eligibility for this Project ---
        if (!eligibilityService.canApplicantApply(applicant, project)) { // Use the service method
            throw new ApplicationException("You are not eligible for project '" + project.getProjectName()
                    + "' based on age, marital status, or available flat types.");
        }

        // --- 6. Validate Preference (if provided) ---
        if (preferredFlatType != null) {
            if (!project.getFlatTypes().containsKey(preferredFlatType)) {
                throw new ApplicationException("Project '" + project.getProjectName()
                        + "' does not offer the preferred flat type: " + preferredFlatType);
            }
            if (!eligibilityService.isApplicantEligibleForFlatType(applicant, preferredFlatType)) {
                throw new ApplicationException(
                        "You are not eligible for the preferred flat type: " + preferredFlatType);
            }
        }

        // --- 7. Check Officer Restrictions ---
        if (applicant.getRole() == UserRole.HDB_OFFICER) {
            List<OfficerRegistration> registrations = officerRegRepo.findByOfficerNric(applicant.getNric());
            boolean isRegisteredForThisProject = registrations.stream()
                    .filter(reg -> reg.getProjectId().equals(projectId))
                    .anyMatch(reg -> reg.getStatus() == OfficerRegStatus.PENDING
                            || reg.getStatus() == OfficerRegStatus.APPROVED);

            if (isRegisteredForThisProject) {
                throw new ApplicationException("As an HDB Officer registered for project '"
                        + project.getProjectName() + "', you cannot submit an application for it.");
            }
        }

        // --- 8. Create New Application ---
        String newAppId = IdGenerator.generateApplicationId();
        Application newApplication = new Application(newAppId, applicant.getNric(), projectId, LocalDate.now());
        newApplication.setStatus(ApplicationStatus.PENDING);
        newApplication.setPreferredFlatType(preferredFlatType);

        // --- 9. Save Application ---
        applicationRepo.save(newApplication);
        System.out.println("ApplicationService: Saved new application " + newAppId);

        return newApplication;
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

    /**
     * Retrieves the most relevant application associated with a given applicant
     * NRIC.
     * In this system, an applicant can only have one "active" (PENDING, SUCCESSFUL,
     * BOOKED)
     * application at a time. If they have previous UNSUCCESSFUL ones, this method
     * should ideally return the latest relevant one, or potentially null if none
     * are active.
     * The repository method findByApplicantNric likely handles finding the correct
     * one.
     *
     * @param applicantNric The NRIC of the applicant.
     * @return The Applicant's Application object, or null if none is found or
     *         relevant.
     */
    @Override
    public Application getApplicationForUser(String applicantNric) {
        if (applicantNric == null || applicantNric.trim().isEmpty()) {
            System.err.println("ApplicationService Warning: Cannot get application for null or empty NRIC.");
            return null; // Or throw IllegalArgumentException
        }
        // Delegate directly to the repository method which should encapsulate
        // the logic of finding the single relevant application for the user.
        // (e.g., the latest one, or the only one not in a final 'UNSUCCESSFUL' state
        // perhaps)
        return applicationRepo.findByApplicantNric(applicantNric);
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
