package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.models.project.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.IEnquiryRepository;

import java.util.List;
import java.time.LocalDate;
import java.util.UUID;

public class EnquiryController {
    private IEnquiryRepository enquiryRepo;

    public EnquiryController(IEnquiryRepository enqRepo) {
        this.enquiryRepo = enqRepo;
    }

    public Enquiry createEnquiry(User user, String projectId, String content) {
        // Implementation
        return null;
    }

    public List<Enquiry> viewMyEnquiries(User user) {
        // Implementation
        return null;
    }

    public boolean editEnquiry(String enquiryId, String newContent, User user) {
        // Implementation
        return false;
    }

    public boolean deleteEnquiry(String enquiryId, User user) {
        // Implementation
        return false;
    }

    public boolean replyToEnquiry(String enquiryId, String replyContent, HDBStaff staff) {
        // Implementation
        return false;
    }

    public List<Enquiry> viewAllEnquiries(HDBManager manager) {
        // Implementation
        return null;
    }

    public List<Enquiry> viewProjectEnquiries(HDBStaff staff, String projectId) {
        // Implementation
        return null;
    }
}
