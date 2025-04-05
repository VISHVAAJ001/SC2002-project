package com.ntu.fdae.group1.bto.models.project;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
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
    private String managerNric;
    private int maxOfficerSlots;
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
        this.isVisible = true; // Default is visible
        this.approvedOfficerNrics = new ArrayList<>();
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

    public void setVisibility(boolean visible) {
        this.isVisible = visible;
    }

    public boolean addApprovedOfficer(String officerNric) {
        if (approvedOfficerNrics.size() < maxOfficerSlots && !approvedOfficerNrics.contains(officerNric)) {
            approvedOfficerNrics.add(officerNric);
            return true;
        }
        return false;
    }

    public boolean removeApprovedOfficer(String officerNric) {
        return approvedOfficerNrics.remove(officerNric);
    }

    public ProjectFlatInfo getFlatInfo(FlatType flatType) {
        return flatTypes.get(flatType);
    }
}
