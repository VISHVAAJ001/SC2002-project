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
    private boolean isVisible;
    private List<String> approvedOfficerNrics;

    public Project(String projectId, String projectName, String neighborhood,
            Map<FlatType, ProjectFlatInfo> flatTypes, LocalDate openingDate,
            LocalDate closingDate, String managerNric, int maxOfficerSlots) {
        this.projectId = Objects.requireNonNull(projectId, "Project ID cannot be null");
        this.projectName = Objects.requireNonNull(projectName, "Project Name cannot be null");
        this.neighborhood = Objects.requireNonNull(neighborhood, "Neighborhood cannot be null");
        // Ensure the map passed in is used, create new if null
        this.flatTypes = flatTypes != null ? new HashMap<>(flatTypes) : new HashMap<>();
        this.openingDate = openingDate; 
        this.closingDate = closingDate; 
        this.managerNric = Objects.requireNonNull(managerNric, "Manager NRIC cannot be null");
        this.maxOfficerSlots = maxOfficerSlots;
        this.isVisible = false; // Default setting to false
        this.approvedOfficerNrics = new ArrayList<>(); // Initialize as empty list
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

    public boolean isVisible() {
        return isVisible; 
    }

    public List<String> getApprovedOfficerNrics() {
        return approvedOfficerNrics; 
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
        this.projectName = projectName.trim(); // Trim whitespace
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
        this.neighborhood = neighborhood.trim(); // Trim whitespace
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
        // Add validation (e.g., non-negative, potentially not less than current approved count)
        if (maxOfficerSlots >= 0) { // Basic non-negative check
            // More complex check: if (maxOfficerSlots >= this.approvedOfficerNrics.size())
            this.maxOfficerSlots = maxOfficerSlots;
        } else {
            System.err.println("WARN: Cannot set maxOfficerSlots to a negative value ("+ maxOfficerSlots +") for project " + this.projectId);
        }
    }

    public void setVisibility(boolean visible) {
        this.isVisible = visible;
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
        if (officerNric != null && !officerNric.isBlank() &&
            approvedOfficerNrics.size() < maxOfficerSlots &&
            !approvedOfficerNrics.contains(officerNric))
        {
            return approvedOfficerNrics.add(officerNric); 
        }
        return false; // Not added
    }

    public boolean removeApprovedOfficer(String officerNric) {
        return approvedOfficerNrics.remove(officerNric); // remove returns boolean
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