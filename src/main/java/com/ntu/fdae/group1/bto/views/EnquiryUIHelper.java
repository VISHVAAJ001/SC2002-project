package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class to manage common UI tasks related to displaying Enquiry
 * information.
 * Used via composition by role-specific UI classes (ApplicantUI, HDBManagerUI,
 * HDBOfficerUI).
 */
public class EnquiryUIHelper {

    private final BaseUI baseUI; // Use BaseUI for console interactions
    private final UserController userController;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Or your preferred
                                                                                              // format
    private static final int SNIPPET_LENGTH = 40; // Max length for content snippet in list view

    /**
     * Constructor for EnquiryUIHelper.
     * 
     * @param baseUI An instance of BaseUI (or a subclass) to handle console I/O.
     */
    public EnquiryUIHelper(BaseUI baseUI, UserController userController) {
        this.userController = Objects.requireNonNull(userController);
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null for EnquiryUIHelper");
    }

    /**
     * Displays a numbered list of enquiries with summary information and prompts
     * the user
     * to select one.
     *
     * @param enquiries The list of Enquiry objects to display.
     * @param listTitle The title to display above the list (e.g., "My Enquiries",
     *                  "Project Enquiries").
     * @return The selected Enquiry object, or null if the user chooses to go back
     *         or the list is empty.
     */
    public Enquiry selectEnquiryFromList(List<Enquiry> enquiries, String listTitle) {
        baseUI.displayHeader(listTitle);

        if (enquiries == null || enquiries.isEmpty()) {
            baseUI.displayMessage("No enquiries found matching the criteria.");
            return null;
        }

        // Display numbered list
        AtomicInteger counter = new AtomicInteger(1);
        enquiries.forEach(enq -> {
            String projectIdDisplay = (enq.getProjectId() == null || enq.getProjectId().trim().isEmpty()) ? "General"
                    : enq.getProjectId();
            String repliedStatus = enq.isReplied()
                    ? "Yes (" + (enq.getReplyDate() != null ? enq.getReplyDate().format(DATE_FORMATTER) : "N/A") + ")"
                    : "No";

            // Create a snippet of the content
            String contentSnippet = enq.getContent();
            if (contentSnippet.length() > SNIPPET_LENGTH) {
                contentSnippet = contentSnippet.substring(0, SNIPPET_LENGTH) + "...";
            }

            String summaryInfo = String.format(
                    "[%d] ID: %s | By: %s | Project: %s | Date: %s | Replied: %-15s | Content: \"%s\"",
                    counter.getAndIncrement(),
                    enq.getEnquiryId(),
                    enq.getUserNric(), // Submitter NRIC
                    projectIdDisplay,
                    enq.getSubmissionDate().format(DATE_FORMATTER),
                    repliedStatus,
                    contentSnippet);
            baseUI.displayMessage(summaryInfo);
        });
        baseUI.displayMessage("[0] Back");
        baseUI.displayMessage("-----------------------------------------");

        // Prompt for selection
        int choice = baseUI.promptForInt("Enter enquiry number to view details/manage (or 0 to go back): ");

        // Validate choice and return selected enquiry or null
        if (choice > 0 && choice <= enquiries.size()) {
            return enquiries.get(choice - 1); // Adjust to 0-based index
        } else {
            if (choice != 0) {
                baseUI.displayError("Invalid enquiry number selected.");
            }
            return null; // Indicates back / invalid choice
        }
    }

    /**
     * Displays the full details of a single Enquiry object.
     *
     * @param enquiry The Enquiry object whose details are to be displayed.
     */
    public void displayEnquiryDetails(Enquiry enquiry) {
        if (enquiry == null) {
            baseUI.displayError("Cannot display details for a null enquiry.");
            return;
        }

        baseUI.displayHeader("Enquiry Details (ID: " + enquiry.getEnquiryId() + ")");
        baseUI.displayMessage("Submitted By:  " + enquiry.getUserNric());
        baseUI.displayMessage("Project Ref:   "
                + ((enquiry.getProjectId() == null || enquiry.getProjectId().trim().isEmpty()) ? "General Enquiry"
                        : enquiry.getProjectId()));
        baseUI.displayMessage("Submitted On:  " + enquiry.getSubmissionDate().format(DATE_FORMATTER));
        baseUI.displayMessage("--- Enquiry Content ---");
        // Print content potentially over multiple lines if long
        baseUI.displayMessage(enquiry.getContent());
        baseUI.displayMessage("-----------------------");
        baseUI.displayMessage("Replied Status:" + (enquiry.isReplied() ? " Yes" : " No"));

        if (enquiry.isReplied()) {
            baseUI.displayMessage("Replied On:    "
                    + (enquiry.getReplyDate() != null ? enquiry.getReplyDate().format(DATE_FORMATTER) : "N/A"));
            baseUI.displayMessage("--- Reply Content ---");
            baseUI.displayMessage(enquiry.getReply() != null ? enquiry.getReply() : "(No reply content recorded)");
            baseUI.displayMessage("---------------------");
        }
        baseUI.displayMessage("=======================");
    }
}