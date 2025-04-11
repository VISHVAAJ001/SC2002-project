package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.project.Project;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Helper class to manage common UI tasks related to displaying Enquiry
 * information.
 * Used via composition by role-specific UI classes (ApplicantUI, HDBManagerUI,
 * HDBOfficerUI).
 */
public class EnquiryUIHelper {

    private final BaseUI baseUI; // Use BaseUI for console interactions
    private final UserController userController;
    private final ProjectController projectController;
    private static final int SNIPPET_LENGTH = 40; // Max length for content snippet in list view

    /**
     * Constructor for EnquiryUIHelper.
     * 
     * @param baseUI An instance of BaseUI (or a subclass) to handle console I/O.
     */
    public EnquiryUIHelper(BaseUI baseUI, UserController userController, ProjectController projectController) {
        this.userController = Objects.requireNonNull(userController);
        this.projectController = Objects.requireNonNull(projectController,
                "ProjectController cannot be null for EnquiryUIHelper");
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
                    ? "Yes (" + (enq.getReplyDate() != null ? enq.getReplyDate().format(BaseUI.DATE_FORMATTER) : "N/A")
                            + ")"
                    : "No";

            // Create a snippet of the content
            String contentSnippet = enq.getContent();
            if (contentSnippet.length() > SNIPPET_LENGTH) {
                contentSnippet = contentSnippet.substring(0, SNIPPET_LENGTH) + "...";
            }

            String summaryInfo = String.format(
                    "[%d] ID: %s | By: %s (%s) | Project: %s | Date: %s | Replied: %-15s",
                    counter.getAndIncrement(),
                    enq.getEnquiryId(),
                    enq.getUserNric(), // Submitter NRIC
                    userController.getUserName(enq.getUserNric()), // Submitter Name
                    projectIdDisplay,
                    enq.getSubmissionDate().format(BaseUI.DATE_FORMATTER),
                    repliedStatus);
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
        baseUI.displayMessage("Submitted By:  " + enquiry.getUserNric() + " ("
                + userController.getUserName(enquiry.getUserNric()) + ")");
        baseUI.displayMessage("Project Ref:   "
                + ((enquiry.getProjectId() == null || enquiry.getProjectId().trim().isEmpty()) ? "General Enquiry"
                        : enquiry.getProjectId()));
        baseUI.displayMessage("Submitted On:  " + enquiry.getSubmissionDate().format(BaseUI.DATE_FORMATTER));
        baseUI.displayMessage("--- Enquiry Content ---");
        // Print content potentially over multiple lines if long
        baseUI.displayMessage(enquiry.getContent());
        baseUI.displayMessage("-----------------------");
        baseUI.displayMessage("Replied Status:" + (enquiry.isReplied() ? " Yes" : " No"));

        if (enquiry.isReplied()) {
            baseUI.displayMessage("Replied On:    "
                    + (enquiry.getReplyDate() != null ? enquiry.getReplyDate().format(BaseUI.DATE_FORMATTER) : "N/A"));
            baseUI.displayMessage("--- Reply Content ---");
            baseUI.displayMessage(enquiry.getReply() != null ? enquiry.getReply() : "(No reply content recorded)");
            baseUI.displayMessage("---------------------");
        }
        baseUI.displayMessage("=======================");
    }

    /**
     * Displays a formatted list of enquiries, sorted by unreplied first,
     * and returns a map for selection.
     * 
     * @param enquiries List of enquiries to display.
     * @param title     Title for the list header.
     * @return Map where key is the displayed number, value is the Enquiry. Empty
     *         map if list is null/empty.
     */
    public Map<Integer, Enquiry> displayEnquiryList(List<Enquiry> enquiries, String title) {
        baseUI.displayHeader(title);
        Map<Integer, Enquiry> enquiryMap = new HashMap<>();
        if (enquiries == null || enquiries.isEmpty()) {
            baseUI.displayMessage("No enquiries to display in this list.");
            return enquiryMap;
        }

        // Sort unreplied first, then perhaps by date?
        List<Enquiry> sortedEnquiries = enquiries.stream()
                .sorted(Comparator.comparing(Enquiry::isReplied) // false (unreplied) comes first
                        .thenComparing(Enquiry::getSubmissionDate, Comparator.nullsLast(Comparator.reverseOrder()))) // Newest
                                                                                                                     // first
                                                                                                                     // within
                                                                                                                     // replied/unreplied
                .collect(Collectors.toList());

        int index = 1;
        for (Enquiry enq : sortedEnquiries) {
            Project proj = (enq.getProjectId() != null) ? projectController.findProjectById(enq.getProjectId()) : null;
            String projName = (proj != null) ? proj.getProjectName() : "General";
            String repliedStatus = enq.isReplied() ? "[Replied]" : "[UNREPLIED]";

            // Use displayMessage for potentially multi-line output
            baseUI.displayMessage(String.format("%d. %s EnqID: %s | User: %s | Project: %s (%s) | Date: %s",
                    index, repliedStatus, enq.getEnquiryId(), enq.getUserNric(), projName,
                    enq.getProjectId() == null ? "N/A" : enq.getProjectId(), formatDateSafe(enq.getSubmissionDate())));
            baseUI.displayMessage(String.format("   Q: %s", enq.getContent())); // Question on new line

            if (enq.isReplied()) {
                baseUI.displayMessage(
                        String.format("   A: %s (on %s)", enq.getReply(), formatDateSafe(enq.getReplyDate()))); // Reply
                                                                                                                // on
                                                                                                                // new
                                                                                                                // line
            }
            enquiryMap.put(index, enq);
            index++;
        }
        baseUI.displayMessage("[0] Back / Cancel");
        return enquiryMap;
    }

    private String formatDateSafe(LocalDate date) {
        return (date == null) ? "N/A" : BaseUI.DATE_FORMATTER.format(date);
    }
}