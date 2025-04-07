package com.ntu.fdae.group1.bto.controllers.enquiry;

import java.util.List;
import java.util.Objects;

// Adjust imports as needed
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.enquiry.IEnquiryService;
import com.ntu.fdae.group1.bto.exceptions.*;
/**
 * Controller for enquiry-related operations, aligned with UML diagram.
 */
public class EnquiryController {
    private final IEnquiryService enquiryService;

    /**
     * Constructs a new EnquiryController
     *
     * @param enquiryService The enquiry service to use
     */
    public EnquiryController(IEnquiryService enquiryService) {
        this.enquiryService = Objects.requireNonNull(enquiryService, "Enquiry Service cannot be null");
    }

    /**
     * Creates a new enquiry.
     * Matches UML: createEnquiry(user: User, projectId: String <<nullable>>, content: String) : Enquiry <<throws InvalidInputException>>
     *
     * @param user      The user creating the enquiry
     * @param projectId ID of the related project, or null for general enquiries
     * @param content   The enquiry content
     * @return The created enquiry object
     * @throws InvalidInputException if user is null or content is empty/blank.
     */
    public Enquiry createEnquiry(User user, String projectId, String content) throws InvalidInputException {
        if (user == null) {
            throw new InvalidInputException("User cannot be null when creating an enquiry.");
        }
        if (content == null || content.trim().isEmpty()) {
            throw new InvalidInputException("Enquiry content cannot be empty.");
        }
        // Project ID can be null, service handles this
        return enquiryService.createEnquiry(user, projectId, content);
    }

    /**
     * Edits the content of an existing enquiry owned by the user.
     * Matches UML: editMyEnquiry(user: User, enquiryId: String, newContent: String) : boolean <<throws InvalidInputException>>
     *
     * @param user       The user editing the enquiry (for authorization)
     * @param enquiryId  ID of the enquiry to edit
     * @param newContent New content for the enquiry
     * @return true if the edit was successful (enquiry found, user is owner, not replied yet), false otherwise.
     * @throws InvalidInputException if user/enquiryId is null/blank, or newContent is empty/blank.
     */
    public boolean editMyEnquiry(User user, String enquiryId, String newContent) throws InvalidInputException {
        if (user == null) {
            throw new InvalidInputException("User cannot be null when editing an enquiry.");
        }
        if (enquiryId == null || enquiryId.isBlank()) {
            throw new InvalidInputException("Enquiry ID cannot be empty for editing.");
        }
        if (newContent == null || newContent.trim().isEmpty()) {
            throw new InvalidInputException("New enquiry content cannot be empty.");
        }
        // Service handles authorization (user must own enquiry) and state check (e.g., cannot edit if replied)
        return enquiryService.editEnquiry(enquiryId, newContent, user);
    }

    /**
     * Deletes an enquiry owned by the user.
     * Matches UML: deleteMyEnquiry(user: User, enquiryId: String) : boolean
     * Note: UML doesn't specify exception, but adding for validation consistency.
     *
     * @param user      The user deleting the enquiry (for authorization)
     * @param enquiryId ID of the enquiry to delete
     * @return true if deletion was successful (enquiry found, user is owner), false otherwise.
     * @throws InvalidInputException if user or enquiryId is null/blank.
     */
    public boolean deleteMyEnquiry(User user, String enquiryId) throws InvalidInputException { // Added exception for validation
        if (user == null) {
            throw new InvalidInputException("User cannot be null when deleting an enquiry.");
        }
        if (enquiryId == null || enquiryId.isBlank()) {
            throw new InvalidInputException("Enquiry ID cannot be empty for deletion.");
        }
        // Service handles authorization (user must own enquiry)
        return enquiryService.deleteEnquiry(enquiryId, user);
    }

