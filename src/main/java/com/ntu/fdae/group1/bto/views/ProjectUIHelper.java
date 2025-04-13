package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.enums.OfficerRegStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger; // For numbered lists

/**
 * Helper class for project-related UI operations in the BTO Management System.
 * <p>
 * This class provides reusable UI components for displaying and managing
 * project
 * information, including:
 * - Displaying lists of projects with filtering options
 * - Showing detailed project information tailored to different user roles
 * - Visualizing flat type availability and administrative details
 * </p>
 * <p>
 * The helper follows a composition pattern, working with a BaseUI instance for
 * common UI operations and controllers for retrieving user and project
 * information.
 * It is designed to be used by role-specific UI classes (ApplicantUI,
 * HDBManagerUI,
 * HDBOfficerUI) to maintain separation of concerns and reduce code duplication.
 * </p>
 */
public class ProjectUIHelper {

    /**
     * The base UI component for common UI operations.
     */
    private final BaseUI baseUI; // Use BaseUI for console interactions

    /**
     * The controller for project-related operations.
     */
    private final ProjectController projectController;

    /**
     * The controller for user-related operations.
     */
    private final UserController userController;

    /**
     * Constructs a new ProjectUIHelper with necessary UI and controllers.
     *
     * @param baseUI   The base UI for common display operations
     * @param userCtrl Controller for user-related operations
     * @param projCtrl Controller for project-related operations
     */
    public ProjectUIHelper(BaseUI baseUI, UserController userCtrl, ProjectController projCtrl) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null");
        this.userController = Objects.requireNonNull(userCtrl, "UserController cannot be null");
        this.projectController = Objects.requireNonNull(projCtrl, "ProjectController cannot be null");
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
            String basicInfo = String.format("[%d] Name: %-20s (%-12s) | Closing: %s",
                    counter.getAndIncrement(),
                    p.getProjectName(),
                    p.getNeighborhood(),
                    p.getClosingDate().format(BaseUI.DATE_FORMATTER));
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
        baseUI.displayMessage("Application Open: " + project.getOpeningDate().format(BaseUI.DATE_FORMATTER));
        baseUI.displayMessage("Application Close:" + project.getClosingDate().format(BaseUI.DATE_FORMATTER));

