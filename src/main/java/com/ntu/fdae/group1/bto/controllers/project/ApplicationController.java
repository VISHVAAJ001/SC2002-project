package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.Applicant;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.services.booking.IDataManager;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;

import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class ApplicationController {
    private Map<String, Application> applicationRepo;
    private Map<String, Project> projectRepo;
    private IDataManager dataManager;
    private IEligibilityService eligibilityService;

    public ApplicationController(Map<String, Application> appMap, Map<String, Project> projMap, IDataManager dataMgr,
            IEligibilityService eligSvc) {
        this.applicationRepo = appMap;
        this.projectRepo = projMap;
        this.dataManager = dataMgr;
        this.eligibilityService = eligSvc;
    }

    public boolean submitApplication(Applicant applicant, String projectId) throws ApplicationException {
        // if (!eligibilityService.canApplicantApply(applicant,
        // projectRepo.get(projectId))) {
        // throw new ApplicationException("Applicant is not eligible to apply for
        // project.");
        // }

        // Application application = new Application(applicationId, applicant.getNric(),
        // projectId);
        // applicationRepo.put(applicantId, application);
        // dataManager.saveApplications(applicationRepo);
        return true;
    }

    public boolean requestWithdrawal(Applicant applicant) throws ApplicationException {
        // Application application = getApplicationForUser(applicant.getNric());

        // // no appli / appli alr withdrawn
        // if (application == null || application.getStatus() ==
        // ApplicationStatus.WITHDRAWN) {
        // throw new ApplicationException("No active application found.");
        // }

        // application.setStatus(ApplicationStatus.WITHDRAWN);
        // dataManager.saveApplications(applicationRepo);
        return true;
    }

    public boolean approveApplication(HDBManager manager, String applicationId) {
        // Application application = applicationRepo.get(applicationId);

        // if (application != null) {
        // application.setStatus(ApplicationStatus.SUCCESSFUL);
        // dataManager.saveApplications(applicationRepo);
        // return true;
        // }
        return false;
    }

    public boolean rejectApplication(HDBManager manager, String applicationId) {
        Application application = applicationRepo.get(applicationId);

        if (application != null) {
            application.setStatus(ApplicationStatus.UNSUCCESSFUL);
            dataManager.saveApplications(applicationRepo);
            return true;
        }
        return false;
    }

    public boolean approveWithdrawal(Applicant applicant) {
        // Application application = applicationRepo.get(applicationId);

        // // check if appli exist + applicant req for withdrawal
        // if (application == null || application.getStatus() !=
        // ApplicationStatus.WITHDRAWN) {
        // return false;
        // }

        // applicationRepo.remove(applicationId);
        // dataManager.saveApplications(applicationRepo);
        return true;
    }

    public boolean rejectWithdrawal(Applicant applicant) {
        // Application application = applicationRepo.get(applicationId);

        // // check if appli exist + applicant req for withdrawal
        // if (application == null || application.getStatus() !=
        // ApplicationStatus.WITHDRAWN) {
        // return false;
        // }

        // // set status back to pending
        // application.setStatus(ApplicationStatus.PENDING);
        // dataManager.saveApplications(applicationRepo);
        return true;
    }

    public Application getApplicationForUser(String applicantNRIC) {
        return null;
    }

    public List<Application> getApplicationsByProject(String projectId) {
        return null;
    }

    public List<Application> getApplicationsByStatus(ApplicationStatus status) {
        return null;
    }

}
