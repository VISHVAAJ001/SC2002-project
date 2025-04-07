package com.ntu.fdae.group1.bto.services.user;

import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.OfficerRegistration;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IOfficerRegistrationRepository; // Corrected path
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.Map;

public class OfficerRegistrationService implements IOfficerRegistrationService {

    private final IOfficerRegistrationRepository registrationRepo;
    private final IProjectRepository projectRepo;
    private final IApplicationRepository applicationRepo;
    private final IEligibilityService eligibilityService;

    public OfficerRegistrationService(IOfficerRegistrationRepository registrationRepo,
                                      IProjectRepository projectRepo,
                                      IApplicationRepository applicationRepo,
                                      IEligibilityService eligibilityService) {
        this.registrationRepo = Objects.requireNonNull(registrationRepo, "Officer Registration Repository cannot be null");
        this.projectRepo = Objects.requireNonNull(projectRepo, "Project Repository cannot be null");
        this.applicationRepo = Objects.requireNonNull(applicationRepo, "Application Repository cannot be null");
        this.eligibilityService = Objects.requireNonNull(eligibilityService, "Eligibility Service cannot be null");
    }

    @Override
    public OfficerRegistration requestProjectRegistration(HDBOfficer officer, String projectId) throws RegistrationException {
         Objects.requireNonNull(officer, "Officer cannot be null");
         Objects.requireNonNull(projectId, "Project ID cannot be null");
         Project project = projectRepo.findById(projectId);
         if (project == null) throw new RegistrationException("Project with ID " + projectId + " not found.");
         boolean alreadyRegistered = registrationRepo.findByOfficerNric(officer.getNric()).stream().anyMatch(reg -> reg.getProjectId().equals(projectId));
         if(alreadyRegistered) throw new RegistrationException("Officer " + officer.getNric() + " is already registered for project " + projectId);
         boolean isEligible = eligibilityService.checkOfficerRegistrationEligibility(officer, project, registrationRepo.findAll().values(), applicationRepo.findAll().values());
         if (!isEligible) throw new RegistrationException("Officer " + officer.getNric() + " is not eligible to register for project " + projectId);
         String registrationId = IdGenerator.generateOfficerRegId();
         OfficerRegistration newRegistration = new OfficerRegistration(registrationId, officer.getNric(), projectId,LocalDate.now());
         registrationRepo.save(newRegistration);
         System.out.println("Service: Officer registration request submitted successfully for Officer " + officer.getNric() + " on project " + projectId);
         return newRegistration;
    }

    @Override
    public boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve) throws RegistrationException {
        Objects.requireNonNull(manager, "Manager cannot be null");
        Objects.requireNonNull(registrationId, "Registration ID cannot be null");
        OfficerRegistration registration = registrationRepo.findById(registrationId);
        if (registration == null) throw new RegistrationException("Registration ID " + registrationId + " not found.");
        Project project = projectRepo.findById(registration.getProjectId());
        if (project == null) throw new RegistrationException("Associated project " + registration.getProjectId() + " not found for registration " + registrationId);
        if (!project.getManagerNric().equals(manager.getNric())) throw new RegistrationException("Manager " + manager.getNric() + " does not have permission for project " + project.getProjectId());
        if (registration.getStatus() != OfficerRegStatus.PENDING) throw new RegistrationException("Registration " + registrationId + " is not PENDING.");

        if (approve) {
            if (project.getApprovedOfficerNrics().size() >= project.getMaxOfficerSlots()) {
                registration.setStatus(OfficerRegStatus.REJECTED);
                registrationRepo.save(registration);
                System.err.println("Service: Registration " + registrationId + " auto-rejected due to max slots.");
                throw new RegistrationException("Cannot approve registration " + registrationId + ". Maximum officer slots reached. Registration rejected.");
            } else {
                registration.setStatus(OfficerRegStatus.APPROVED);
                // Ensure Project class has this method implemented correctly
                boolean added = project.addApprovedOfficer(registration.getOfficerNric());
                if (added) {
                    projectRepo.save(project);
                    System.out.println("Service: Registration " + registrationId + " approved.");
                } else {
                    registration.setStatus(OfficerRegStatus.REJECTED); // Revert if add failed
                    System.err.println("Service Error: Failed add officer " + registration.getOfficerNric() + " to project list. Registration rejected.");
                    throw new RegistrationException("Internal error adding approved officer to project list. Registration rejected.");
                }
            }
        } else {
            registration.setStatus(OfficerRegStatus.REJECTED);
             System.out.println("Service: Registration " + registrationId + " rejected.");
        }
        registrationRepo.save(registration); // Save final registration status
        return true;
    }

    @Override
    public OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId) {
        if (officer == null || projectId == null) return null;
        return registrationRepo.findByOfficerNric(officer.getNric())
                .stream()
                .filter(reg -> projectId.equals(reg.getProjectId()))
                .map(OfficerRegistration::getStatus)
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<OfficerRegistration> getPendingRegistrations() {
         // Ensure findAll() doesn't return null map before streaming
         Map<String, OfficerRegistration> regMap = registrationRepo.findAll();
         if (regMap == null || regMap.isEmpty()) return List.of();
         return regMap.values().stream()
                .filter(reg -> reg.getStatus() == OfficerRegStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public List<OfficerRegistration> getRegistrationsByProject(String projectId) {
        // ... (Implementation from previous correct version) ...
         if (projectId == null || projectId.isBlank()) return List.of();
         return registrationRepo.findByProjectId(projectId);
    }
}