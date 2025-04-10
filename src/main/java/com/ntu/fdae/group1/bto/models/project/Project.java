package com.ntu.fdae.group1.bto.models.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ntu.fdae.group1.bto.enums.FlatType;

public class Project {
    private String projectId;
    private String projectName;
    private String neighborhood;
    private Map<FlatType, ProjectFlatInfo> flatTypes;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String managerNric;
    private int maxOfficerSlots;
    private int remainingOfficerSlots;
    private boolean isVisible;
    private List<String> approvedOfficerNrics;

    public Project(String projectId, String projectName, String neighborhood,
            Map<FlatType, ProjectFlatInfo> flatTypes, LocalDate openingDate,
            LocalDate closingDate, String managerNric, int maxOfficerSlots) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypes = flatTypes != null ? new HashMap<>(flatTypes) : new HashMap<>();
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.managerNric = managerNric;
        this.maxOfficerSlots = maxOfficerSlots;
        this.remainingOfficerSlots = this.maxOfficerSlots;
        this.isVisible = true; // Default is visible
        this.approvedOfficerNrics = new ArrayList<>();

        if (maxOfficerSlots < 0) {
            System.err.println("WARN: Max officer slots cannot be negative. Setting to 0 for project " + projectId);
            this.maxOfficerSlots = 0;
       } else {
            this.maxOfficerSlots = maxOfficerSlots;
       }
    }

    public String getProjectId() {
        return projectId;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getNeighborhood() {
        return neighborhood;
    }

    public Map<FlatType, ProjectFlatInfo> getFlatTypes() {
        return flatTypes;
    }

    public LocalDate getOpeningDate() {
        return openingDate;
    }

    public LocalDate getClosingDate() {
        return closingDate;
    }

    public String getManagerNric() {
        return managerNric;
    }

    public int getMaxOfficerSlots() {
        return maxOfficerSlots;
    }

    public int getRemainingOfficerSlots() {
        return remainingOfficerSlots; 
    }

    public boolean isVisible() {
        return isVisible;
    }

    public List<String> getApprovedOfficerNrics() {
        return approvedOfficerNrics;
    }

    public void setVisibility(boolean visible) {
        this.isVisible = visible;
    }

    public ProjectFlatInfo getFlatInfo(FlatType flatType) {
        return flatTypes.get(flatType);
    }

     // --- Setters with Validation ---

    /**
     * Sets the project name.
     * @param projectName The new name, must not be null or blank (after trimming).
     * @throws IllegalArgumentException if projectName is null or blank.
     */
    public void setProjectName(String projectName) {
        if (projectName == null || projectName.trim().isEmpty()) {
            throw new IllegalArgumentException("Project Name cannot be null or empty.");
        }
        this.projectName = projectName.trim(); 
    }

    /**
     * Sets the project neighborhood.
     * @param neighborhood The new neighborhood, must not be null or blank (after trimming).
     * @throws IllegalArgumentException if neighborhood is null or blank.
     */
    public void setNeighborhood(String neighborhood) {
        if (neighborhood == null || neighborhood.trim().isEmpty()) {
            throw new IllegalArgumentException("Neighborhood cannot be null or empty.");
        }
        this.neighborhood = neighborhood.trim(); 
    }

    /**
     * Sets the project opening date.
     * Ensures the new opening date is not after the current closing date (if set).
     * @param openingDate The new opening date. Can be null.
     * @throws IllegalArgumentException if the new openingDate is after the existing closingDate.
     */
    public void setOpeningDate(LocalDate openingDate) {
        // Check against current closing date if both are set
        if (openingDate != null && this.closingDate != null && openingDate.isAfter(this.closingDate)) {
            throw new IllegalArgumentException("Opening Date (" + openingDate + ") cannot be after the current Closing Date (" + this.closingDate + ").");
        }
        this.openingDate = openingDate;
    }

    /**
     * Sets the project closing date.
     * Ensures the new closing date is not before the current opening date (if set).
     * @param closingDate The new closing date. Can be null.
     * @throws IllegalArgumentException if the new closingDate is before the existing openingDate.
     */
    public void setClosingDate(LocalDate closingDate) {
         // Check against current opening date if both are set
         if (closingDate != null && this.openingDate != null && closingDate.isBefore(this.openingDate)) {
            throw new IllegalArgumentException("Closing Date (" + closingDate + ") cannot be before the current Opening Date (" + this.openingDate + ").");
        }
        this.closingDate = closingDate;
    }

    public void setMaxOfficerSlots(int maxOfficerSlots) {
        // When MAX slots change, remaining slots need recalculation
        if (maxOfficerSlots >= 0) {
            this.maxOfficerSlots = maxOfficerSlots;
            // Recalculate remaining based on current approved list
            int currentApproved = (this.approvedOfficerNrics != null) ? this.approvedOfficerNrics.size() : 0;
            this.remainingOfficerSlots = Math.max(0, this.maxOfficerSlots - currentApproved); // Ensure non-negative
            if (this.remainingOfficerSlots < 0) { // Should be caught by Math.max, but safeguard
                 System.err.println("WARN: Remaining slots calculation resulted in negative for project " + this.projectId + ". Setting to 0.");
                 this.remainingOfficerSlots = 0;
            }
        } else {
            System.err.println("WARN: Cannot set maxOfficerSlots to a negative value for project " + this.projectId);
        }
    }


    // --- Setters NEEDED BY ProjectRepository LoadAll ---

    /**
     * Sets the entire map of flat types for this project.
     * Used by the repository during loading.
     * Creates defensive copies.
     *
     * @param flatTypes A map where the key is the FlatType enum and the value is the ProjectFlatInfo object.
     */
    public void setFlatTypes(Map<FlatType, ProjectFlatInfo> flatTypes) {
        // Create a new map defensively, even if input is null
        this.flatTypes = (flatTypes != null) ? new HashMap<>(flatTypes) : new HashMap<>();
    }

    /**
     * Sets the entire list of approved officer NRICs for this project.
     * Used by the repository during loading.
     * Creates defensive copies.
     *
     * @param approvedOfficerNrics A list of strings containing the NRICs.
     */
    public void setApprovedOfficerNrics(List<String> approvedOfficerNrics) {
        // Create a new list defensively, even if input is null
        this.approvedOfficerNrics = (approvedOfficerNrics != null) ? new ArrayList<>(approvedOfficerNrics) : new ArrayList<>();
    }


    // --- Methods for Modifying Lists/Maps ---

    public boolean addApprovedOfficer(String officerNric) {
        // Consider null/blank check for officerNric
        if (officerNric != null && !officerNric.trim().isEmpty() &&
            approvedOfficerNrics.size() < maxOfficerSlots &&
            !approvedOfficerNrics.contains(officerNric) &&
            this.remainingOfficerSlots > 0)
        {
            this.remainingOfficerSlots--;
            return approvedOfficerNrics.add(officerNric); 
        }
        return false; // Not added
    }

    public boolean removeApprovedOfficer(String officerNric) {
        if (this.approvedOfficerNrics.remove(officerNric)) { // If removal was successful
            // <<< INCREMENT on successful remove (but don't exceed max) >>>
            this.remainingOfficerSlots = Math.min(this.maxOfficerSlots, this.remainingOfficerSlots + 1);
            return true;
       }
       return false; // Not removed
    }
    

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Project project = (Project) o;
        return projectId.equals(project.projectId); // Equality based on ID
    }

    @Override
    public int hashCode() {
        return Objects.hash(projectId); // Hash based on ID
    }

    @Override
    public String toString() {
        return "Project{" +
                "projectId='" + projectId + '\'' +
                ", projectName='" + projectName + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                // Avoid printing large collections/maps directly in toString if they can be huge
                ", flatTypesCount=" + (flatTypes != null ? flatTypes.size() : 0) +
                ", openingDate=" + openingDate +
                ", closingDate=" + closingDate +
                ", managerNric='" + managerNric + '\'' +
                ", maxOfficerSlots=" + maxOfficerSlots +
                ", isVisible=" + isVisible +
                ", approvedOfficerCount=" + (approvedOfficerNrics != null ? approvedOfficerNrics.size() : 0) +
                '}';
    }
}
