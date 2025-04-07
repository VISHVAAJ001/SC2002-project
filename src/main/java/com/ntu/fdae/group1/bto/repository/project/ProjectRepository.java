package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ProjectRepository implements IProjectRepository {
    private static final String PROJECT_FILE_PATH = "resources/projects.csv";
    private static final String FLAT_INFO_FILE_PATH = "resources/projects_flat_info.csv";

    private Map<String, Project> projects;
    private Set<String> loadedFlatInfoIds = new HashSet<>();

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
            FileUtil.writeCsvLines(PROJECT_FILE_PATH, serializeProjects(), getProjectCsvHeader());

            // Save flat info data
            FileUtil.writeCsvLines(FLAT_INFO_FILE_PATH, serializeFlatInfos(), getFlatInfoCsvHeader());
        } catch (IOException e) {
            throw new DataAccessException("Error saving projects to file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Project> loadAll() throws DataAccessException {
        this.projects.clear();
        this.loadedFlatInfoIds.clear();

        try {
            List<String[]> projectData = FileUtil.readCsvLines(PROJECT_FILE_PATH);
            List<String[]> flatInfoData = FileUtil.readCsvLines(FLAT_INFO_FILE_PATH);
            projects = deserializeProjects(projectData, flatInfoData, this.loadedFlatInfoIds);
        } catch (IOException e) {
            throw new DataAccessException("Error loading projects from file: " + e.getMessage(), e);
        }
        return projects;
    }

    @Override
    public Set<String> findAllFlatInfoIds() throws DataAccessException {
        if (projects.isEmpty() && loadedFlatInfoIds.isEmpty()) {
            loadAll();
        }
        return new HashSet<>(this.loadedFlatInfoIds);
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

    private Map<String, Project> deserializeProjects(List<String[]> projectData, List<String[]> flatInfoData,
            Set<String> foundFlatInfoIds) {
        Map<String, Project> projectMap = new HashMap<>();
        Map<String, Map<FlatType, ProjectFlatInfo>> flatInfoMap = new HashMap<>();

        // First process all flat info data and organize by project ID
        if (flatInfoData != null && !flatInfoData.isEmpty()) {
            for (String[] row : flatInfoData) {
                if (row.length < 6)
                    continue; // Skip invalid rows

                try {
                    String flatInfoId = row[0];
                    String projectId = row[1];
                    FlatType flatType = FileUtil.parseEnum(FlatType.class, row[2]);
                    int totalUnits = FileUtil.parseIntOrDefault(row[3], 0);
                    int remainingUnits = FileUtil.parseIntOrDefault(row[4], 0);
                    double price = FileUtil.parseDoubleOrDefault(row[5], 0.0);

                    foundFlatInfoIds.add(flatInfoId);

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
                    LocalDate openingDate = FileUtil.parseLocalDate(row[3]);
                    LocalDate closingDate = FileUtil.parseLocalDate(row[4]);
                    String managerNric = row[5];
                    int maxOfficerSlots = FileUtil.parseIntOrDefault(row[6], 0);
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
                        List<String> approvedOfficers = FileUtil.splitString(row[8], ";");
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
                    FileUtil.formatLocalDate(project.getOpeningDate()),
                    FileUtil.formatLocalDate(project.getClosingDate()),
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

    @Override
    public void deleteById(String projectId) throws DataAccessException {
        // 1. Basic validation
        if (projectId == null || projectId.trim().isEmpty()) {
            System.err.println("Warning: Attempted to delete project with null or empty ID.");
            return;
        }

        // 2. Remove project from the in-memory map
        Project removedProject = this.projects.remove(projectId);

        // 3. Check if the project existed in the map
        if (removedProject != null) {
            System.out.println("Project deleted from memory: " + projectId);

            // 4. Persist the changes to BOTH files
            try {
                // a) Save the updated projects map (excluding the deleted one)
                // We can reuse the existing serialize/save logic, as it writes the current map
                // state.
                FileUtil.writeCsvLines(PROJECT_FILE_PATH, serializeProjects(), getProjectCsvHeader());

                // b) *** CRITICAL: Update and save the flat info data ***
                // We need to filter out the flat infos related to the deleted project
                // and then save the remaining ones.

                // Get all current flat infos (from all *remaining* projects)
                List<String[]> remainingFlatInfos = serializeFlatInfos(); // This already iterates over the *current*
                                                                          // `projects` map

                // Write the filtered flat info data back to the file
                FileUtil.writeCsvLines(FLAT_INFO_FILE_PATH, remainingFlatInfos, getFlatInfoCsvHeader());

            } catch (IOException e) {
                // If saving fails, the in-memory state might be inconsistent with files.
                // Consider rolling back the in-memory deletion? (More complex)
                // For now, propagate the error.
                System.err.println("Error persisting deletion for project: " + projectId);
                throw new DataAccessException(
                        "Error saving state after deleting project " + projectId + ": " + e.getMessage(), e);
            }
        } else {
            System.out.println("Project not found for deletion: " + projectId);
            // No changes to persist if project wasn't found
        }
    }
}