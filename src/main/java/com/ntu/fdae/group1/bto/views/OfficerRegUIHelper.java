package com.ntu.fdae.group1.bto.views; // Or views.helpers

import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for displaying Officer Registration related lists and details.
 * Uses composition with BaseUI for console interactions.
 */
public class OfficerRegUIHelper {

    private final BaseUI baseUI; // Instance for console I/O
    private final ProjectController projectController; // To get project names

    /**
     * Constructor for OfficerRegUIHelper.
     * @param baseUI An instance of BaseUI (or subclass) for console I/O.
     * @param projectController Controller to fetch project details.
     */
    public OfficerRegUIHelper(BaseUI baseUI, ProjectController projectController) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null for OfficerRegUIHelper");
        this.projectController = Objects.requireNonNull(projectController, "ProjectController cannot be null for OfficerRegUIHelper");
    }

    /**
     * Displays a formatted list of officer registrations and returns a map for selection.
     * @param regs List of registrations to display.
     * @param title Title for the list header.
     * @return Map where key is the displayed number, value is the OfficerRegistration. Empty map if list is null/empty.
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
                    "[%d] RegID: %-10s | Officer: %-9s | Project: %s (%s) | Status: %-9s | Date: %s",
                    index,
                    reg.getRegistrationId(),
                    reg.getOfficerNric(),
                    projName, // Display name
                    reg.getProjectId(), // Display ID
                    reg.getStatus(),
                    this.baseUI.formatDateSafe(reg.getRequestDate())
            );
            this.baseUI.displayMessage(formattedString);
            regMap.put(index, reg);
            index++;
        }
        this.baseUI.displayMessage("[0] Back / Cancel");
        return regMap;
    }

    /**
    * Displays details for a single OfficerRegistration.
    * @param reg The registration object to display details for.
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
    * Displays a formatted list of officer registrations for viewing purposes.
    * @param regs List of registrations to display.
    * @param title Title for the list header.
    */
    public void displayOfficerRegListForViewing(List<OfficerRegistration> regs, String title) {
        this.baseUI.displayHeader(title);
        if (regs == null || regs.isEmpty()) {
            this.baseUI.displayMessage("No registrations found in this list.");
            return;
        }

        this.baseUI.displayMessage(String.format("%-10s | %-15s | %-20s | %-12s | %s",
            "Reg ID", "Project ID", "Project Name", "Request Date", "Status"));
        this.baseUI.displayMessage("-------------------------------------------------------------------------"); // Adjust separator length

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