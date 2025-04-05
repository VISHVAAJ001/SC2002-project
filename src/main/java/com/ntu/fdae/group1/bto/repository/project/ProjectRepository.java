package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtils;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectRepository implements IProjectRepository {
    private static final String PROJECT_FILE_PATH = "data/projects.csv";
    private static final String FLAT_INFO_FILE_PATH = "data/projects_flat_info.csv";

    private Map<String, Project> projects;

    public ProjectRepository() {
        this.projects = new HashMap<>();
    }

    @Override
    public Project findById(String projectId) {
        return projects.get(projectId);
    }

    @Override
    public Map<String, Project> findAll() {
        return new HashMap<>(projects);
    }

    @Override
    public void save(Project project) {
        projects.put(project.getProjectId(), project);
        saveAll(projects);
    }

    @Override
    public void saveAll(Map<String, Project> entities) {
        this.projects = entities;
        try {
            // Save main project data
            FileUtils.writeCsvLines(PROJECT_FILE_PATH, serializeProjects(), getProjectCsvHeader());

            // Save flat info data
            FileUtils.writeCsvLines(FLAT_INFO_FILE_PATH, serializeFlatInfos(), getFlatInfoCsvHeader());
        } catch (IOException e) {
            throw new DataAccessException("Error saving projects to file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Project> loadAll() throws DataAccessException {
        try {
            List<String[]> projectData = FileUtils.readCsvLines(PROJECT_FILE_PATH);
            List<String[]> flatInfoData = FileUtils.readCsvLines(FLAT_INFO_FILE_PATH);
            projects = deserializeProjects(projectData, flatInfoData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading projects from file: " + e.getMessage(), e);
        }
        return projects;
    }

    // Helper methods for serialization/deserialization
    private String[] getProjectCsvHeader() {
        return new String[] {
                "projectId", "projectName", "neighborhood", "openingDate", "closingDate",
                "managerNric", "maxOfficerSlots", "isVisible", "approvedOfficerNrics"
        };
    }

    private String[] getFlatInfoCsvHeader() {
        return new String[] {
                "flatInfoId", "projectId", "typeName", "totalUnits", "remainingUnits", "price"
        };
    }

    private Map<String, Project> deserializeProjects(List<String[]> projectData, List<String[]> flatInfoData) {
        Map<String, Project> projectMap = new HashMap<>();
        Map<String, Map<FlatType, ProjectFlatInfo>> flatInfoMap = new HashMap<>();

        // First process all flat info data and organize by project ID
        if (flatInfoData != null && !flatInfoData.isEmpty()) {
            for (String[] row : flatInfoData) {
                if (row.length < 6)
                    continue; // Skip invalid rows

                try {
                    String projectId = row[1];
                    FlatType flatType = FileUtils.parseEnum(FlatType.class, row[2]);
                    int totalUnits = FileUtils.parseIntOrDefault(row[3], 0);
                    int remainingUnits = FileUtils.parseIntOrDefault(row[4], 0);
                    double price = FileUtils.parseDoubleOrDefault(row[5], 0.0);

                    ProjectFlatInfo flatInfo = new ProjectFlatInfo(flatType, totalUnits, remainingUnits, price);

                    // Group flat info by project ID in a map of FlatType -> ProjectFlatInfo
                    flatInfoMap.computeIfAbsent(projectId, k -> new HashMap<>()).put(flatType, flatInfo);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing flat info data: " + e.getMessage());
                }
            }
        }

        // Then process project data and create projects with their flat types
        if (projectData != null && !projectData.isEmpty()) {
            for (String[] row : projectData) {
                if (row.length < 8)
                    continue; // Skip invalid rows

                try {
                    String projectId = row[0];
                    String projectName = row[1];
                    String neighborhood = row[2];
                    LocalDate openingDate = FileUtils.parseLocalDate(row[3]);
                    LocalDate closingDate = FileUtils.parseLocalDate(row[4]);
                    String managerNric = row[5];
                    int maxOfficerSlots = FileUtils.parseIntOrDefault(row[6], 0);
                    boolean isVisible = Boolean.parseBoolean(row[7]);

                    // Get the flat types map for this project
                    Map<FlatType, ProjectFlatInfo> projectFlatTypes = flatInfoMap.getOrDefault(projectId,
                            new HashMap<>());

                    // Create the project with the flat types in the constructor
                    Project project = new Project(
                            projectId, projectName, neighborhood,
                            projectFlatTypes, openingDate, closingDate,
                            managerNric, maxOfficerSlots);

                    project.setVisibility(isVisible);

                    // Add approved officers if any
                    if (row.length > 8 && row[8] != null && !row[8].trim().isEmpty()) {
                        List<String> approvedOfficers = FileUtils.splitString(row[8], ";");
                        for (String officerNric : approvedOfficers) {
                            if (!officerNric.trim().isEmpty()) {
                                project.addApprovedOfficer(officerNric.trim());
                            }
                        }
                    }

                    projectMap.put(projectId, project);
                } catch (IllegalArgumentException e) {
                    System.err.println("Error parsing project data: " + e.getMessage());
                }
            }
        }

        return projectMap;
    }

    private List<String[]> serializeProjects() {
        List<String[]> serializedData = new ArrayList<>();

        for (Project project : projects.values()) {
            String approvedOfficers = String.join(";", project.getApprovedOfficerNrics());

            serializedData.add(new String[] {
                    project.getProjectId(),
                    project.getProjectName(),
                    project.getNeighborhood(),
                    FileUtils.formatLocalDate(project.getOpeningDate()),
                    FileUtils.formatLocalDate(project.getClosingDate()),
                    project.getManagerNric(),
                    String.valueOf(project.getMaxOfficerSlots()),
                    String.valueOf(project.isVisible()),
                    approvedOfficers
            });
        }

        return serializedData;
    }

    private List<String[]> serializeFlatInfos() {
        List<String[]> serializedData = new ArrayList<>();
        int flatInfoIdCounter = 1;

        for (Project project : projects.values()) {
            for (Map.Entry<FlatType, ProjectFlatInfo> entry : project.getFlatTypes().entrySet()) {
                ProjectFlatInfo flatInfo = entry.getValue();
                String flatInfoId = String.format("FLAT%03d", flatInfoIdCounter++);

                serializedData.add(new String[] {
                        flatInfoId,
                        project.getProjectId(),
                        entry.getKey().toString(),
                        String.valueOf(flatInfo.getTotalUnits()),
                        String.valueOf(flatInfo.getRemainingUnits()),
                        String.valueOf(flatInfo.getPrice())
                });
            }
        }

        return serializedData;
    }
}