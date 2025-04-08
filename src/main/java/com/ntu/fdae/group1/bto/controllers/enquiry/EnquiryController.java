package com.ntu.fdae.group1.bto.controllers.enquiry;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.enums.UserRole;
import com.ntu.fdae.group1.bto.exceptions.AuthenticationException;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.services.enquiry.IEnquiryService;
import com.ntu.fdae.group1.bto.services.project.IOfficerRegistrationService;

/**
 * Controller for enquiry-related operations
 */
public class EnquiryController {
    private final IEnquiryService enquiryService;
    private final IOfficerRegistrationService registrationService;

    // Constructor for dependency injection
    public EnquiryController(IEnquiryService enquiryService, IOfficerRegistrationService registrationService) {
        this.enquiryService = Objects.requireNonNull(enquiryService, "EnquiryService cannot be null");
        this.registrationService = Objects.requireNonNull(registrationService,
                "OfficerRegistrationService cannot be null");
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
        if (newContent == null || newContent.trim().isEmpty()) {
            return false; // Invalid content
        }

        // Check if the user is allowed to edit the enquiry
        Enquiry enquiry = enquiryService.findEnquiryById(enquiryId);
        if (enquiry == null || !enquiry.getUserNric().equals(user.getNric())) {
            return false; // User not authorized to edit this enquiry
        }

        // Check if enquiry has already been replied to
        if (enquiry.isReplied()) {
            // Once an enquiry has been
            // replied to, should the applicant still
            // be able to edit or delete it?
            // A: No. Once replied, the enquiry
            // should not be editable or deletable
            // by the applicant.
            return false;
        }

        // Proceed with the edit
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
        // Check if the user is allowed to edit the enquiry
        Enquiry enquiry = enquiryService.findEnquiryById(enquiryId);
        if (enquiry == null || !enquiry.getUserNric().equals(user.getNric())) {
            return false; // User not authorized to edit this enquiry
        }

        // Check if enquiry has already been replied to
        if (enquiry.isReplied()) {
            // Once an enquiry has been
            // replied to, should the applicant still
            // be able to edit or delete it?
            // A: No. Once replied, the enquiry
            // should not be editable or deletable
            // by the applicant.
            return false;
        }

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
    public boolean replyToEnquiry(HDBStaff staff, String enquiryId, String replyContent) {
        // Check if the staff member is allowed to reply to the enquiry
        Enquiry enquiry = enquiryService.findEnquiryById(enquiryId);
        if (enquiry == null) {
            return false; // Enquiry not found
        }

        // Check if the staff member is authorized to reply
        if (staff.getRole() == UserRole.HDB_OFFICER) {
            // Officers can only reply to enquiries they are registered for
            try {
                HDBOfficer officer = (HDBOfficer) staff;
                OfficerRegStatus status = registrationService.getRegistrationStatus(officer, enquiry.getProjectId());
                if (status != OfficerRegStatus.APPROVED) {
                    return false; // Not authorized to reply
                }
            } catch (ClassCastException e) {
                // Should not happen if role check is correct, but handle defensively
                System.err.println("Authorization Error: User role mismatch during officer check.");
                return false;
            }
        }

        return enquiryService.replyToEnquiry(enquiryId, replyContent, staff);
    }

    /**
     * Gets all enquiries by a specific user
     * 
     * @param user The user
     * @return List of enquiries by the user
     */
    public List<Enquiry> viewMyEnquiries(User user) {
        return enquiryService.viewMyEnquiries(user);
    }

    /**
     * Gets all enquiries in the system
     * 
     * @return All enquiries
     */
    public List<Enquiry> viewAllEnquiries() {
        return enquiryService.viewAllEnquiries();
    }

    /**
     * Retrieves enquiries associated with a specific project, performing
     * authorization checks.
     * Managers can view enquiries for any project.
     * Officers can only view enquiries for projects they are approved to handle.
     *
     * @param staff     The HDBStaff member (Officer or Manager) making the request.
     * @param projectId The ID of the project whose enquiries are requested.
     * @return A List of Enquiry objects for the specified project.
     * @throws InvalidInputException   if projectId is null or empty.
     * @throws AuthenticationException if the staff member is not authorized to view
     *                                 these enquiries.
     * @throws DataAccessException     if an error occurs during data retrieval or
     *                                 auth check.
     */
    public List<Enquiry> viewProjectEnquiries(HDBStaff staff, String projectId)
            throws InvalidInputException, AuthenticationException, DataAccessException {

        // 1. Input Validation
        if (staff == null) {
            throw new AuthenticationException("User context is required.");
        }
        if (projectId == null || projectId.trim().isEmpty()) {
            throw new InvalidInputException("Project ID cannot be empty when viewing project enquiries.");
        }

        // 2. Authorization Check
        boolean isAuthorized = false;
        if (staff.getRole() == UserRole.HDB_MANAGER) {
            // Managers are authorized to view ALL project enquiries (FAQ pg 57)
            isAuthorized = true;
        } else if (staff.getRole() == UserRole.HDB_OFFICER) {
            try {
                // Cast staff to HDBOfficer to use the specific method signature
                HDBOfficer officer = (HDBOfficer) staff;
                OfficerRegStatus status = registrationService.getRegistrationStatus(officer, projectId);

                // Check if the status returned is APPROVED
                isAuthorized = (status == OfficerRegStatus.APPROVED);

            } catch (ClassCastException e) {
                // Should not happen if role check is correct, but handle defensively
                System.err.println("Authorization Error: User role mismatch during officer check.");
                throw new AuthenticationException("Internal error during authorization check.", e);
            } catch (Exception e) { // Catch potential errors from the service call
                System.err.println(
                        "Error checking officer registration status for project " + projectId + ": " + e.getMessage());
                throw new AuthenticationException("Could not determine authorization status for officer.", e);
            }
        }

        if (!isAuthorized) {
            throw new AuthenticationException(
                    "User " + staff.getNric() + " is not authorized to view enquiries for project " + projectId);
        }

        // 3. Data Retrieval (if authorized)
        try {
            List<Enquiry> projectEnquiries = enquiryService.viewProjectEnquiries(projectId);
            return projectEnquiries != null ? projectEnquiries : Collections.emptyList();
        } catch (DataAccessException e) {
            System.err.println("Data access error fetching enquiries for project " + projectId + ": " + e.getMessage());
            throw e;
        } catch (Exception e) {
            System.err.println("Unexpected error fetching enquiries for project " + projectId + ": " + e.getMessage());
            throw new RuntimeException("An unexpected error occurred while fetching project enquiries.", e);
        }
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
