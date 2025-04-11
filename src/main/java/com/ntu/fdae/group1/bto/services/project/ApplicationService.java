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
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.User;
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
    public Application submitApplication(User user, String projectId, FlatType preferredFlatType)
            throws ApplicationException {

        // --- 1. Input Validation ---
        Objects.requireNonNull(user, "Applicant cannot be null.");
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
        Application existingApp = applicationRepo.findByApplicantNric(user.getNric());
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
        if (!eligibilityService.canApplicantApply(user, project)) { // Use the service method
            throw new ApplicationException("You are not eligible for project '" + project.getProjectName()
                    + "' based on age, marital status, or available flat types.");
        }

        // --- 6. Validate Preference (if provided) ---
        if (preferredFlatType != null) {
            if (!project.getFlatTypes().containsKey(preferredFlatType)) {
                throw new ApplicationException("Project '" + project.getProjectName()
                        + "' does not offer the preferred flat type: " + preferredFlatType);
            }
            if (!eligibilityService.isApplicantEligibleForFlatType(user, preferredFlatType)) {
                throw new ApplicationException(
                        "You are not eligible for the preferred flat type: " + preferredFlatType);
            }
            if (project.getFlatInfo(preferredFlatType) == null
                    || project.getFlatInfo(preferredFlatType).getRemainingUnits() <= 0) {
                throw new ApplicationException("No remaining units available for the preferred flat type: "
                        + preferredFlatType);
            }
        }

        // --- 7. Check Officer Restrictions ---
        if (user.getRole() == UserRole.HDB_OFFICER) {
            List<OfficerRegistration> registrations = officerRegRepo.findByOfficerNric(user.getNric());
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
        Application newApplication = new Application(newAppId, user.getNric(), projectId, LocalDate.now());
        newApplication.setStatus(ApplicationStatus.PENDING);
        newApplication.setPreferredFlatType(preferredFlatType);

        // --- 9. Save Application ---
        applicationRepo.save(newApplication);
        System.out.println("ApplicationService: Saved new application " + newAppId);

        return newApplication;
    }

    @Override
    public boolean requestWithdrawal(User user) throws ApplicationException {
        Objects.requireNonNull(user, "Applicant cannot be null");

        Application app = applicationRepo.findByApplicantNric(user.getNric());
        if (app == null) {
            throw new ApplicationException(
                    "No active application found for applicant " + user.getNric() + " to withdraw.");
        }

        // Check if already requested or in a final state
        if (app.getRequestedWithdrawalDate() != null) {
            throw new ApplicationException(
                    "Withdrawal has already been requested for application " + app.getApplicationId());
        }
        if (app.getStatus() == ApplicationStatus.BOOKED) {
            // Withdrawal possible before/after booking, but review needed.
            // Allow request even if booked, manager decides.
            System.out.println("Service INFO: Withdrawal requested for a BOOKED application " + app.getApplicationId()
                    + ". Manager review required.");
        }
        if (app.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
            throw new ApplicationException(
                    "Application " + app.getApplicationId()
                            + " is already UNSUCCESSFUL. Cannot withdraw. You may apply again.");
        }

        // Set withdrawal request date
        app.setRequestedWithdrawalDate(LocalDate.now());
        applicationRepo.save(app);
        System.out.println("Service: Withdrawal requested for application " + app.getApplicationId());
        return true;
    }

    @Override
    public boolean reviewApplication(HDBManager manager, String applicationId, boolean approve)
            throws ApplicationException { // <<< ADD throws ApplicationException >>>
        Objects.requireNonNull(manager, "Manager cannot be null");
        Objects.requireNonNull(applicationId, "Application ID cannot be null");

        Application application = applicationRepo.findById(applicationId);
        if (application == null) {
            throw new ApplicationException("Application ID " + applicationId + " not found.");
        }
        Project project = projectRepo.findById(application.getProjectId());
        if (project == null) {
            throw new ApplicationException("Associated project " + application.getProjectId()
                    + " not found for application " + applicationId + ". Cannot review.");
        }

        // Authorization Check
        if (!project.getManagerNric().equals(manager.getNric())) {
            throw new ApplicationException("Manager " + manager.getNric()
                    + " does not have permission to review applications for project " + project.getProjectId());
        }

        // State Check
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ApplicationException("Application " + applicationId + " is not in PENDING state (Current state: "
                    + application.getStatus() + "). Cannot review.");
        }
        // Check if withdrawal requested - manager should review withdrawal first
        if (application.getRequestedWithdrawalDate() != null) {
            throw new ApplicationException("Application " + applicationId
                    + " has a pending withdrawal request. Please review withdrawal first.");
        }

        if (approve) {
            FlatType requestedType = application.getPreferredFlatType();
            if (requestedType == null) {
                // Should not happen if submitApplication logic is correct, but safeguard
                application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                applicationRepo.save(application);
                throw new ApplicationException(
                        "Cannot approve application " + applicationId + ": Preferred flat type is missing.");
            }
            ProjectFlatInfo flatInfo = project.getFlatInfo(requestedType);
            if (flatInfo == null || flatInfo.getRemainingUnits() <= 0) {
                application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                applicationRepo.save(application);
                System.err.println("Service: Application " + applicationId + " auto-rejected. No remaining "
                        + requestedType + " units for project " + project.getProjectId());
                throw new ApplicationException("Cannot approve application " + applicationId + ". No remaining "
                        + requestedType + " units available. Application rejected.");
            }

            // Decrease unit count upon HDBManager's approval
            boolean unitsDecremented = flatInfo.decreaseRemainingUnits(); 

            if (!unitsDecremented) {
                // This might happen in a concurrent scenario if units hit 0 between check and decrease.
                // Or if decreaseRemainingUnits has internal logic preventing decrease below 0 (which it should).
                application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                applicationRepo.save(application);
                 System.err.println("Service Error: Failed to decrease remaining units for " + requestedType + " for application " + applicationId + ". Application rejected.");
                throw new ApplicationException("Failed to reserve unit due to availability change. Application rejected.");
            }

            // If units were successfully decremented:
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            applicationRepo.save(application); // Save updated application status FIRST
            projectRepo.save(project);       // THEN save the project with updated unit count
            System.out.println("Service: Application " + applicationId + " approved by manager " + manager.getNric()
                  + ". Remaining " + requestedType + " units for project " + project.getProjectId() + ": " + flatInfo.getRemainingUnits());
        }

        else { // Reject
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Service: Application " + applicationId + " rejected by manager " + manager.getNric());
        }

        applicationRepo.save(application);
        return true;
    }

    @Override
    public boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve)
            throws ApplicationException {
        Objects.requireNonNull(manager, "Manager cannot be null");
        Objects.requireNonNull(applicationId, "Application ID cannot be null");

        Application application = applicationRepo.findById(applicationId);
        if (application == null) {
            throw new ApplicationException("Application ID " + applicationId + " not found.");
        }
        Project project = projectRepo.findById(application.getProjectId());
        if (project == null) {
            throw new ApplicationException("Associated project " + application.getProjectId()
                    + " not found for application " + applicationId + ". Cannot review withdrawal.");
        }

        // Authorization Check
        if (!project.getManagerNric().equals(manager.getNric())) {
            throw new ApplicationException("Manager " + manager.getNric()
                    + " does not have permission to review withdrawals for project " + project.getProjectId());
        }

        // State Check
        if (application.getRequestedWithdrawalDate() == null) {
            throw new ApplicationException("Application " + applicationId + " has no pending withdrawal request.");
        }

        if (approve) {
            // Business Rule: If status was SUCCESSFUL, the number of flat units do NOT
            // change. It only changes when Manager affirms booking.

            // Final state after approved withdrawal is UNSUCCESSFUL
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            application.setRequestedWithdrawalDate(null); // Clear the request flag
            applicationRepo.save(application);
            System.out.println("Service: Withdrawal request for application " + applicationId
                    + " approved. Application marked unsuccessful.");

        } else { // Reject withdrawal request
            // Just clear the request flag, status remains as it was (PENDING or SUCCESSFUL)
            application.setRequestedWithdrawalDate(null);
            applicationRepo.save(application);
            System.out.println("Service: Withdrawal request for application " + applicationId
                    + " rejected. Application status remains " + application.getStatus());
        }
        return true;
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
