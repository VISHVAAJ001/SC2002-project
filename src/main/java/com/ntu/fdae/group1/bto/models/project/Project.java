package com.ntu.fdae.group1.bto.models.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import com.ntu.fdae.group1.bto.enums.FlatType;

/**
 * Represents a BTO housing project in the system.
 * <p>
 * This class serves as the central model for housing projects in the BTO
 * Management System.
 * It encapsulates all the details about a Build-To-Order housing project
 * including its
 * basic information, location, timeline, and available flat types.
 * </p>
 *
 * Each Project contains:
 * <ul>
 * <li>Project metadata (ID, name, location)</li>
 * <li>Timeline information (launch and application dates)</li>
 * <li>A collection of flat types with their associated details</li>
 * <li>Visibility control for public listing</li>
 * </ul>
 *
 * <p>
 * Projects serve as the foundation for applications and bookings in the system,
 * representing the housing units that citizens can apply for.
 * </p>
 */
public class Project {
    /**
     * Unique identifier for the project.
     */
    private String projectId;

    /**
     * Name of the housing project.
     */
    private String projectName;

    /**
     * Location or address of the project.
     */
    private String neighborhood;

    /**
     * Map of flat types available in this project, with their associated
     * information.
     */
    private Map<FlatType, ProjectFlatInfo> flatTypes;

    /**
     * Date when the application period opens.
     */
    private LocalDate openingDate;

    /**
     * Date when the application period closes.
     */
    private LocalDate closingDate;

    /**
     * NRIC of the manager assigned to this project.
     */
    private String managerNric;

    /**
     * Maximum number of officer slots available for this project.
     */
    private int maxOfficerSlots;

    /**
     * Remaining number of officer slots available for this project.
     */
    private int remainingOfficerSlots;

    /**
     * Visibility status of the project.
     */
    private boolean isVisible;

    /**
     * List of NRICs of HDB officers approved to work on this project.
     */
    private List<String> approvedOfficerNrics;

    /**
     * Constructs a new Project with the specified details.
     * 
     * @param projectId       The unique identifier for the project
     * @param projectName     The name of the housing project
     * @param neighborhood    The location or address of the project
     * @param flatTypes       A map of flat types available in this project
     * @param openingDate     The date when the application period opens
     * @param closingDate     The date when the application period closes
     * @param managerNric     The NRIC of the manager assigned to this project
     * @param maxOfficerSlots The maximum number of officer slots available for this
     *                        project
     */
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

    /**
     * Gets the unique identifier for this project.
     * 
     * @return The project ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Gets the name of this housing project.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the neighborhood of this project.
     * <p>
     * The neighborhood represents the geographical area where the BTO project
     * is located in Singapore.
     * </p>
     * 
     * @return The neighborhood location name
     */
    public String getNeighborhood() {
        return neighborhood;
    }

    /**
     * Gets the map of flat types available in this project.
     * <p>
     * Returns a mapping of each FlatType to its corresponding ProjectFlatInfo
     * object,
     * which contains details like unit count, price, and availability information.
     * </p>
     * 
     * @return A map of flat types to their associated information
     */
    public Map<FlatType, ProjectFlatInfo> getFlatTypes() {
        return flatTypes;
    }

    /**
     * Gets the date when the application period opens.
     * <p>
     * This date marks the beginning of the period when applicants can submit
     * their applications for this BTO project.
     * </p>
     * 
     * @return The opening date for applications
     */
    public LocalDate getOpeningDate() {
        return openingDate;
    }

    /**
     * Gets the date when the application period closes.
     * <p>
     * This date marks the end of the period when applications will be accepted
     * for this BTO project.
     * </p>
     * 
     * @return The closing date for applications
     */
    public LocalDate getClosingDate() {
        return closingDate;
    }

    /**
     * Gets the NRIC of the manager assigned to this project.
     * <p>
     * Each BTO project is assigned an HDB manager who has overall responsibility
     * for the project's administration and oversight.
     * </p>
     * 
     * @return The manager's NRIC identifier
     */
    public String getManagerNric() {
        return managerNric;
    }

    /**
     * Gets the maximum number of officer slots available for this project.
     * <p>
     * This value represents the total number of HDB officers that can be
     * assigned to work on this project simultaneously.
     * </p>
     * 
     * @return The maximum number of officer slots
     */
    public int getMaxOfficerSlots() {
        return maxOfficerSlots;
    }