    /**
     * Adds a reply to an enquiry by an HDB staff member.
     * Matches UML: replyToEnquiry(staff: HDBStaff, enquiryId: String, replyContent: String) : boolean <<throws InvalidInputException>>
     *
     * @param staff        The staff member replying (for authorization)
     * @param enquiryId    ID of the enquiry to reply to
     * @param replyContent The reply content
     * @return true if the reply was successfully added (enquiry found, staff has permission, not already replied), false otherwise.
     * @throws InvalidInputException if staff/enquiryId is null/blank, or replyContent is empty/blank.
     */
    public boolean replyToEnquiry(HDBStaff staff, String enquiryId, String replyContent) throws InvalidInputException {
        if (staff == null) {
            throw new InvalidInputException("Staff cannot be null when replying to an enquiry.");
        }
        if (enquiryId == null || enquiryId.isBlank()) {
            throw new InvalidInputException("Enquiry ID cannot be empty for replying.");
        }
        if (replyContent == null || replyContent.trim().isEmpty()) {
            throw new InvalidInputException("Reply content cannot be empty.");
        }
        // Service handles authorization (e.g., manager replies to project/general, officer replies to own project) and state check
        return enquiryService.replyToEnquiry(enquiryId, replyContent, staff);
    }

    /**
     * Gets all enquiries created by a specific user.
     * Matches UML: viewMyEnquiries(user: User) : List<Enquiry>
     *
     * @param user The user whose enquiries are requested.
     * @return List of enquiries created by the user. Returns an empty list if user is null or has no enquiries.
     */
    public List<Enquiry> viewMyEnquiries(User user) {
        if (user == null) {
            System.err.println("Controller Warning: User is required to view their enquiries.");
            return List.of();
        }
        return enquiryService.viewMyEnquiries(user);
    }

    /**
     * Gets all enquiries in the system. Requires staff context.
     * Matches UML: viewAllEnquiries(staff: HDBStaff) : List<Enquiry>
     *
     * @param staff The HDB staff member requesting the list (for context/potential authorization).
     * @return List of all enquiries in the system. Returns an empty list if staff context is missing.
     */
    public List<Enquiry> viewAllEnquiries(HDBStaff staff) {
        if (staff == null) {
            System.err.println("Controller Warning: Staff context is required to view all enquiries.");
            return List.of(); // Or throw authorization exception
        }
        // Service layer fetches all enquiries
        return enquiryService.viewAllEnquiries();
    }

    /**
     * Gets all enquiries related to a specific project. Requires staff context.
     * Matches UML: viewProjectEnquiries(staff: HDBStaff, projectId: String) : List<Enquiry>
     *
     * @param staff     The HDB staff member requesting the list (for context/potential authorization).
     * @param projectId ID of the project.
     * @return List of enquiries for the specified project. Returns an empty list if staff/projectId is invalid or no enquiries found.
     */
    public List<Enquiry> viewProjectEnquiries(HDBStaff staff, String projectId) {
         if (staff == null) {
             System.err.println("Controller Warning: Staff context is required to view project enquiries.");
             return List.of(); // Or throw authorization exception
         }
        if (projectId == null || projectId.isBlank()) {
            System.err.println("Controller Warning: Project ID is required to view project enquiries.");
            return List.of();
        }
        // Service layer fetches enquiries by project ID
        return enquiryService.viewProjectEnquiries(projectId);
    }

    // This method was kept from the previous Java version but is NOT in the UML Controller definition.
    // Keep if needed for internal controller logic or UI helpers, otherwise remove for strict UML adherence.
    /**
     * Finds a specific enquiry by its ID.
     * Note: This method is not defined in the UML diagram for EnquiryController.
     *
     * @param enquiryId ID of the enquiry to find.
     * @return The Enquiry object, or null if not found or ID is invalid.
     */
    public Enquiry findEnquiryById(String enquiryId) {
        if (enquiryId == null || enquiryId.isBlank()) {
            System.err.println("Controller Warning: Enquiry ID is required for lookup.");
            return null;
        }
        return enquiryService.findEnquiryById(enquiryId);
    }
}