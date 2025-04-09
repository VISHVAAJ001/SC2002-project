package com.ntu.fdae.group1.bto.services.project;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Arrays;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
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

        Objects.requireNonNull(applicant, "Applicant cannot be null");
        Objects.requireNonNull(projectId, "Project ID cannot be null");
        // preferredFlatType can be null based on UML

        // 1. Check if Applicant already has an existing application (able to apply for 1 project only)
        Application existingApp = applicationRepo.findByApplicantNric(applicant.getNric());
        if (existingApp != null &&
            existingApp.getStatus() != ApplicationStatus.UNSUCCESSFUL) {
            throw new ApplicationException("Applicant " + applicant.getNric() + " already has an active application (ID: " + existingApp.getApplicationId() + ", Status: " + existingApp.getStatus() + "). Cannot apply for multiple projects.");
        }

        // 2. Check if Project exists and is accepting applications
        Project project = projectRepo.findById(projectId);
        if (project == null) {
            throw new ApplicationException("Project with ID " + projectId + " not found.");
        }
        LocalDate today = LocalDate.now();
        if (project.getOpeningDate() == null || project.getClosingDate() == null ||
            today.isBefore(project.getOpeningDate()) || today.isAfter(project.getClosingDate())) {
            throw new ApplicationException("Project " + projectId + " is not currently accepting applications.");
        }
        // Check visibility? Assume that they can only view and apply visible projects
        if (!project.isVisible()) {
             throw new ApplicationException("Project " + projectId + " is not currently visible or open for application.");
        }

        // 3. Check Applicant Eligibility for the Project 
        if (!eligibilityService.canApplicantApply(applicant, project)) {
            throw new ApplicationException("Applicant " + applicant.getNric() + " is not eligible for project " + projectId + " based on age/marital status/flat type availability.");
        }

        // 4. Validate preferredFlatType against eligibility rules and project offerings
        if (preferredFlatType != null) {
            if (applicant.getMaritalStatus() == MaritalStatus.SINGLE && preferredFlatType != FlatType.TWO_ROOM) {
                throw new ApplicationException("Single applicants can only apply for TWO_ROOM flats. Preferred type was " + preferredFlatType);
            }
            if (!project.getFlatTypes().containsKey(preferredFlatType)) {
                 throw new ApplicationException("Project " + projectId + " does not offer the preferred flat type: " + preferredFlatType);
            }
        } else {
            // If null, check if only one type is eligible/available and assign it? Or require it?
            // Applicants choose ONLY if Married (2 or 3 room). Singles only have 2-room option.
            if (applicant.getMaritalStatus() == MaritalStatus.SINGLE) {
                 if (!project.getFlatTypes().containsKey(FlatType.TWO_ROOM)) {
                      throw new ApplicationException("Project " + projectId + " does not offer TWO_ROOM flats, which is required for single applicants.");
                 }
                 preferredFlatType = FlatType.TWO_ROOM; // Auto-assign for single
            } else {
                 throw new ApplicationException("Married applicants must specify a preferred flat type (TWO_ROOM or THREE_ROOM).");
            }
        }

        // 5. Create and Save Application
        String appId = IdGenerator.generateApplicationId(); 
        Application newApplication = new Application(appId, applicant.getNric(), projectId, LocalDate.now());
        newApplication.setPreferredFlatType(preferredFlatType); // Set the determined preference
        // Status defaults to PENDING in Application constructor

        applicationRepo.save(newApplication);
        System.out.println("Service: Application " + appId + " submitted successfully for " + applicant.getNric());
        return newApplication;
    }

    @Override
    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        Objects.requireNonNull(applicant, "Applicant cannot be null");

        Application app = applicationRepo.findByApplicantNric(applicant.getNric());
        if (app == null) {
            throw new ApplicationException("No active application found for applicant " + applicant.getNric() + " to withdraw.");
        }

        // Check if already requested or in a final state
        if (app.getRequestedWithdrawalDate() != null) {
            throw new ApplicationException("Withdrawal has already been requested for application " + app.getApplicationId());
        }
         if (app.getStatus() == ApplicationStatus.BOOKED) {
            // Withdrawal possible before/after booking, but review needed.
            // Allow request even if booked, manager decides.
            System.out.println("Service INFO: Withdrawal requested for a BOOKED application " + app.getApplicationId() + ". Manager review required.");
        }
         if (app.getStatus() == ApplicationStatus.UNSUCCESSFUL) {
             // Arguably cannot withdraw an already unsuccessful app, but let's allow request for manager clarity.
             System.out.println("Service INFO: Withdrawal requested for an UNSUCCESSFUL application " + app.getApplicationId() + ". Manager review required.");
         }

        // Set withdrawal request date
        app.setRequestedWithdrawalDate(LocalDate.now());
        applicationRepo.save(app);
        System.out.println("Service: Withdrawal requested for application " + app.getApplicationId());
        return true;
    }

    @Override
    public boolean reviewApplication(HDBManager manager, String applicationId, boolean approve) throws ApplicationException { // <<< ADD throws ApplicationException >>>
        Objects.requireNonNull(manager, "Manager cannot be null");
        Objects.requireNonNull(applicationId, "Application ID cannot be null");

        Application application = applicationRepo.findById(applicationId);
        if (application == null) {
            throw new ApplicationException("Application ID " + applicationId + " not found.");
        }
        Project project = projectRepo.findById(application.getProjectId());
        if (project == null) {
            throw new ApplicationException("Associated project " + application.getProjectId() + " not found for application " + applicationId + ". Cannot review.");
        }

        // Authorization Check
        if (!project.getManagerNric().equals(manager.getNric())) {
            throw new ApplicationException("Manager " + manager.getNric() + " does not have permission to review applications for project " + project.getProjectId());
        }

        // State Check
        if (application.getStatus() != ApplicationStatus.PENDING) {
            throw new ApplicationException("Application " + applicationId + " is not in PENDING state (Current state: " + application.getStatus() + "). Cannot review.");
        }
        // Check if withdrawal requested - manager should review withdrawal first
        if (application.getRequestedWithdrawalDate() != null) {
            throw new ApplicationException("Application " + applicationId + " has a pending withdrawal request. Please review withdrawal first.");
        }

        if (approve) {
            FlatType requestedType = application.getPreferredFlatType();
            if (requestedType == null) {
                 // Should not happen if submitApplication logic is correct, but safeguard
                 application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                 applicationRepo.save(application);
                 throw new ApplicationException("Cannot approve application " + applicationId + ": Preferred flat type is missing.");
            }
            ProjectFlatInfo flatInfo = project.getFlatInfo(requestedType); 
            if (flatInfo == null || flatInfo.getRemainingUnits() <= 0) {
                application.setStatus(ApplicationStatus.UNSUCCESSFUL);
                applicationRepo.save(application);
                System.err.println("Service: Application " + applicationId + " auto-rejected. No remaining " + requestedType + " units for project " + project.getProjectId());
                throw new ApplicationException("Cannot approve application " + applicationId + ". No remaining " + requestedType + " units available. Application rejected.");
            }
            application.setStatus(ApplicationStatus.SUCCESSFUL);
            System.out.println("Service: Application " + applicationId + " approved by manager " + manager.getNric());
        }
       
        else { // Reject
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            System.out.println("Service: Application " + applicationId + " rejected by manager " + manager.getNric());
        }

        applicationRepo.save(application); 
        return true;
    }

   @Override
    public boolean reviewWithdrawal(HDBManager manager, String applicationId, boolean approve) throws ApplicationException { // <<< ADD throws ApplicationException >>>
         Objects.requireNonNull(manager, "Manager cannot be null");
         Objects.requireNonNull(applicationId, "Application ID cannot be null");

         Application application = applicationRepo.findById(applicationId);
         if (application == null) {
             throw new ApplicationException("Application ID " + applicationId + " not found.");
         }
         Project project = projectRepo.findById(application.getProjectId());
          if (project == null) {
             throw new ApplicationException("Associated project " + application.getProjectId() + " not found for application " + applicationId + ". Cannot review withdrawal.");
         }

         // Authorization Check
         if (!project.getManagerNric().equals(manager.getNric())) {
             throw new ApplicationException("Manager " + manager.getNric() + " does not have permission to review withdrawals for project " + project.getProjectId());
         }

         // State Check
         if (application.getRequestedWithdrawalDate() == null) {
             throw new ApplicationException("Application " + applicationId + " has no pending withdrawal request.");
         }

         if (approve) {
             // Business Rule: If status was SUCCESSFUL, the number of flat units do NOT change. It only changes when Manager affirms booking.
             
             // Final state after approved withdrawal is UNSUCCESSFUL
             application.setStatus(ApplicationStatus.UNSUCCESSFUL);
             application.setRequestedWithdrawalDate(null); // Clear the request flag
             applicationRepo.save(application);
             System.out.println("Service: Withdrawal request for application " + applicationId + " approved. Application marked unsuccessful.");

         } else { // Reject withdrawal request
             // Just clear the request flag, status remains as it was (PENDING or SUCCESSFUL)
             application.setRequestedWithdrawalDate(null);
             applicationRepo.save(application);
             System.out.println("Service: Withdrawal request for application " + applicationId + " rejected. Application status remains " + application.getStatus());
         }
         return true;
    }

    @Override
    public Application getApplicationForUser(String applicantNRIC) {
         if (applicantNRIC == null || applicantNRIC.trim().isEmpty()) {
            return null;
        }
        // Repository method handles finding by NRIC
        return applicationRepo.findByApplicantNric(applicantNRIC);
    }

    @Override
    public List<Application> getApplicationsByProject(String projectId) {
         if (projectId == null || projectId.trim().isEmpty()) {
             return Arrays.asList(); // Return empty list
        }
        // Repository method handles finding by Project ID
        return applicationRepo.findByProjectId(projectId);
    }

    @Override
    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
         if (status == null) {
             return Arrays.asList(); // Return empty list
         }
         // Find all and filter by status
         Map<String, Application> appMap = applicationRepo.findAll();
         if (appMap == null || appMap.isEmpty()) {
             return Arrays.asList();
         }
         return appMap.values().stream()
                .filter(app -> app.getStatus() == status)
                .collect(Collectors.toList());
    }

}


