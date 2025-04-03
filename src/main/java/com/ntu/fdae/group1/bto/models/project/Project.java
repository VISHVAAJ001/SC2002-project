package com.ntu.fdae.group1.bto.models.project;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import com.ntu.fdae.group1.bto.enums.FlatType;

public class Project {
    private String projectId;
    private String projectName;
    private String neighborhood;
    private Map<FlatType, ProjectFlatInfo> flatTypes;
    private LocalDate openingDate;
    private LocalDate closingDate;
    private String managerInChargeNRIC;
    private int maxOfficerSlots = 10;
    private boolean isVisible = true;
    private List<String> approvedOfficerNRICs;

    public Project(String projectId, String projectName, String neighborhood, Map<FlatType, ProjectFlatInfo> flatTypes,
            LocalDate openingDate, LocalDate closingDate, String managerInChargeNRIC) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.flatTypes = flatTypes;
        this.openingDate = openingDate;
        this.closingDate = closingDate;
        this.managerInChargeNRIC = managerInChargeNRIC;
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

    public String getManagerInChargeNRIC() {
        return managerInChargeNRIC;
    }

    public boolean isVisible() {
        return isVisible;
    }

    public List<String> getApprovedOfficerNRICs() {
        return approvedOfficerNRICs;
    }

    public void setVisibility(boolean visible) {
        this.isVisible = visible;
    }

    public boolean addApprovedOfficer(String nric) {
        if (approvedOfficerNRICs.size() < maxOfficerSlots) {
            approvedOfficerNRICs.add(nric);
            return true;
        }
        return false;
    }

    public void removeApprovedOfficer(String nric) {
        approvedOfficerNRICs.remove(nric);
    }

    public ProjectFlatInfo getFlatInfo(FlatType flatType) {
        return flatTypes.get(flatType);
    }

}
