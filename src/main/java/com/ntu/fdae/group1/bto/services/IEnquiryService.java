package com.ntu.fdae.group1.bto.services;

import java.util.List;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;

public interface IEnquiryService {
    /**
     * Creates a new enquiry
     * 
     * @param user      The user creating the enquiry
     * @param projectId ID of the related project, or null for general enquiries
     * @param content   The enquiry content
     * @return The created enquiry
     */
    Enquiry createEnquiry(User user, String projectId, String content);

    /**
     * Edits an existing enquiry
     * 
     * @param enquiryId  ID of the enquiry to edit
     * @param newContent New content for the enquiry
     * @param user       The user editing the enquiry
     * @return true if edit was successful, false otherwise
     */
    boolean editEnquiry(String enquiryId, String newContent, User user);

    /**
     * Deletes an enquiry
     * 
     * @param enquiryId ID of the enquiry to delete
     * @param user      The user deleting the enquiry
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteEnquiry(String enquiryId, User user);

    /**
     * Adds a reply to an enquiry
     * 
     * @param enquiryId    ID of the enquiry to reply to
     * @param replyContent The reply content
     * @param staff        The staff member replying
     * @return true if reply was successful, false otherwise
     */
    boolean replyToEnquiry(String enquiryId, String replyContent, HDBStaff staff);

    /**
     * Gets all enquiries by a specific user
     * 
     * @param user The user
     * @return List of enquiries by the user
     */
    List<Enquiry> viewMyEnquiries(User user);

    /**
     * Gets all enquiries in the system
     * 
     * @return All enquiries
     */
    List<Enquiry> viewAllEnquiries();

    /**
     * Gets all enquiries for a specific project
     * 
     * @param projectId ID of the project
     * @return List of enquiries for the project
     */
    List<Enquiry> viewProjectEnquiries(String projectId);

    /**
     * Finds an enquiry by its ID
     * 
     * @param enquiryId ID of the enquiry to find
     * @return The enquiry, or null if not found
     */
    Enquiry findEnquiryById(String enquiryId);
}
