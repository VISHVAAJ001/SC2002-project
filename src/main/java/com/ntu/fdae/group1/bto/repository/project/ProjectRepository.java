package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.enums.*;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class ProjectRepository implements IProjectRepository {

    // Define file paths (adjust as needed)
    // It's complex to store Map<FlatType, ProjectFlatInfo> and List<String> easily in a single flat CSV.
    // We might need separate files or a more complex serialization format (JSON/XML - though assignment forbids).
    // ---- SIMPLIFIED CSV APPROACH ----
    // Store project core details in one file.
    // Store flat info in another, linked by projectId.
    // Store approved officers in another, linked by projectId.
    private static final String PROJECT_CORE_FILE_PATH = "data/projects_core.csv";
    private static final String PROJECT_FLATS_FILE_PATH = "data/projects_flats.csv";
    private static final String PROJECT_OFFICERS_FILE_PATH = "data/projects_officers.csv";

    private static final String[] CORE_HEADERS = {"projectId", "projectName", "neighborhood", "openingDate", "closingDate", "managerNric", "maxOfficerSlots", "isVisible"};
    private static final String[] FLATS_HEADERS = {"projectId", "flatType", "totalUnits", "remainingUnits", "price"}; // Assuming price exists
    private static final String[] OFFICERS_HEADERS = {"projectId", "officerNric"};

    private final Map<String, Project> projects;

    public ProjectRepository() {
        this.projects = new ConcurrentHashMap<>();
        try {
            loadAll();
        } catch (DataAccessException e) {
            System.err.println("WARN: Failed to load projects on startup: " + e.getMessage());
        }
    }

    @Override
    public Project findById(String id) {
        return projects.get(id);
    }

    @Override
    public Map<String, Project> findAll() {
        return new ConcurrentHashMap<>(projects); // Defensive copy
    }

    @Override
    public void save(Project entity) {
        Objects.requireNonNull(entity, "Cannot save null Project");
        Objects.requireNonNull(entity.getProjectId(), "Project ID cannot be null");
        projects.put(entity.getProjectId(), entity);
        saveAllInternal(); // Persist changes
    }

    @Override
    public void saveAll(Map<String, Project> entities) {
         Objects.requireNonNull(entities, "Cannot save null map of projects");
         this.projects.clear();
         this.projects.putAll(entities);
         saveAllInternal(); // Persist changes
    }

     @Override
     public void deleteById(String id) {
         if (id != null && projects.remove(id) != null) {
            saveAllInternal(); // Persist changes after removal
            System.out.println("INFO: Deleted Project with ID: " + id);
        } else {
            System.err.println("WARN: Attempted to delete non-existent Project with ID: " + id);
        }
     }


    @Override
    public Map<String, Project> loadAll() throws DataAccessException {
        projects.clear();
        Map<String, Project> loadedCore = loadCoreProjects();
        Map<String, Map<FlatType, ProjectFlatInfo>> loadedFlats = loadProjectFlats();
        Map<String, List<String>> loadedOfficers = loadProjectOfficers();

        // Combine the data
        for (Map.Entry<String, Project> entry : loadedCore.entrySet()) {
            String projectId = entry.getKey();
            Project coreProject = entry.getValue();

            // Add flats info
            if (loadedFlats.containsKey(projectId)) {
                // Need a way to set this map in Project - ASSUMING a setter or accessible field
                coreProject.setFlatTypes(loadedFlats.get(projectId)); // ASSUMES setFlatTypes exists
            } else {
                 System.err.println("WARN: No flat info found for project ID: " + projectId);
                 coreProject.setFlatTypes(new HashMap<>()); // Set empty map
            }

            // Add officers info
            if (loadedOfficers.containsKey(projectId)) {
                 // Need a way to set this list in Project - ASSUMING a setter or accessible field
                 coreProject.setApprovedOfficerNrics(loadedOfficers.get(projectId)); // ASSUMES setApprovedOfficerNrics exists
            } else {
                 coreProject.setApprovedOfficerNrics(new ArrayList<>()); // Set empty list
            }
            projects.put(projectId, coreProject);
        }
        System.out.println("INFO: Loaded " + projects.size() + " combined projects.");
        return projects; // Return the internally combined map
    }

    // --- Internal Load/Save Helpers for Multi-File Approach ---

    private void saveAllInternal() {
        List<String[]> coreData = new ArrayList<>();
        List<String[]> flatData = new ArrayList<>();
        List<String[]> officerData = new ArrayList<>();

        for (Project p : projects.values()) {
            // Core data
            coreData.add(new String[] {
                p.getProjectId(), p.getProjectName(), p.getNeighborhood(),
                FileUtil.formatLocalDate(p.getOpeningDate()), FileUtil.formatLocalDate(p.getClosingDate()),
                p.getManagerNric(), String.valueOf(p.getMaxOfficerSlots()), String.valueOf(p.isVisible())
            });
            // Flat data
            if (p.getFlatTypes() != null) {
                for (ProjectFlatInfo info : p.getFlatTypes().values()) {
                    flatData.add(new String[] {
                        p.getProjectId(), info.getFlatType().name(),
                        String.valueOf(info.getTotalUnits()), String.valueOf(info.getRemainingUnits()),
                        String.valueOf(info.getPrice()) // Assuming getPrice exists
                    });
                }
            }
            // Officer data
            if (p.getApprovedOfficerNrics() != null) {
                for (String officerNric : p.getApprovedOfficerNrics()) {
                    officerData.add(new String[] {p.getProjectId(), officerNric});
                }
            }
        }

        // Save to respective files
        try {
            FileUtil.writeCsvLines(PROJECT_CORE_FILE_PATH, coreData, CORE_HEADERS);
            FileUtil.writeCsvLines(PROJECT_FLATS_FILE_PATH, flatData, FLATS_HEADERS);
            FileUtil.writeCsvLines(PROJECT_OFFICERS_FILE_PATH, officerData, OFFICERS_HEADERS);
        } catch (IOException e) {
            System.err.println("FATAL: Error saving project data: " + e.getMessage());
            // Consider throwing DataAccessException or RuntimeException
        }
    }

    private Map<String, Project> loadCoreProjects() throws DataAccessException {
        Map<String, Project> coreMap = new HashMap<>();
         try {
             List<String[]> lines = FileUtil.readCsvLines(PROJECT_CORE_FILE_PATH);
             // Skip header done by FileUtil now

             for (String[] fields : lines) {
                 if (fields.length >= CORE_HEADERS.length) {
                     try {
                        String id = fields[0];
                        String name = fields[1];
                        String neighborhood = fields[2];
                        LocalDate openDate = FileUtil.parseLocalDate(fields[3]);
                        LocalDate closeDate = FileUtil.parseLocalDate(fields[4]);
                        String managerNric = fields[5];
                        int slots = FileUtil.parseIntOrDefault(fields[6], 0);
                        boolean isVisible = Boolean.parseBoolean(fields[7]); // Safe parse

                        // Create project with EMPTY flat/officer lists initially
                        Project p = new Project(id, name, neighborhood, new HashMap<>(), openDate, closeDate, managerNric, slots);
                        p.setVisibility(isVisible); // Set visibility
                        // Officer list initialized in Project constructor
                        coreMap.put(id, p);
                    } catch (Exception e) { // Catch parsing errors per line
                          System.err.println("WARN: Skipping invalid core project line: " + String.join(",", fields) + " - Error: " + e.getMessage());
                    }
                 } else {
                     System.err.println("WARN: Skipping malformed core project line: " + String.join(",", fields));
                 }
             }
         } catch (IOException e) {
             System.err.println("INFO: Core projects file not found or failed to read, starting empty: " + PROJECT_CORE_FILE_PATH + " (" + e.getMessage() + ")");
             // Don't throw, just return empty map for core data
         }
        return coreMap;
    }

    private Map<String, Map<FlatType, ProjectFlatInfo>> loadProjectFlats() throws DataAccessException {
        Map<String, Map<FlatType, ProjectFlatInfo>> flatsMap = new HashMap<>();
        try {
             List<String[]> lines = FileUtil.readCsvLines(PROJECT_FLATS_FILE_PATH);
             // Skip header done by FileUtil now

             for (String[] fields : lines) {
                 if (fields.length >= FLATS_HEADERS.length) {
                    try {
                        String projectId = fields[0];
                        FlatType type = FileUtil.parseEnum(FlatType.class, fields[1]); // Assumes parseEnum exists
                        int total = FileUtil.parseIntOrDefault(fields[2], 0);
                        int remaining = FileUtil.parseIntOrDefault(fields[3], 0);
                        double price = FileUtil.parseDoubleOrDefault(fields[4], 0.0);

                        if (projectId != null && type != null) {
                            ProjectFlatInfo info = new ProjectFlatInfo(type, total, remaining, price);
                            // Add to the map for the project ID
                            flatsMap.computeIfAbsent(projectId, k -> new HashMap<>()).put(type, info);
                        } else {
                             System.err.println("WARN: Skipping invalid project flat line (missing projectId or invalid type): " + String.join(",", fields));
                        }
                    } catch (Exception e) {
                          System.err.println("WARN: Skipping invalid project flat line: " + String.join(",", fields) + " - Error: " + e.getMessage());
                    }
                 } else {
                     System.err.println("WARN: Skipping malformed project flat line: " + String.join(",", fields));
                 }
             }
        } catch (IOException e) {
            System.err.println("INFO: Project flats file not found or failed to read: " + PROJECT_FLATS_FILE_PATH + " (" + e.getMessage() + ")");
             // Don't throw, projects might exist without flats initially
         }
        return flatsMap;
    }

     private Map<String, List<String>> loadProjectOfficers() throws DataAccessException {
        Map<String, List<String>> officersMap = new HashMap<>();
        try {
             List<String[]> lines = FileUtil.readCsvLines(PROJECT_OFFICERS_FILE_PATH);
             // Skip header done by FileUtil now

             for (String[] fields : lines) {
                 if (fields.length >= OFFICERS_HEADERS.length) {
                     String projectId = fields[0];
                     String officerNric = fields[1];

                     if (projectId != null && !projectId.isBlank() && officerNric != null && !officerNric.isBlank()) {
                        officersMap.computeIfAbsent(projectId, k -> new ArrayList<>()).add(officerNric);
                     } else {
                          System.err.println("WARN: Skipping invalid project officer line: " + String.join(",", fields));
                     }
                 } else {
                      System.err.println("WARN: Skipping malformed project officer line: " + String.join(",", fields));
                 }
             }
         } catch (IOException e) {
            System.err.println("INFO: Project officers file not found or failed to read: " + PROJECT_OFFICERS_FILE_PATH + " (" + e.getMessage() + ")");
            // Don't throw, projects might exist without officers initially
         }
        return officersMap;
    }
    
}
   