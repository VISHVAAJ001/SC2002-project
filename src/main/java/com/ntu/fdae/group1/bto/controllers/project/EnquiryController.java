package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.models.project.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.booking.IDataManager;

import java.util.Map;
import java.util.List;
import java.time.LocalDate;
import java.util.UUID;
import java.util.stream.Collectors;

public class EnquiryController {
    private Map<String, Enquiry> enquiryRepo;
    private IDataManager dataManager;

    public EnquiryController(Map<String, Enquiry> enqMap, IDataManager dataMgr) {
        this.enquiryRepo = enqMap;
        this.dataManager = dataMgr;
    }

    public Enquiry createEnquiry(User user, String projectId, String content) {
        // String enquiryId = UUID.randomUUID().toString();
        // Enquiry enquiry = new Enquiry();

        // enquiryRepo.put(enquiryId, enquiry);
        // dataManager.saveEnquiries(enquiryRepo);
        // return enquiry;
        return null;
    }

    public List<Enquiry> viewMyEnquiries(User user) {

        return null;
    }

    public boolean editEnquiry(String enquiryId, String newContent, User user) {
        // Enquiry enquiry = enquiryRepo.get(enquiryId);

        // // ensure that enquiries are correctly edited by person who created it
        // if (enquiry != null && enquiry.getUserNric().equals(user.getUserId())) {
        // enquiry.setContent(newContent);
        // dataManager.saveEnquiries(enquiryRepo);
        // return true;
        // }
        return false;
    }

    public boolean deleteEnquiry(String enquiryId, User user) {
        // if (enquiryRepo.containsKey(enquiryId) &&
        // enquiryRepo.get(enquiryId).equals(user.getUserId())) {
        // enquiryRepo.remove(enquiryId);
        // dataManager.saveEnquiries(enquiryRepo);
        // return true;
        // }
        return false;
    }

    public boolean replyToEnquiry(String enquiryId, User user, HDBStaff staff) {
        // Enquiry enquiry = enquiryRepo.get(enquiryId);

        // if (enquiry != null && enquiry.getStatus() == EnquiryStatus.PENDING) {
        // enquiry.setReply(replyContent);
        // enquiry.setStatus(EnquiryStatus.RESOLVED);
        // enquiry.setReplyDate(LocalDate.now());
        // dataManager.saveEnquiries(enquiryRepo);
        // return true;
        // }
        return false;
    }

    public List<Enquiry> viewAllEnquiries(HDBManager manager) {

        return null;
    }

    public List<Enquiry> viewProjectEnquiries(HDBStaff staff, String projectId) {

        return null;
    }

}
