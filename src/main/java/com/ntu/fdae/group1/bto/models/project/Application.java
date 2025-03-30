package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import java.time.LocalDate;

public class Application {
    private String applicationId;
    private String applicantNRIC;
    private String projectId;
    private LocalDate submissionDate;
    private ApplicationStatus status = ApplicationStatus.PENDING;

    public Application(String applicationId, String applicantNRIC, String projectId, LocalDate submissionDate) {
        this.applicationId = applicationId;
        this.applicantNRIC = applicantNRIC;
        this.projectId = projectId;
        this.submissionDate = submissionDate;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicantNRIC() {
        return applicantNRIC;
    }

    public String getProjectId() {
        return projectId;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public ApplicationStatus getStatus() {
        return status;
    }

    public void setStatus(ApplicationStatus newStatus) {
        this.status = newStatus;
    }
}
