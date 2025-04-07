package com.ntu.fdae.group1.bto.repository.user; 

import com.ntu.fdae.group1.bto.models.user.OfficerRegistration;
import com.ntu.fdae.group1.bto.repository.IRepository; 

import java.util.List;

/**
 * Repository interface for managing OfficerRegistration entities.
 */
public interface IOfficerRegistrationRepository extends IRepository<OfficerRegistration, String> {

    List<OfficerRegistration> findByOfficerNric(String nric);

    List<OfficerRegistration> findByProjectId(String projectId);

}