package com.ntu.fdae.group1.bto.repository;

import com.ntu.fdae.group1.bto.models.project.Application;

import java.util.List;

public interface IApplicationRepository extends IRepository<Application, String> {
    Application findByApplicantNric(String nric); // Nullable

    List<Application> findByProjectId(String projectId);
}
