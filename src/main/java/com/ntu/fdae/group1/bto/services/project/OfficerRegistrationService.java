package com.ntu.fdae.group1.bto.services.project;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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

public class OfficerRegistrationService implements IOfficerRegistrationService {
    private final IOfficerRegistrationRepository officerRepository;
    private final IProjectRepository projectRepository;
    private final IApplicationRepository applicationRepository;
    private final IEligibilityService eligibilityService;

    public OfficerRegistrationService(IOfficerRegistrationRepository regRepo, IProjectRepository projRepo,
            IApplicationRepository appRepo, IEligibilityService eligSvc) {
        this.officerRepository = regRepo;
        this.projectRepository = projRepo;
        this.applicationRepository = appRepo;
        this.eligibilityService = eligSvc;
    }

    @Override
    public OfficerRegistration requestProjectRegistration(HDBOfficer officer, String projectId)
            throws RegistrationException {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'requestProjectRegistration'");
    }

    @Override
    public boolean reviewRegistration(HDBManager manager, String registrationId, boolean approve) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reviewRegistration'");
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
            List<OfficerRegistration> officerRegistrations = officerRepository.findByOfficerNric(officer.getNric());

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
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getPendingRegistrations'");
    }

    @Override
    public List<OfficerRegistration> getRegistrationsByProject(String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRegistrationsByProject'");
    }

    @Override
    public List<OfficerRegistration> getRegistrationsByOfficer(String officerNric) {
        return officerRepository.findAll().values().stream() // Adjust if findAll() returns List
                .filter(reg -> reg.getOfficerNric().equals(officerNric))
                .collect(Collectors.toList());
    }
}
