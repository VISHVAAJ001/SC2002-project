package com.ntu.fdae.group1.bto.services.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Collectors;
import java.util.Objects;
import java.util.Arrays;
import java.util.Map;
import java.time.LocalDate;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository;
import com.ntu.fdae.group1.bto.repository.project.IOfficerRegistrationRepository;
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.utils.*;
import com.ntu.fdae.group1.bto.models.project.*;

public class OfficerRegistrationService implements IOfficerRegistrationService {

    private final IOfficerRegistrationRepository registrationRepo;
    private final IProjectRepository projectRepo;
    private final IApplicationRepository applicationRepo;
    private final IEligibilityService eligibilityService;

    public OfficerRegistrationService(IOfficerRegistrationRepository registrationRepo,
            IProjectRepository projectRepo,
            IApplicationRepository applicationRepo,
            IEligibilityService eligibilityService) {
        this.registrationRepo = Objects.requireNonNull(registrationRepo,
                "Officer Registration Repository cannot be null");
        this.projectRepo = Objects.requireNonNull(projectRepo, "Project Repository cannot be null");
        this.applicationRepo = Objects.requireNonNull(applicationRepo, "Application Repository cannot be null");
        this.eligibilityService = Objects.requireNonNull(eligibilityService, "Eligibility Service cannot be null");
    }

    @Override
    public OfficerRegistration requestProjectRegistration(HDBOfficer officer, String projectId)
            throws RegistrationException {
        Objects.requireNonNull(officer, "Officer cannot be null");
        Objects.requireNonNull(projectId, "Project ID cannot be null");
        Project project = projectRepo.findById(projectId);
        if (project == null)
            throw new RegistrationException("Project with ID " + projectId + " not found.");
        boolean alreadyRegistered = registrationRepo.findByOfficerNric(officer.getNric()).stream()
                .anyMatch(reg -> reg.getProjectId().equals(projectId));
        if (alreadyRegistered)
            throw new RegistrationException(
                    "Officer " + officer.getNric() + " is already registered for project " + projectId);
        boolean isEligible = eligibilityService.canOfficerRegister(officer, project,
                registrationRepo.findAll().values(), applicationRepo.findAll().values());
        if (!isEligible)
            throw new RegistrationException(
                    "Officer " + officer.getNric() + " is not eligible to register for project " + projectId);
        String registrationId = IdGenerator.generateOfficerRegId();
        OfficerRegistration newRegistration = new OfficerRegistration(registrationId, officer.getNric(), projectId,
                LocalDate.now());
        registrationRepo.save(newRegistration);
        System.out.println("Service: Officer registration request submitted successfully for Officer "
                + officer.getNric() + " on project " + projectId);
        return newRegistration;
    }

    @Override
    public boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve)
            throws RegistrationException {
        Objects.requireNonNull(manager, "Manager cannot be null");
        Objects.requireNonNull(registrationId, "Registration ID cannot be null");
        OfficerRegistration registration = registrationRepo.findById(registrationId);
        if (registration == null)
            throw new RegistrationException("Registration ID " + registrationId + " not found.");
        Project project = projectRepo.findById(registration.getProjectId());
        if (project == null)
            throw new RegistrationException("Associated project " + registration.getProjectId()
                    + " not found for registration " + registrationId);
        if (!project.getManagerNric().equals(manager.getNric()))
            throw new RegistrationException(
                    "Manager " + manager.getNric() + " does not have permission for project " + project.getProjectId());
        if (registration.getStatus() != OfficerRegStatus.PENDING)
            throw new RegistrationException("Registration " + registrationId + " is not PENDING.");

        if (approve) {
            if (project.getApprovedOfficerNrics().size() >= project.getMaxOfficerSlots()) {
                registration.setStatus(OfficerRegStatus.REJECTED);
                registrationRepo.save(registration);
                System.err.println("Service: Registration " + registrationId + " auto-rejected due to max slots.");
                throw new RegistrationException("Cannot approve registration " + registrationId
                        + ". Maximum officer slots reached. Registration rejected.");
            } else {
                registration.setStatus(OfficerRegStatus.APPROVED);
                // Ensure Project class has this method implemented correctly
                boolean added = project.addApprovedOfficer(registration.getOfficerNric());
                if (added) {
                    projectRepo.save(project);
                    System.out.println("Service: Registration " + registrationId + " approved.");
                } else {
                    registration.setStatus(OfficerRegStatus.REJECTED); // Revert if add failed
                    System.err.println("Service Error: Failed add officer " + registration.getOfficerNric()
                            + " to project list. Registration rejected.");
                    throw new RegistrationException(
                            "Internal error adding approved officer to project list. Registration rejected.");
                }
            }
        } else {
            registration.setStatus(OfficerRegStatus.REJECTED);
            System.out.println("Service: Registration " + registrationId + " rejected.");
        }
        registrationRepo.save(registration); // Save final registration status
        return true;
    }

    /**
     * Gets the current registration status for a specific officer and project.
     *
     * @param officer   The HDBOfficer whose registration status is needed.
     * @param projectId The ID of the project in question.
     * @return The OfficerRegStatus if a registration exists, otherwise null.
     * @throws DataAccessException if an error occurs during data retrieval.
     */
    @Override
    public OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId) throws DataAccessException {
        // 1. Input Validation
        if (officer == null || officer.getNric() == null || officer.getNric().trim().isEmpty()) {
            System.err.println("Warning: getRegistrationStatus called with invalid officer details.");
            return null; // Cannot find status without officer NRIC
        }
        if (projectId == null || projectId.trim().isEmpty()) {
            System.err.println("Warning: getRegistrationStatus called with invalid projectId.");
            return null; // Cannot find status without project ID
        }

        try {
            List<OfficerRegistration> officerRegistrations = registrationRepo.findByOfficerNric(officer.getNric());

            if (officerRegistrations == null || officerRegistrations.isEmpty()) {
                return null; // Officer has no registrations at all
            }

            // Filter the list for the specific project ID
            Optional<OfficerRegistration> specificRegistration = officerRegistrations.stream()
                    .filter(reg -> projectId.equals(reg.getProjectId()))
                    .findFirst(); // There should only be one registration per officer per

            return specificRegistration.map(OfficerRegistration::getStatus).orElse(null);

        } catch (DataAccessException e) {
            System.err.println("Data access error fetching registration status for officer " + officer.getNric()
                    + " and project " + projectId + ": " + e.getMessage());
            throw e; // Re-throw
        } catch (Exception e) {
            System.err.println("Unexpected error fetching registration status for officer " + officer.getNric()
                    + " and project " + projectId + ": " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while fetching registration status.", e);
        }
    }

    @Override
    public List<OfficerRegistration> getPendingRegistrations() {
        // Ensure findAll() doesn't return null map before streaming
        Map<String, OfficerRegistration> regMap = registrationRepo.findAll();
        if (regMap == null || regMap.isEmpty())
            return Arrays.asList();
        return regMap.values().stream()
                .filter(reg -> reg.getStatus() == OfficerRegStatus.PENDING)
                .collect(Collectors.toList());
    }

    @Override
    public List<OfficerRegistration> getRegistrationsByProject(String projectId) {
        if (projectId == null || projectId.trim().isEmpty())
            return Arrays.asList();
        return registrationRepo.findByProjectId(projectId);
    }

    @Override
    public List<OfficerRegistration> getRegistrationsByOfficer(String officerNric) {
        return registrationRepo.findAll().values().stream() // Adjust if findAll() returns List
                .filter(reg -> reg.getOfficerNric().equals(officerNric))
                .collect(Collectors.toList());
    }
}