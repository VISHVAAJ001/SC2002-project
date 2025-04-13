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
 * Controller responsible for managing enquiry-related operations in the BTO
 * Management System.
 * <p>
 * This controller serves as an intermediary between the UI layer and the
 * enquiry service,
 * handling operations such as creating, editing, and deleting enquiries, as
 * well as
 * managing replies to enquiries. It also enforces business rules regarding who
 * can
 * perform certain operations on enquiries.
 * </p>
 * <p>
 * The controller implements role-based authorization checks to ensure that:
 * - Only the creator of an enquiry can edit or delete it
 * - Enquiries that have been replied to cannot be edited or deleted
 * - HDB Officers can only reply to enquiries for projects they are approved for
 * - HDB Managers can access and reply to all enquiries
 * </p>
 */
public class EnquiryController {
    private final IEnquiryService enquiryService;
    private final IOfficerRegistrationService registrationService;

    // Constructor for dependency injection
    /**
     * Constructs a new EnquiryController with the specified services.
     * <p>
     * Uses dependency injection to receive the required services and performs
     * null checks to ensure valid dependencies.
     * </p>
     * 
     * @param enquiryService      The service handling enquiry-related business
     *                            logic
     * @param registrationService The service handling officer registration status
     *                            checks
     * @throws NullPointerException if either service is null
     */
    public EnquiryController(IEnquiryService enquiryService, IOfficerRegistrationService registrationService) {
        this.enquiryService = Objects.requireNonNull(enquiryService, "EnquiryService cannot be null");
        this.registrationService = Objects.requireNonNull(registrationService,
                "OfficerRegistrationService cannot be null");
    }

    /**
     * Creates a new enquiry in the system.
     * <p>
     * Allows users to submit enquiries either related to a specific project or
     * as general enquiries when projectId is null. The enquiry is associated with
     * the user who created it.
     * </p>
     * 
     * @param user      The user creating the enquiry
     * @param projectId ID of the related project, or null for general enquiries
     * @param content   The enquiry content
     * @return The created enquiry with its assigned ID and metadata
     */
    public Enquiry createEnquiry(User user, String projectId, String content) {
        return enquiryService.createEnquiry(user, projectId, content);
    }

    /**
     * Edits an existing enquiry's content.
     * <p>
     * This operation is restricted by the following business rules:
     * 1. Only the original creator of the enquiry can edit it
     * 2. Enquiries that have already received a reply cannot be edited
     * 3. The new content must not be empty or null
     * </p>
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
     * Deletes an existing enquiry from the system.
     * <p>
     * This operation is restricted by the following business rules:
     * 1. Only the original creator of the enquiry can delete it
     * 2. Enquiries that have already received a reply cannot be deleted
     * </p>
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
     * Adds an official reply to an existing enquiry.
     * <p>
     * This operation is restricted by role-based authorization:
     * - HDB Managers can reply to any enquiry
     * - HDB Officers can only reply to enquiries for projects they are approved for
     * </p>
     * <p>
     * Once an enquiry has been replied to, it can no longer be edited or deleted by
     * the original submitter.
     * </p>
     * 
     * @param staff        The HDB staff member (Officer or Manager) providing the
     *                     reply
     * @param enquiryId    ID of the enquiry to reply to
     * @param replyContent The content of the reply
     * @return true if reply was successfully added, false otherwise
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
     * Retrieves all enquiries submitted by a specific user.
     * <p>
     * This method allows users to view their own enquiry history, including
     * both replied and non-replied enquiries.
     * </p>
     * 
     * @param user The user whose enquiries to retrieve
     * @return List of enquiries submitted by the user, or an empty list if none
     *         exist
     */
    public List<Enquiry> viewMyEnquiries(User user) {
        return enquiryService.viewMyEnquiries(user);
    }

    /**
     * Retrieves all enquiries in the system.
     * <p>
     * This method provides access to all enquiries regardless of their status or
     * the project they are associated with. It is typically used by administrators
     * or for reporting purposes.
     * </p>
     * 
     * @return List of all enquiries in the system
     */
    public List<Enquiry> viewAllEnquiries() {
        return enquiryService.viewAllEnquiries();
    }

    /**
     * Retrieves enquiries associated with a specific project, performing
     * authorization checks.
     * <p>
     * This method implements role-based access control:
     * - Managers can view enquiries for any project
     * - Officers can only view enquiries for projects they are approved to handle
     * </p>
     * <p>
     * The method performs thorough validation and exception handling to provide
     * clear feedback about authorization issues or data access problems.
     * </p>
     *
     * @param staff     The HDB staff member (Officer or Manager) making the request
     * @param projectId The ID of the project whose enquiries are requested
     * @return A List of Enquiry objects for the specified project
     * @throws InvalidInputException   if projectId is null or empty
     * @throws AuthenticationException if the staff member is not authorized to view
     *                                 these enquiries
     * @throws DataAccessException     if an error occurs during data retrieval or
     *                                 authorization check
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
     * Finds an enquiry by its unique identifier.
     * <p>
     * This method retrieves a specific enquiry based on its ID. It is commonly used
     * before performing operations like editing, deleting, or replying to an
     * enquiry
     * to verify the enquiry exists and check its current state.
     * </p>
     * 
     * @param enquiryId ID of the enquiry to find
     * @return The enquiry if found, or null if no enquiry exists with the given ID
     */
    public Enquiry findEnquiryById(String enquiryId) {
        return enquiryService.findEnquiryById(enquiryId);
    }
}
