package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger; // For numbered lists

/*
 * Helper class to manage common UI tasks related to displaying Project information.
 * This class is intended to be used via composition by role-specific UI classes
 * (ApplicantUI, HDBManagerUI, HDBOfficerUI).
 * It uses separate methods for displaying details based on user role.
 */
public class ProjectUIHelper {

    private final BaseUI baseUI; // Use BaseUI for console interactions
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Or your preferred
                                                                                              // format

    /**
     * Constructor for ProjectUIHelper.
     * 
     * @param baseUI An instance of BaseUI (or a subclass) to handle console I/O.
     */
    public ProjectUIHelper(BaseUI baseUI) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null");
    }

    /**
     * Displays a numbered list of projects with basic information and prompts the
     * user
     * to select one.
     *
     * @param projects  The list of Project objects to display.
     * @param listTitle The title to display above the list (e.g., "Available
     *                  Projects").
     * @return The selected Project object, or null if the user chooses to go back
     *         or the list is empty.
     */
    public Project selectProjectFromList(List<Project> projects, String listTitle) {
        baseUI.displayHeader(listTitle);

        if (projects == null || projects.isEmpty()) {
            baseUI.displayMessage("No projects found matching the criteria.");
            return null;
        }

        // Display numbered list using an AtomicInteger for 1-based indexing in lambda
        AtomicInteger counter = new AtomicInteger(1);
        projects.forEach(p -> {
            // Customize the basic info shown in the list as needed
            // Show visibility status as it's relevant for staff and implicitly determines
            // applicant view
            String basicInfo = String.format("[%d] ID: %-8s | Name: %-20s (%-12s) | Closing: %s | Visible: %s",
                    counter.getAndIncrement(),
                    p.getProjectId(),
                    p.getProjectName(),
                    p.getNeighborhood(),
                    p.getClosingDate().format(DATE_FORMATTER),
                    p.isVisible() ? "Yes" : "No");
            baseUI.displayMessage(basicInfo);
        });
        baseUI.displayMessage("[0] Back");
        baseUI.displayMessage("-----------------------------------------");

        // Prompt for selection
        int choice = baseUI.promptForInt("Enter project number to view details (or 0 to go back): ");

        // Validate choice and return selected project or null
        if (choice > 0 && choice <= projects.size()) {
            return projects.get(choice - 1); // Adjust to 0-based index
        } else {
            if (choice != 0) {
                baseUI.displayError("Invalid project number selected.");
            }
            return null; // Indicates back / invalid choice
        }
    }

    /**
     * Displays project details relevant to an Applicant.
     * Shows core info and available flat types/units.
     *
     * @param project The Project object whose details are to be displayed.
     */
    public void displayApplicantProjectDetails(Project project) {
        if (project == null) {
            baseUI.displayError("Cannot display details for a null project.");
            return;
        }

        baseUI.displayHeader("Project Details: " + project.getProjectName() + " (" + project.getProjectId() + ")");
        baseUI.displayMessage("Neighborhood:     " + project.getNeighborhood());
        baseUI.displayMessage("Application Open: " + project.getOpeningDate().format(DATE_FORMATTER));
        baseUI.displayMessage("Application Close:" + project.getClosingDate().format(DATE_FORMATTER));

        // Display Flat Type Information (relevant for applicants)
        displayFlatInfoSection(project);

        // --- Applicant-specific context/eligibility display would go in ApplicantUI
        // ---
        // Example: baseUI.displayMessage("Eligibility Check: ...");
    }

    /**
     * Displays comprehensive project details relevant to HDB Staff
     * (Officers/Managers).
     * Shows core info, flat types/units, and administrative details.
     *
     * @param project The Project object whose details are to be displayed.
     */
    public void displayStaffProjectDetails(Project project) {
        if (project == null) {
            baseUI.displayError("Cannot display details for a null project.");
            return;
        }

        baseUI.displayHeader("Project Details: " + project.getProjectName() + " (" + project.getProjectId() + ")");
        baseUI.displayMessage("Neighborhood:     " + project.getNeighborhood());
        baseUI.displayMessage("Application Open: " + project.getOpeningDate().format(DATE_FORMATTER));
        baseUI.displayMessage("Application Close:" + project.getClosingDate().format(DATE_FORMATTER));

        // Display Flat Type Information
        displayFlatInfoSection(project);

        // --- Staff-Specific Administrative Details ---
        baseUI.displayMessage("--- Administrative Details ---");
        baseUI.displayMessage("Managed By (NRIC):" + project.getManagerNric());
        baseUI.displayMessage("Visibility Status:" + (project.isVisible() ? "ON (Visible)" : "OFF (Hidden)"));
        baseUI.displayMessage("Officer Slots Max:" + project.getMaxOfficerSlots());
        List<String> approvedOfficers = project.getApprovedOfficerNrics(); // Assuming getter exists
        baseUI.displayMessage("Approved Officers:" + (approvedOfficers == null || approvedOfficers.isEmpty() ? "None"
                : String.join(", ", approvedOfficers)));
        baseUI.displayMessage("-----------------------------");
    }

    /**
     * Helper method to display the flat type information section,
     * common to both Applicant and Staff views.
     * 
     * @param project The project containing the flat info.
     */
    private void displayFlatInfoSection(Project project) {
        baseUI.displayMessage("--- Flat Type Information ---");
        Map<FlatType, ProjectFlatInfo> flatInfoMap = project.getFlatTypes(); // Assuming getter exists

        if (flatInfoMap == null || flatInfoMap.isEmpty()) {
            baseUI.displayMessage("  (No flat type information available for this project)");
        } else {
            flatInfoMap.forEach((flatType, info) -> {
                String flatDetails = String.format("  - %-12s | Total Units: %-4d | Remaining: %-4d | Price: $%.2f",
                        flatType.name() + ":", // e.g., "TWO_ROOM:"
                        info.getTotalUnits(),
                        info.getRemainingUnits(),
                        info.getPrice());
                baseUI.displayMessage(flatDetails);
            });
        }
        baseUI.displayMessage("-----------------------------");
    }

}