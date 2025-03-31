package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.repository.IRepository;

import java.util.List;

public interface IOfficerRegistrationRepository extends IRepository<OfficerRegistration, String> {
    List<OfficerRegistration> findByOfficerNric(String nric);

    List<OfficerRegistration> findByProjectId(String projectId);
}