        // Display Flat Type Information (relevant for applicants)
        displayFlatInfoSection(project);
    }

    /**
     * Displays comprehensive project details relevant to HDB Staff
     * (Officers/Managers).
     * Shows core info, flat types/units, and administrative details.
     *
     * @param project      The Project object whose details are to be displayed
     * @param pendingCount The number of pending officer registrations for the
     *                     project
     */
    public void displayStaffProjectDetails(Project project, int pendingCount) {
        if (project == null) {
            baseUI.displayError("Cannot display details for a null project.");
            return;
        }

        baseUI.displayHeader("Project Details: " + project.getProjectName() + " (" + project.getProjectId() + ")");
        baseUI.displayMessage("Neighborhood:     " + project.getNeighborhood());
        baseUI.displayMessage("Application Open: " + baseUI.formatDateSafe(project.getOpeningDate()));
        baseUI.displayMessage("Application Close:" + baseUI.formatDateSafe(project.getClosingDate()));

        displayFlatInfoSection(project); // Display flat info

        baseUI.displayMessage("--- Administrative Details ---");
        // Use injected userController to get manager name
        baseUI.displayMessage("Managed By: " + project.getManagerNric() +
                " (" + this.userController.getUserName(project.getManagerNric()) + ")"); // Use field
        baseUI.displayMessage("Visibility Status: " + (project.isVisible() ? "ON (Visible)" : "OFF (Hidden)"));

        // --- Display Officer Slot Counts (using passed-in pendingCount) ---
        baseUI.displayMessage(String.format("Officer Slots    : %d / %d (Max: %d, Remaining: %d, Pending: %d)",
                project.getApprovedOfficerNrics().size(),
                project.getMaxOfficerSlots(),
                project.getMaxOfficerSlots(),
                project.getRemainingOfficerSlots(),
                pendingCount));

        List<String> approvedOfficers = project.getApprovedOfficerNrics(); // Assuming getter exists
        if (approvedOfficers == null || approvedOfficers.isEmpty()) {
            baseUI.displayMessage("Approved Officers: None");
        } else {
            String approvedOfficerNames = approvedOfficers.stream()
                    .map(nric -> nric + " (" + userController.getUserName(nric) + ")")
                    .reduce((a, b) -> a + ", " + b)
                    .orElse("");
            baseUI.displayMessage("Approved Officers: " + approvedOfficerNames);
        }
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

    /**
     * Prompts the user for project filtering criteria (Neighbourhood, Flat Type,
     * Visibility).
     * Allows users to skip criteria by pressing Enter.
     *
     * @param allowStaffFilters Set to true if staff-specific filters (like
     *                          visibility) should be offered.
     * @return A Map containing the filter keys and selected values. Empty map if no
     *         filters applied.
     */
    public Map<String, Object> promptForProjectFilters(boolean allowStaffFilters) {
        Map<String, Object> filters = new HashMap<>();
        baseUI.displayMessage("\n--- Apply Filters (Press Enter to skip) ---");

        // Neighbourhood
        String neighborhood = baseUI.promptForInput("Filter by Neighbourhood: ");
        if (!neighborhood.trim().isEmpty()) {
            filters.put("neighborhood", neighborhood.trim());
        }

        List<FlatType> availableFlatTypes = Arrays.asList(FlatType.values()); // Or create dynamically if needed
        FlatType selectedFlatType = baseUI.promptForEnum(
                "Filter by Flat Type (Choose number or 0 to cancel/skip):",
                FlatType.class,
                availableFlatTypes);

        if (selectedFlatType != null) { // Only add filter if user didn't cancel/skip
            filters.put("flatType", selectedFlatType);
        }

        // Visibility Filter (For Staff)
        if (allowStaffFilters) {
            String visibleStr = baseUI.promptForInput("Filter by Visibility (ON/OFF): ").toUpperCase();
            if (visibleStr.equals("ON"))
                filters.put("visibility", true);
            else if (visibleStr.equals("OFF"))
                filters.put("visibility", false);
            // If input is neither ON nor OFF, the filter is simply skipped
        }

        baseUI.displayMessage("-------------------------------------------");
        if (filters.isEmpty()) {
            baseUI.displayMessage("No filters applied.");
        } else {
            baseUI.displayMessage("Filters applied: "); // Show which filters were set
            for (Map.Entry<String, Object> entry : filters.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                // Format the value nicely (especially for enums)
                String valueStr = (value instanceof Enum) ? ((Enum<?>) value).name() : value.toString();
                System.out.println("  - " + key + ": " + valueStr);
            }
        }
        return filters;
    }

    /**
     * Displays the availability of different flat types for a given project.
     * 
     * @param project The project whose flat availability should be displayed.
     */
    public void displayFlatAvailability(Project project) {
        if (project == null) {
            baseUI.displayError("Cannot display flat availability for a null project.");
            return;
        }
        baseUI.displayMessage("\n--- Available Flats for Project " + project.getProjectId() + " ("
                + project.getProjectName() + ")" + " ---");
        Map<FlatType, ProjectFlatInfo> flatInfoMap = project.getFlatTypes();
        boolean flatsAvailable = false;

        if (flatInfoMap == null || flatInfoMap.isEmpty()) {
            baseUI.displayMessage("  (No flat type information available for this project)");
        } else {
            for (Map.Entry<FlatType, ProjectFlatInfo> entry : flatInfoMap.entrySet()) {
                // Display all types, showing remaining units
                baseUI.displayMessage(String.format("  Type: %-12s | Remaining: %d",
                        entry.getKey().name() + ":",
                        entry.getValue().getRemainingUnits()));
                if (entry.getValue().getRemainingUnits() > 0) {
                    flatsAvailable = true;
                }
            }
            if (!flatsAvailable) {
                baseUI.displayMessage("\nWarning: No flats seem available according to current data!");
            }
        }
        baseUI.displayMessage("----------------------------------");
    }
}