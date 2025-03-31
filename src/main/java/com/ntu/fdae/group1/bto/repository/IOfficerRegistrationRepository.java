package com.ntu.fdae.group1.bto.repository;

import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;

import java.util.List;

public interface IOfficerRegistrationRepository extends IRepository<OfficerRegistration, String> {
    List<OfficerRegistration> findByOfficerNric(String nric);

    List<OfficerRegistration> findByProjectId(String projectId);
}
