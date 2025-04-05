package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import java.time.LocalDate;

public class Application {
    private String applicationId;
    private String applicantNric;
    private String projectId;
    private LocalDate submissionDate;
    private ApplicationStatus status = ApplicationStatus.PENDING;
    private LocalDate requestedWithdrawalDate; // Can be null
    private FlatType preferredFlatType; // Can be null

    public Application(String applicationId, String applicantNric, String projectId, LocalDate submissionDate) {
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectId = projectId;
        this.submissionDate = submissionDate;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicantNric() {
        return applicantNric;
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

    public LocalDate getRequestedWithdrawalDate() {
        return requestedWithdrawalDate;
    }

    public void setRequestedWithdrawalDate(LocalDate requestedWithdrawalDate) {
        this.requestedWithdrawalDate = requestedWithdrawalDate;
    }

    public FlatType getPreferredFlatType() {
        return preferredFlatType;
    }

    public void setPreferredFlatType(FlatType preferredFlatType) {
        this.preferredFlatType = preferredFlatType;
    }
}
