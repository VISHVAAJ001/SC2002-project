package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.repository.IRepository;

import java.util.List;

public interface IApplicationRepository extends IRepository<Application, String> {
    Application findByApplicantNric(String nric); // Nullable

    List<Application> findByProjectId(String projectId);

    List<Application> findByStatus(ApplicationStatus status);
}
