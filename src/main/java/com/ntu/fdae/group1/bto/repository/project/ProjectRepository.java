package com.ntu.fdae.group1.bto.repository.project;

import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.repository.util.CsvRepositoryHelper;
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
import java.util.stream.Collectors;

/**
 * Repository implementation for managing Project entities in the BTO Management
 * System.
 * <p>
 * This class handles the persistence and retrieval of Project records using two
 * CSV files
 * as the backing store - one for project metadata and one for flat type
 * information.
 * It provides methods for finding projects, adding/removing projects, and
 * managing
 * associated flat information.
 * </p>
 * <p>
 * The repository maintains the relationship between Project objects and their
 * associated ProjectFlatInfo objects, ensuring that changes to either are
 * properly synchronized and persisted.
 * </p>
 */
public class ProjectRepository implements IProjectRepository {
    /**
     * Path to the CSV file where project data is stored.
     */
    private static final String PROJECT_FILE_PATH = "data/projects.csv";

    /**
     * Path to the CSV file where project flat information is stored.
     */
    private static final String FLAT_INFO_FILE_PATH = "data/projects_flat_info.csv";

    /**
     * CSV header columns for the projects file.
     */
    private static final String[] PROJECT_CSV_HEADER = new String[] {
            "projectId", "projectName", "neighborhood", "openingDate", "closingDate",
            "managerNric", "maxOfficerSlots", "isVisible", "approvedOfficerNrics"
    };

    /**
     * CSV header columns for the project flat information file.
     */
    private static final String[] FLAT_INFO_CSV_HEADER = new String[] {
            "flatInfoId", "projectId", "typeName", "totalUnits", "remainingUnits", "price"
    };

    /**
     * In-memory cache of all projects, indexed by project ID.
     */
    private Map<String, Project> projects;

    /**
     * Set of all flat info IDs that have been loaded from the CSV file.
     * Used to track which flat info records exist in the system.
     */
    private Set<String> loadedFlatInfoIds = new HashSet<>();

    /**
     * Helper for CSV file operations, handling serialization and deserialization
     * of project data.
     */
    private final CsvRepositoryHelper<String, Project> csvHelper;

