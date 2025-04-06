package com.ntu.fdae.group1.bto.services.application;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.user.Applicant;

import java.util.List;

/**
 * Interface defining operations for managing BTO applications.
 */
public interface IApplicationService {

    Application submitApplication(Applicant applicant, String projectId, FlatType flatType)
            throws ApplicationException;

    boolean requestWithdrawal(Applicant applicant) throws ApplicationException;

    boolean reviewApplication(String applicationId, boolean approve) throws ApplicationException;

    boolean reviewWithdrawal(String applicationId, boolean approve) throws ApplicationException;

    List<Application> getApplicationsByProject(String applicantNRIC) throws ApplicationException;

    List<Application> getApplicationsByStatus(ApplicationStatus status) throws ApplicationException;

    Application getApplicationForUser(String applicantNric);
}
