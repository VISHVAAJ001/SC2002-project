package com.ntu.fdae.group1.bto.services.project;

import java.util.List;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.repository.project.IOfficerRegistrationRepository;

public class OfficerRegistrationService implements IOfficerRegistrationService {
    private final IOfficerRegistrationRepository officerRepository;

    public OfficerRegistrationService(IOfficerRegistrationRepository officerRepository) {
        this.officerRepository = officerRepository;
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

    @Override
    public OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getRegistrationStatus'");
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

}
