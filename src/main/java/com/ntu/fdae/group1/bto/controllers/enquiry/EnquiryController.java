package com.ntu.fdae.group1.bto.controllers.enquiry;

import java.util.List;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.IEnquiryService;

/**
 * Controller for enquiry-related operations
 */
public class EnquiryController {
    private final IEnquiryService enquiryService;

    /**
     * Constructs a new EnquiryController
     * 
     * @param enquiryService The enquiry service to use
     */
    public EnquiryController(IEnquiryService enquiryService) {
        this.enquiryService = enquiryService;
    }

    /**
     * Creates a new enquiry
     * 
     * @param user      The user creating the enquiry
     * @param projectId ID of the related project, or null for general enquiries
     * @param content   The enquiry content
     * @return The created enquiry
     */
    public Enquiry createEnquiry(User user, String projectId, String content) {
        return enquiryService.createEnquiry(user, projectId, content);
    }

    /**
     * Edits an existing enquiry
     * 
     * @param enquiryId  ID of the enquiry to edit
     * @param newContent New content for the enquiry
     * @param user       The user editing the enquiry
     * @return true if edit was successful, false otherwise
     */
    public boolean editEnquiry(String enquiryId, String newContent, User user) {
        return enquiryService.editEnquiry(enquiryId, newContent, user);
    }

    /**
     * Deletes an enquiry
     * 
     * @param enquiryId ID of the enquiry to delete
     * @param user      The user deleting the enquiry
     * @return true if deletion was successful, false otherwise
     */
    public boolean deleteEnquiry(String enquiryId, User user) {
        return enquiryService.deleteEnquiry(enquiryId, user);
    }

    /**
     * Adds a reply to an enquiry
     * 
     * @param enquiryId    ID of the enquiry to reply to
     * @param replyContent The reply content
     * @param staff        The staff member replying
     * @return true if reply was successful, false otherwise
     */
    public boolean replyToEnquiry(String enquiryId, String replyContent, HDBStaff staff) {
        return enquiryService.replyToEnquiry(enquiryId, replyContent, staff);
    }

    /**
     * Gets all enquiries by a specific user
     * 
     * @param user The user
     * @return List of enquiries by the user
     */
    public List<Enquiry> getEnquiriesByUser(User user) {
        return enquiryService.viewMyEnquiries(user);
    }

    /**
     * Gets all enquiries in the system
     * 
     * @return All enquiries
     */
    public List<Enquiry> getAllEnquiries() {
        return enquiryService.viewAllEnquiries();
    }

    /**
     * Gets all enquiries for a specific project
     * 
     * @param projectId ID of the project
     * @return List of enquiries for the project
     */
    public List<Enquiry> getEnquiriesByProject(String projectId) {
        return enquiryService.viewProjectEnquiries(projectId);
    }

    /**
     * Finds an enquiry by its ID
     * 
     * @param enquiryId ID of the enquiry to find
     * @return The enquiry, or null if not found
     */
    public Enquiry findEnquiryById(String enquiryId) {
        return enquiryService.findEnquiryById(enquiryId);
    }
}
