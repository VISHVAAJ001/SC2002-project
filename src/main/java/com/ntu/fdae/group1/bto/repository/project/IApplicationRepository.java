package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.models.project.Application;
import java.util.List;

public interface IApplicationRepository {
    void save(Application application);
    Application findById(String applicationId);
    Application findByApplicantNric(String applicantNRIC);
    List<Application> findByProjectId(String projectId);
    List<Application> findByStatus(ApplicationStatus status);
}
