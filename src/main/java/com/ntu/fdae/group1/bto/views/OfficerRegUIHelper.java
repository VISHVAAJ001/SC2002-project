package com.ntu.fdae.group1.bto.views; // Or views.helpers

import com.ntu.fdae.group1.bto.controllers.project.ProjectController; // Corrected path potentially
import com.ntu.fdae.group1.bto.models.project.Project; // Needed for project name
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Helper class for displaying Officer Registration related lists and details.
 */
public class OfficerRegUIHelper {

    private final BaseUI baseUI;
    private final ProjectController projectController; // To get project names
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE; // Or get from BaseUI

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
        baseUI.displayHeader(title);
        Map<Integer, OfficerRegistration> regMap = new HashMap<>();
        if (regs == null || regs.isEmpty()) {
            baseUI.displayMessage("No registrations to display in this list.");
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
                    reg.getStatus(), // Enum name is usually fine
                    formatDateSafe(reg.getRequestDate()) // Use local/BaseUI formatDate
            );
            baseUI.displayMessage(formattedString); // Use injected BaseUI
            regMap.put(index, reg);
            index++;
        }
        baseUI.displayMessage("[0] Back / Cancel"); // Use injected BaseUI
        return regMap;
    }

     // --- Potentially add displayOfficerRegistrationDetails method here ---
     public void displayOfficerRegistrationDetails(OfficerRegistration reg) {
         if (reg == null) { baseUI.displayError("No registration details to display."); return; }
         baseUI.displayHeader("Officer Registration Details (ID: " + reg.getRegistrationId() + ")");
         Project project = projectController.findProjectById(reg.getProjectId());
         baseUI.displayMessage("Officer NRIC:    " + reg.getOfficerNric());
         baseUI.displayMessage("Project ID:      " + reg.getProjectId());
         baseUI.displayMessage("Project Name:    " + (project != null ? project.getProjectName() : "N/A"));
         baseUI.displayMessage("Request Date:    " + formatDateSafe(reg.getRequestDate()));
         baseUI.displayMessage("Current Status:  " + reg.getStatus());
         baseUI.displayMessage("----------------------------------");
     }

    // Helper for date formatting - remove if BaseUI provides it
    private String formatDateSafe(LocalDate date) {
        return (date == null) ? "N/A" : DATE_FORMATTER.format(date);
    }
}