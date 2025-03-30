package com.ntu.fdae.group1.bto.controllers.user;

import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.models.*;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.booking.IBookingService;
import com.ntu.fdae.group1.bto.services.booking.IEligibilityService;
import com.ntu.fdae.group1.bto.services.booking.IReceiptService;
import com.ntu.fdae.group1.bto.services.booking.IDataManager;

import java.util.Map;
import java.util.UUID;
import java.util.List;

public class OfficerController {
    private Map<String, Registration> registrationRepo;
    private Map<String, Project> projectRepo;
    private Map<String, User> userRepo;
    private IDataManager dataManager;
    private IEligibilityService eligibilityService;
    private IBookingService bookingService;
    private IReceiptService receiptService;

    public OfficerController(Map<String, Registration> regMap, Map<String, Project> projMap, Map<String, User> userMap, IDataManager dataMgr, IEligibilityService eligSvc, IBookingService bookSvc, IReceiptService receiptSvc){
        this.registrationRepo = regMap;
        this.projectRepo = projMap;
        this.userRepo = userMap;
        this.dataManager = dataMgr;
        this.eligibilityService = eligSvc;
        this.bookingService = bookSvc;
        this.receiptService = receiptSvc;
    }

    public boolean requestProjectRegistration(HDBOfficer officer, String projectId) throws RegistrationException{
        if (!projectRepo.containsKey(projectId)){
            throw new RegistrationException("Project not found.");

            String registrationId = UUID.randomUUID().toString();
            OfficerRegistration registration = new OfficerRegistration();
            registrationRepo.put(registrationId, registration);
            dataManager.saveOfficerRegistrations(registrationRepo);
            return true;
        }
    }

    public OfficerRegStatus getRegistrationStatus(HDBOfficer officer, String projectId){

    }

    public boolean approveRegistration(HDBManager manager, String registrationId){
        OfficerRegistration registration = registrationRepo.get(registrationId);
        
        if (registration != null && registration.getStatus() == OfficerRegistration.PENDING){
            registration.setStatus(OfficerRegistration.APPROVED);
            dataManager.saveOfficerRegistrations(registrationRepo);
            return true;
        }
        return false;
    }

    public boolean rejectRegistration(HDBManager manager, String registrationId){
        OfficerRegistration registration = registrationRepo.get(registrationId);

        if (registration != null && registration.getStatus() == OfficerRegistration.PENDING){
            registration.setStatus(OfficerRegistration.REJECTED);
            dataManager.saveOfficerRegistrations(registrationRepo);
            return true;
        }
        return false;
    }

    public String handleFlatBooking(HDBOfficer officer, String applicantNRIC, String flatType) throws BookingException{
        if (!userRepo.containsKey(applicantNRIC)){
            throw new BookingException("Applicant not found");
        }

        Applicant applicant = userRepo.get(applicantNRIC);

        
    }

    public List<OfficerRegistration> getPendingRegistrations(){

    }

    public List<OfficerRegistration> getRegistrationByProject(String projectId){

    }
    
}