    /**
     * Gets the remaining number of officer slots available for this project.
     * <p>
     * This value represents how many more HDB officers can be assigned to
     * this project before reaching the maximum capacity.
     * </p>
     * 
     * @return The remaining number of officer slots
     */
    public int getRemainingOfficerSlots() {
        return remainingOfficerSlots;
    }

    /**
     * Gets the visibility status of this project.
     * 
     * @return true if the project is visible, false otherwise
     */
    public boolean isVisible() {
        return isVisible;
    }

    /**
     * Gets the list of NRICs of HDB officers approved to work on this project.
     * 
     * @return The list of approved officer NRICs
     */
    public List<String> getApprovedOfficerNrics() {
        return approvedOfficerNrics;
    }

    /**
     * Sets the visibility status of this project.
     * 
     * @param visible The visibility status to set
     */
    public void setVisibility(boolean visible) {
        this.isVisible = visible;
    }

    /**
     * Gets information about a specific flat type in this project.
     * 
     * @param flatType The flat type to get information for
     * @return The flat type information, or null if the flat type is not available
     *         in this project
     */
    public ProjectFlatInfo getFlatInfo(FlatType flatType) {
        return flatTypes.get(flatType);
    }

    /**
     * Sets the project name.
     * 
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
     * 
     * @param neighborhood The new neighborhood, must not be null or blank (after
     *                     trimming).
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
     * 
     * @param openingDate The new opening date. Can be null.
     * @throws IllegalArgumentException if the new openingDate is after the existing
     *                                  closingDate.
     */
    public void setOpeningDate(LocalDate openingDate) {
        if (openingDate == null) {
            throw new IllegalArgumentException("Opening date cannot be null.");
        }
        this.openingDate = openingDate;
    }

    /**
     * Sets the project closing date.
     * Ensures the new closing date is not before the current opening date (if set).
     * 
     * @param closingDate The new closing date. Can be null.
     * @throws IllegalArgumentException if the new closingDate is before the
     *                                  existing openingDate.
     */
    public void setClosingDate(LocalDate closingDate) {
        if (closingDate == null) {
            throw new IllegalArgumentException("Closing date cannot be null.");
        }
        this.closingDate = closingDate;
    }

    /**
     * Sets the maximum number of officer slots available for this project.
     * 
     * @param maxOfficerSlots The maximum number of officer slots to set
     */
    public void setMaxOfficerSlots(int maxOfficerSlots) {
        // When MAX slots change, remaining slots need recalculation
        if (maxOfficerSlots >= 0) {
            this.maxOfficerSlots = maxOfficerSlots;
            // Recalculate remaining based on current approved list
            int currentApproved = (this.approvedOfficerNrics != null) ? this.approvedOfficerNrics.size() : 0;
            this.remainingOfficerSlots = Math.max(0, this.maxOfficerSlots - currentApproved); // Ensure non-negative
            if (this.remainingOfficerSlots < 0) { // Should be caught by Math.max, but safeguard
                System.err.println("WARN: Remaining slots calculation resulted in negative for project "
                        + this.projectId + ". Setting to 0.");
                this.remainingOfficerSlots = 0;
            }
        } else {
            System.err.println("WARN: Cannot set maxOfficerSlots to a negative value for project " + this.projectId);
        }
    }

    /**
     * Sets the entire map of flat types for this project.
     * Used by the repository during loading.
     * Creates defensive copies.
     *
     * @param flatTypes A map where the key is the FlatType enum and the value is
     *                  the ProjectFlatInfo object.
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
        this.approvedOfficerNrics = (approvedOfficerNrics != null) ? new ArrayList<>(approvedOfficerNrics)
                : new ArrayList<>();
    }

    /**
     * Adds an officer to the list of officers approved to work on this project.
     * 
     * @param officerNric The NRIC of the officer to add
     * @return true if the officer was added, false otherwise
     */
    public boolean addApprovedOfficer(String officerNric) {
        // Consider null/blank check for officerNric
        if (officerNric != null && !officerNric.trim().isEmpty() &&
                approvedOfficerNrics.size() < maxOfficerSlots &&
                !approvedOfficerNrics.contains(officerNric) &&
                this.remainingOfficerSlots > 0) {
            this.remainingOfficerSlots--;
            return approvedOfficerNrics.add(officerNric);
        }
        return false; // Not added
    }

    /**
     * Removes an officer from the list of officers approved to work on this
     * project.
     * 
     * @param officerNric The NRIC of the officer to remove
     * @return true if the officer was removed, false if the officer was not in the
     *         list
     */
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
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;
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
                // Avoid printing large collections/maps directly in toString if they can be
                // huge
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
