package com.ntu.fdae.group1.bto.views; // Or views.helpers

import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for officer registration related UI operations in the BTO
 * Management System.
 * <p>
 * This class provides reusable UI components for displaying and managing
 * officer
 * registration data, including:
 * - Displaying lists of officer registrations
 * - Showing detailed information about specific registrations
 * - Formatting registration data for user-friendly display
 * </p>
 * <p>
 * The helper follows a composition pattern, working with a BaseUI instance for
 * common UI operations and a ProjectController for retrieving project
 * information
 * related to registrations.
 * </p>
 */
public class OfficerRegUIHelper {

    /**
     * The base UI component for common UI operations.
     */
    private final BaseUI baseUI;

    /**
     * The controller for retrieving project information related to registrations.
     */
    private final ProjectController projectController;

    /**
     * The controller for user-related to get user information.
     */
    private final UserController userController;

    /**
     * Constructs a new OfficerRegUIHelper with the specified dependencies.
     *
     * @param baseUI            An instance of BaseUI for console I/O operations
     * @param projectController Controller to fetch project details
     * @throws NullPointerException if either parameter is null
     */
    public OfficerRegUIHelper(BaseUI baseUI, ProjectController projectController, UserController userController) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null for OfficerRegUIHelper");
        this.projectController = Objects.requireNonNull(projectController,
                "ProjectController cannot be null for OfficerRegUIHelper");
        this.userController = Objects.requireNonNull(userController,
                "UserController cannot be null for OfficerRegUIHelper");
    }

    /**
     * Displays a formatted list of officer registrations and provides a mapping for
     * selection.
     * <p>
     * This method displays each registration with its key details including ID,
     * officer,
     * project name, status, and date, and returns a map that associates the display
     * index with the corresponding registration object for easy selection.
     * </p>
     *
     * @param regs  List of registrations to display
     * @param title Title for the list header
     * @return Map where key is the displayed number, value is the
     *         OfficerRegistration; empty map if list is null/empty
     */
    public Map<Integer, OfficerRegistration> displayOfficerRegList(List<OfficerRegistration> regs, String title) {
        this.baseUI.displayHeader(title);
        Map<Integer, OfficerRegistration> regMap = new HashMap<>();
        if (regs == null || regs.isEmpty()) {
            this.baseUI.displayMessage("No registrations to display in this list."); // Use the baseUI field
            return regMap;
        }

        int index = 1;
        for (OfficerRegistration reg : regs) {
            // Fetch project name for context
            Project proj = projectController.findProjectById(reg.getProjectId());
            String projName = (proj != null) ? proj.getProjectName() : "Unknown/Deleted";

            // Format the string for display
            String formattedString = String.format(
                    "[%d] RegID: %-10s | Officer: %-9s (%s) | Project: %s (%s) | Status: %-9s | Date: %s",
                    index,
                    reg.getRegistrationId(),
                    reg.getOfficerNric(),
                    userController.getUserName(reg.getOfficerNric()),
                    projName, // Display name
                    reg.getProjectId(), // Display ID
                    reg.getStatus(),
                    this.baseUI.formatDateSafe(reg.getRequestDate()));
            this.baseUI.displayMessage(formattedString);
            regMap.put(index, reg);
            index++;
        }
        this.baseUI.displayMessage("[0] Back / Cancel");
        return regMap;
    }

    /**
     * Displays detailed information about a specific officer registration.
     * <p>
     * Shows comprehensive information about a single registration, including
     * its ID, associated officer, project details, request date, and current
     * status.
     * </p>
     *
     * @param reg The registration object to display details for
     */
    public void displayOfficerRegistrationDetails(OfficerRegistration reg) {
        if (reg == null) {
            this.baseUI.displayError("No registration details to display.");
            return;
        }
        this.baseUI.displayHeader("Officer Registration Details (ID: " + reg.getRegistrationId() + ")");
        Project project = projectController.findProjectById(reg.getProjectId());
        this.baseUI.displayMessage("Officer NRIC:    " + reg.getOfficerNric());
        this.baseUI.displayMessage("Project ID:      " + reg.getProjectId());
        this.baseUI.displayMessage("Project Name:    " + (project != null ? project.getProjectName() : "N/A"));
        this.baseUI.displayMessage("Request Date:    " + this.baseUI.formatDateSafe(reg.getRequestDate()));
        this.baseUI.displayMessage("Current Status:  " + reg.getStatus());
        this.baseUI.displayMessage("----------------------------------");
    }

    /**
     * Displays a formatted list of officer registrations for viewing purposes only.
     * <p>
     * Unlike displayOfficerRegList, this method is designed for view-only scenarios
     * where user selection is not required. It displays registrations in a tabular
     * format with columns for key details.
     * </p>
     *
     * @param regs  List of registrations to display
     * @param title Title for the list header
     */
    public void displayOfficerRegListForViewing(List<OfficerRegistration> regs, String title) {
        this.baseUI.displayHeader(title);
        if (regs == null || regs.isEmpty()) {
            this.baseUI.displayMessage("No registrations found in this list.");
            return;
        }

        this.baseUI.displayMessage(String.format("%-10s | %-15s | %-20s | %-12s | %s",
                "Reg ID", "Project ID", "Project Name", "Request Date", "Status"));
        this.baseUI.displayMessage("-------------------------------------------------------------------------"); // Adjust
                                                                                                                 // separator
                                                                                                                 // length

        regs.forEach(reg -> {
            Project proj = projectController.findProjectById(reg.getProjectId());
            String projName = (proj != null) ? proj.getProjectName() : "Unknown/Deleted";
            this.baseUI.displayMessage(String.format("%-10s | %-15s | %-20s | %-12s | %s",
                    reg.getRegistrationId(),
                    reg.getProjectId(),
                    projName,
                    this.baseUI.formatDateSafe(reg.getRequestDate()),
                    reg.getStatus().name()));
        });
        this.baseUI.displayMessage("-------------------------------------------------------------------------");
    }
}