    /**
     * Constructs a new ProjectRepository.
     * <p>
     * Initializes the CSV helper with appropriate serializers/deserializers and
     * loads the initial project data from both CSV files. The repository maintains
     * an in-memory cache of projects which includes their associated flat
     * information.
     * </p>
     * <p>
     * If loading fails, the repository starts with an empty project collection.
     * </p>
     */
    public ProjectRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                PROJECT_FILE_PATH,
                PROJECT_CSV_HEADER,
                this::deserializeProjectsAndFlatInfo,
                this::serializeProjectsAndFlatInfo);
        try {
            this.projects = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial project load failed: " + e.getMessage());
            this.projects = new HashMap<>();
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns the project with the specified ID from the in-memory cache,
     * or null if no project with that ID exists.
     * </p>
     */
    @Override
    public Project findById(String projectId) {
        return projects.get(projectId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a defensive copy of the in-memory project map to prevent
     * external modification of the repository's internal state.
     * </p>
     */
    @Override
    public Map<String, Project> findAll() {
        return new HashMap<>(projects);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves a project and its associated flat information to both the in-memory
     * cache and the CSV files. The method validates that neither the project nor
     * its ID is null before saving.
     * </p>
     * <p>
     * This method delegates to the CSV helper which in turn calls
     * serializeProjectsAndFlatInfo
     * to handle saving data to both the project file and the flat info file.
     * </p>
     * 
     * @throws DataAccessException if there is an error writing to either CSV file
     */
    @Override
    public void save(Project project) {
        if (project == null || project.getProjectId() == null) {
            System.err.println("Attempted to save null project or project with null ID");
            return;
        }
        projects.put(project.getProjectId(), project);
        try {
            csvHelper.saveData(projects); // Delegates saving BOTH files via serializeProjectsAndFlatInfo
        } catch (DataAccessException e) {
            System.err.println("Failed to save project " + project.getProjectId() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Replaces the entire in-memory project collection with the provided map
     * and persists all projects and their flat information to the CSV files.
     * </p>
     * <p>
     * This operation is atomic - either all projects are saved successfully, or
     * an exception is thrown and no changes are made to the files.
     * </p>
     * 
     * @throws DataAccessException if there is an error writing to either CSV file
     */
    @Override
    public void saveAll(Map<String, Project> entities) {
        this.projects = new HashMap<>(entities);
        try {
            csvHelper.saveData(projects);
        } catch (DataAccessException e) {
            System.err.println("Failed to save all projects: " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Loads all projects and their associated flat information from the CSV files
     * into the in-memory cache. Returns a defensive copy of the loaded projects.
     * </p>
     * 
     * @throws DataAccessException if there is an error reading from either CSV file
     */
    @Override
    public Map<String, Project> loadAll() throws DataAccessException {
        this.projects = csvHelper.loadData(); // Delegates loading BOTH files via deserializeProjectsAndFlatInfo
        return new HashMap<>(projects);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a set of all flat info IDs that have been loaded from the CSV file.
     * If the projects and flat info IDs are not already loaded, this method will
     * trigger a load operation.
     * </p>
     * 
     * @throws DataAccessException if there is an error reading from either CSV file
     */
    @Override
    public Set<String> findAllFlatInfoIds() throws DataAccessException {
        if (projects.isEmpty() && loadedFlatInfoIds.isEmpty()) {
            loadAll();
        }
        return new HashSet<>(this.loadedFlatInfoIds);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Deletes the project with the specified ID from the in-memory cache and
     * persists the change to the CSV files. If the project ID is null or empty,
     * the method logs a warning and returns without making any changes.
     * </p>
     * 
     * @throws DataAccessException if there is an error writing to either CSV file
     */
    @Override
    public void deleteById(String id) throws DataAccessException {
        if (id == null || id.trim().isEmpty()) {
            System.err.println("Warning: Attempted to delete project with null or empty ID.");
            return;
        }

        // Remove from the in-memory map using the 'id' parameter
        Project removedProject = this.projects.remove(id);

        if (removedProject != null) {
            System.out.println("Project deleted from memory: " + id);

            // Persist the changes by saving the entire current state
            // The CsvRepositoryHelper's saveData (called by saveAll) should handle writing
            // both files
            try {
                // saveAll is suitable here as it overwrites files with the current map state
                this.saveAll(this.projects);
            } catch (DataAccessException e) {
                System.err.println(
                        "Error persisting deletion for project: " + id + ". In-memory map may be inconsistent.");
                throw e;
            }
        } else {
            System.out.println("Project not found for deletion: " + id);
        }
    }

    /**
     * Deserializes project and flat information data from CSV rows into a map of
     * Project objects.
     * <p>
     * This method performs a two-step deserialization process:
     * <ol>
     * <li>First loads and processes flat information from a separate CSV file</li>
     * <li>Then processes project data, linking flat information to each
     * project</li>
     * </ol>
     * </p>
     * <p>
     * The method handles potential format issues and logs errors for problematic
     * rows
     * without throwing exceptions that would disrupt the entire loading process.
     * It also tracks loaded flat info IDs for reference by other repository
     * methods.
     * </p>
     * 
     * @param projectData List of CSV row data arrays for projects
     * @return Map of deserialized Project objects indexed by project ID
     * @throws DataAccessException if there is an error reading from the flat info
     *                             CSV file
     */
    private Map<String, Project> deserializeProjectsAndFlatInfo(List<String[]> projectData) throws DataAccessException {
        this.loadedFlatInfoIds.clear(); // Reset for this load operation
        Map<String, Project> projectMap = new HashMap<>();
        Map<String, Map<FlatType, ProjectFlatInfo>> flatInfoByProjectId = new HashMap<>();

        // 1. Read and process Flat Info data first
        try {
            List<String[]> flatInfoData = FileUtil.readCsvLines(FLAT_INFO_FILE_PATH);
            if (flatInfoData != null) {
                for (String[] row : flatInfoData) {
                    if (row.length < 6)
                        continue;
                    try {
                        String flatInfoId = row[0];
                        String projId = row[1];
                        FlatType flatType = FileUtil.parseEnum(FlatType.class, row[2]);
                        int totalUnits = FileUtil.parseIntOrDefault(row[3], 0);
                        int remainingUnits = FileUtil.parseIntOrDefault(row[4], 0);
                        double price = FileUtil.parseDoubleOrDefault(row[5], 0.0);

                        loadedFlatInfoIds.add(flatInfoId); // Track loaded IDs

                        ProjectFlatInfo flatInfo = new ProjectFlatInfo(flatType, totalUnits, remainingUnits, price);
                        flatInfoByProjectId.computeIfAbsent(projId, k -> new HashMap<>()).put(flatType, flatInfo);
                    } catch (Exception e) {
                        System.err.println(
                                "Error parsing flat info row: " + String.join(",", row) + " - " + e.getMessage());
                    }
                }
            }
        } catch (IOException e) {
            throw new DataAccessException(
                    "Error loading flat info from file: " + FLAT_INFO_FILE_PATH + " - " + e.getMessage(), e);
        }

        // 2. Process Project data (passed in as argument by the helper)
        if (projectData != null) {
            for (String[] row : projectData) {
                if (row.length < 8)
                    continue;
                try {
                    String projId = row[0];
                    String projectName = row[1];
                    String neighborhood = row[2];
                    LocalDate openingDate = FileUtil.parseLocalDate(row[3]);
                    LocalDate closingDate = FileUtil.parseLocalDate(row[4]);
                    String managerNric = row[5];
                    int maxOfficerSlots = FileUtil.parseIntOrDefault(row[6], 0);
                    boolean isVisible = Boolean.parseBoolean(row[7]);

                    Map<FlatType, ProjectFlatInfo> projectFlatTypes = flatInfoByProjectId.getOrDefault(projId,
                            new HashMap<>());

                    Project project = new Project(projId, projectName, neighborhood, projectFlatTypes, openingDate,
                            closingDate, managerNric, maxOfficerSlots);
                    project.setVisibility(isVisible);

                    if (row.length > 8 && row[8] != null && !row[8].trim().isEmpty()) {
                        List<String> approvedOfficers = FileUtil.splitString(row[8], ";");
                        project.setApprovedOfficerNrics(approvedOfficers.stream().filter(s -> !s.trim().isEmpty())
                                .collect(Collectors.toList())); // Ensure list is set
                    }
                    project.setMaxOfficerSlots(maxOfficerSlots); // Recalculate remaining slots

                    projectMap.put(projId, project);
                } catch (Exception e) {
                    System.err.println("Error parsing project row: " + String.join(",", row) + " - " + e.getMessage());
                }
            }
        }

        return projectMap;
    }

    /**
     * Serializes Project objects and their associated flat information into CSV row
     * format for storage.
     * <p>
     * This method performs a two-step serialization process:
     * <ol>
     * <li>First serializes all Project objects to project CSV rows</li>
     * <li>Then serializes all associated ProjectFlatInfo objects to flat info CSV
     * rows</li>
     * <li>Writes the flat info CSV file directly</li>
     * <li>Returns the project CSV rows for the helper to write</li>
     * </ol>
     * </p>
     * <p>
     * The method generates unique IDs for flat info records using a simple counter,
     * and handles potential null values defensively to ensure robust serialization.
     * </p>
     * 
     * @param projectsToSerialize Map of Project objects to serialize
     * @return List of CSV row data arrays for projects
     * @throws DataAccessException if there is an error writing to the flat info CSV
     *                             file
     */
    private List<String[]> serializeProjectsAndFlatInfo(Map<String, Project> projectsToSerialize)
            throws DataAccessException {
        List<String[]> serializedProjectData = new ArrayList<>();
        List<String[]> serializedFlatInfoData = new ArrayList<>();
        int flatInfoIdCounter = 1; // Simple counter for flat info IDs

        if (projectsToSerialize != null) {
            for (Project project : projectsToSerialize.values()) {
                // Serialize Project data
                serializedProjectData.add(new String[] {
                        project.getProjectId(),
                        project.getProjectName(),
                        project.getNeighborhood(),
                        FileUtil.formatLocalDate(project.getOpeningDate()),
                        FileUtil.formatLocalDate(project.getClosingDate()),
                        project.getManagerNric(),
                        String.valueOf(project.getMaxOfficerSlots()),
                        String.valueOf(project.isVisible()),
                        FileUtil.joinList(project.getApprovedOfficerNrics(), ";") // Use util
                });

                // Serialize associated FlatInfo data
                for (Map.Entry<FlatType, ProjectFlatInfo> entry : project.getFlatTypes().entrySet()) {
                    ProjectFlatInfo flatInfo = entry.getValue();
                    String flatInfoId = String.format("FLAT%03d", flatInfoIdCounter++); // Generate ID

                    serializedFlatInfoData.add(new String[] {
                            flatInfoId,
                            project.getProjectId(),
                            entry.getKey().toString(),
                            String.valueOf(flatInfo.getTotalUnits()),
                            String.valueOf(flatInfo.getRemainingUnits()),
                            String.valueOf(flatInfo.getPrice())
                    });
                }
            }
        }

        // Write the secondary file (Flat Info) MANUALLY here
        try {
            FileUtil.writeCsvLines(FLAT_INFO_FILE_PATH, serializedFlatInfoData, FLAT_INFO_CSV_HEADER);
        } catch (IOException e) {
            throw new DataAccessException(
                    "Error saving flat info to file: " + FLAT_INFO_FILE_PATH + " - " + e.getMessage(), e);
        }

        // Return the PRIMARY file data for the helper to write
        return serializedProjectData;
    }

}