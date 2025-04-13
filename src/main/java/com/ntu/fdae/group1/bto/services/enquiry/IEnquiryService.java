package com.ntu.fdae.group1.bto.services.enquiry;

import java.util.List;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;

/**
 * Interface defining the service operations for handling enquiries in the BTO
 * Management System.
 * <p>
 * This service interface provides methods for the complete lifecycle management
 * of enquiries,
 * including creation, editing, deletion, and replying. It also offers various
 * methods for
 * retrieving enquiries based on different criteria.
 * </p>
 * <p>
 * The service acts as part of the business logic layer in the application
 * architecture,
 * positioned between controllers and repositories, implementing all
 * enquiry-related
 * business rules and validation.
 * </p>
 */
public interface IEnquiryService {

    /**
     * Creates a new enquiry in the system.
     * <p>
     * Records the enquiry with metadata including the creator, creation time,
     * and association with a project (if applicable).
     * </p>
     * 
     * @param user      The user creating the enquiry
     * @param projectId ID of the related project, or null for general enquiries
     * @param content   The enquiry content
     * @return The created enquiry with its assigned ID and metadata
     */
    Enquiry createEnquiry(User user, String projectId, String content);

    /**
     * Edits an existing enquiry's content.
     * <p>
     * The implementation should verify that the user is authorized to edit the
     * enquiry
     * and that the enquiry has not been replied to yet.
     * </p>
     * 
     * @param enquiryId  ID of the enquiry to edit
     * @param newContent New content for the enquiry
     * @param user       The user editing the enquiry
     * @return true if edit was successful, false otherwise
     */
    boolean editEnquiry(String enquiryId, String newContent, User user);

    /**
     * Deletes an existing enquiry from the system.
     * <p>
     * The implementation should verify that the user is authorized to delete the
     * enquiry
     * and that the enquiry has not been replied to yet.
     * </p>
     * 
     * @param enquiryId ID of the enquiry to delete
     * @param user      The user deleting the enquiry
     * @return true if deletion was successful, false otherwise
     */
    boolean deleteEnquiry(String enquiryId, User user);

    /**
     * Adds an official reply to an existing enquiry.
     * <p>
     * The reply is recorded with the staff member who provided it and the
     * timestamp.
     * Once replied to, an enquiry should be marked as such and should not be
     * editable or deletable by the original submitter.
     * </p>
     * 
     * @param enquiryId    ID of the enquiry to reply to
     * @param replyContent The content of the reply
     * @param staff        The HDB staff member providing the reply
     * @return true if reply was successfully added, false otherwise
     */
    boolean replyToEnquiry(String enquiryId, String replyContent, HDBStaff staff);

    /**
     * Retrieves all enquiries submitted by a specific user.
     * 
     * @param user The user whose enquiries to retrieve
     * @return List of enquiries submitted by the user
     */
    List<Enquiry> viewMyEnquiries(User user);

    /**
     * Retrieves all enquiries in the system.
     * 
     * @return List of all enquiries
     */
    List<Enquiry> viewAllEnquiries();

    /**
     * Retrieves enquiries associated with a specific project.
     * 
     * @param projectId ID of the project
     * @return List of enquiries for the specified project
     */
    List<Enquiry> viewProjectEnquiries(String projectId);

    /**
     * Finds an enquiry by its unique identifier.
     * 
     * @param enquiryId ID of the enquiry to find
     * @return The enquiry if found, or null if no enquiry exists with the given ID
     */
    Enquiry findEnquiryById(String enquiryId);
}
