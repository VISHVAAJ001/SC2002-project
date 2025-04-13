package com.ntu.fdae.group1.bto.services.enquiry;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.user.HDBStaff;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.enquiry.IEnquiryRepository;
import com.ntu.fdae.group1.bto.utils.IdGenerator;

/**
 * Service for managing enquiries in the BTO Management System.
 * <p>
 * This service is responsible for handling the business logic related to
 * enquiries,
 * including:
 * - Creating new enquiries
 * - Editing and deleting existing enquiries
 * - Replying to enquiries (for HDB staff)
 * - Retrieving enquiries by various criteria (user, project, ID)
 * </p>
 * <p>
 * The service follows the Service Layer pattern, encapsulating business logic
 * related to enquiry management and providing a clean API for controllers.
 * </p>
 */
public class EnquiryService implements IEnquiryService {
    /**
     * Repository for accessing and manipulating enquiry data.
     */
    private final IEnquiryRepository enquiryRepo;

    /**
     * Constructs an EnquiryService with the specified enquiry repository.
     *
     * @param enquiryRepo The repository for enquiry data access operations
     */
    public EnquiryService(IEnquiryRepository enquiryRepo) {
        this.enquiryRepo = enquiryRepo;
    }

    /**
     * Creates a new enquiry with the specified details.
     * <p>
     * Generates a unique ID for the enquiry, sets the creation date to the current
     * date,
     * and saves it to the repository.
     * </p>
     *
     * @param user      The user creating the enquiry
     * @param projectId The ID of the project the enquiry is about
     * @param content   The content/text of the enquiry
     * @return The newly created Enquiry object
     */
    @Override
    public Enquiry createEnquiry(User user, String projectId, String content) {
        String enquiryId = IdGenerator.generateEnquiryId();
        Enquiry newEnquiry = new Enquiry(enquiryId, user.getNric(), projectId, content, LocalDate.now());
        enquiryRepo.save(newEnquiry);
        return newEnquiry;
    }

    /**
     * Edits the content of an existing enquiry.
     * <p>
     * Updates the enquiry's content and saves the changes to the repository.
     * </p>
     *
     * @param enquiryId  The ID of the enquiry to edit
     * @param newContent The new content for the enquiry
     * @param user       The user attempting to edit the enquiry
     * @return true if the edit was successful
     */
    @Override
    public boolean editEnquiry(String enquiryId, String newContent, User user) {
        Enquiry enquiry = enquiryRepo.findById(enquiryId);

        enquiry.editContent(newContent);
        enquiryRepo.save(enquiry);
        return true;
    }

    /**
     * Deletes an enquiry with the specified ID.
     * <p>
     * Attempts to delete the enquiry from the repository and returns a success
     * indicator.
     * </p>
     *
     * @param enquiryId The ID of the enquiry to delete
     * @param user      The user attempting to delete the enquiry
     * @return true if the deletion was successful, false if an error occurred
     */
    @Override
    public boolean deleteEnquiry(String enquiryId, User user) {
        try {
            enquiryRepo.deleteById(enquiryId);
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Adds a reply to an enquiry.
     * <p>
     * Verifies that the enquiry exists and has not been previously replied to,
     * then adds the reply with the current date and saves the updated enquiry.
     * </p>
     *
     * @param enquiryId    The ID of the enquiry to reply to
     * @param replyContent The content of the reply
     * @param staff        The HDB staff member providing the reply
     * @return true if the reply was successfully added, false if the enquiry
     *         doesn't
     *         exist or already has a reply
     */
    @Override
    public boolean replyToEnquiry(String enquiryId, String replyContent, HDBStaff staff) {
        Enquiry enquiry = enquiryRepo.findById(enquiryId);

        // Check if enquiry exists
        if (enquiry == null) {
            return false;
        }

        // Check if enquiry has already been replied to
        if (enquiry.isReplied()) {
            return false;
        }

        enquiry.addReply(replyContent, LocalDate.now());
        enquiryRepo.save(enquiry);
        return true;
    }

    /**
     * Retrieves all enquiries made by a specific user.
     *
     * @param user The user whose enquiries to retrieve
     * @return A list of enquiries made by the specified user
     */
    @Override
    public List<Enquiry> viewMyEnquiries(User user) {
        return enquiryRepo.findByUserNric(user.getNric());
    }

    /**
     * Retrieves all enquiries in the system.
     * <p>
     * This method is typically used by HDB staff to view all enquiries.
     * </p>
     *
     * @return A list of all enquiries in the system
     */
    @Override
    public List<Enquiry> viewAllEnquiries() {
        return enquiryRepo.findAll().values().stream()
                .collect(Collectors.toList());
    }

    /**
     * Retrieves all enquiries related to a specific project.
     *
     * @param projectId The ID of the project
     * @return A list of enquiries for the specified project
     */
    @Override
    public List<Enquiry> viewProjectEnquiries(String projectId) {
        return enquiryRepo.findByProjectId(projectId);
    }

    /**
     * Finds an enquiry by its unique identifier.
     *
     * @param enquiryId The ID of the enquiry to find
     * @return The enquiry with the specified ID, or null if not found
     */
    @Override
    public Enquiry findEnquiryById(String enquiryId) {
        return enquiryRepo.findById(enquiryId);
    }
}
