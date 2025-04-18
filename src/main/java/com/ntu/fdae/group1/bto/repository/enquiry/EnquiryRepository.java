package com.ntu.fdae.group1.bto.repository.enquiry;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;
import com.ntu.fdae.group1.bto.repository.util.CsvRepositoryHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Implementation of the IEnquiryRepository interface that persists Enquiry
 * entities
 * to a CSV file.
 * <p>
 * This repository manages enquiry data, providing CRUD operations and
 * specialized
 * queries for enquiry management in the BTO system. It uses a CSV file as the
 * persistent storage mechanism, with in-memory caching for efficient access.
 * </p>
 * <p>
 * The repository handles serialization and deserialization of enquiry data to
 * and
 * from the CSV format, including conversion of complex fields like timestamps.
 * </p>
 */
public class EnquiryRepository implements IEnquiryRepository {
    private static final String ENQUIRY_FILE_PATH = "data/enquiries.csv";
    private static final String[] ENQUIRY_CSV_HEADER = new String[] {
            "enquiryId", "userNric", "projectId", "content", "reply",
            "isReplied", "submissionDate", "replyDate"
    };

    private Map<String, Enquiry> enquiries;
    private final CsvRepositoryHelper<String, Enquiry> csvHelper;

    /**
     * Constructs a new EnquiryRepository.
     * <p>
     * Initializes the repository and loads existing enquiry data from the CSV file.
     * If the initial data load fails, an empty enquiry collection is created.
     * </p>
     */
    public EnquiryRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                ENQUIRY_FILE_PATH,
                ENQUIRY_CSV_HEADER,
                this::deserializeEnquiries,
                this::serializeEnquiries);
        // Load initial data
        try {
            this.enquiries = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial enquiry load failed: " + e.getMessage());
            this.enquiries = new HashMap<>(); // Start with empty map on failure
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves an enquiry by its unique identifier from the in-memory cache.
     * Returns null if no enquiry exists with the specified ID.
     * </p>
     */
    @Override
    public Enquiry findById(String enquiryId) {
        return enquiries.get(enquiryId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a defensive copy of the enquiries map to prevent external
     * modification of the repository's internal state.
     * </p>
     */
    @Override
    public Map<String, Enquiry> findAll() {
        return new HashMap<>(enquiries);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves an enquiry to both the in-memory cache and the CSV file.
     * If the enquiry or its ID is null, the method logs an error and returns
     * without saving.
     * </p>
     */
    @Override
    public void save(Enquiry enquiry) {
        if (enquiry == null || enquiry.getEnquiryId() == null) {
            System.err.println("Attempted to save null enquiry or enquiry with null ID");
            return;
        }
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        try {
            csvHelper.saveData(enquiries);
        } catch (DataAccessException e) {
            System.err.println("Failed to save enquiry " + enquiry.getEnquiryId() + ": " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Updates the in-memory enquiry collection with the provided map and
     * persists all enquiries to the CSV file.
     * </p>
     */
    @Override
    public void saveAll(Map<String, Enquiry> entities) {
        this.enquiries = new HashMap<>(entities); // Replace with a copy
        try {
            csvHelper.saveData(enquiries);
        } catch (DataAccessException e) {
            System.err.println("Failed to save all enquiries: " + e.getMessage());
            throw e;
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Loads all enquiry data from the CSV file into the in-memory cache,
     * replacing any existing data. Returns a defensive copy of the loaded
     * enquiries.
     * </p>
     */
    @Override
    public Map<String, Enquiry> loadAll() throws DataAccessException {
        this.enquiries = csvHelper.loadData();
        return new HashMap<>(enquiries);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves all enquiries submitted by the user with the specified NRIC.
     * Returns an empty list if no matching enquiries are found.
     * </p>
     */
    @Override
    public List<Enquiry> findByUserNric(String nric) {
        return enquiries.values().stream()
                .filter(enquiry -> enquiry.getUserNric().equals(nric))
                .collect(Collectors.toList());
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves all enquiries related to the specified project.
     * Returns an empty list if no matching enquiries are found or if projectId is
     * null.
     * </p>
     */
    @Override
    public List<Enquiry> findByProjectId(String projectId) {
        if (projectId == null) {
            return enquiries.values().stream()
                    .filter(enquiry -> enquiry.getProjectId() == null)
                    .collect(Collectors.toList());
        }

        return enquiries.values().stream()
                .filter(enquiry -> projectId.equals(enquiry.getProjectId()))
                .collect(Collectors.toList());
    }

    /**
     * Deserializes CSV data into Enquiry objects.
     * <p>
     * Converts each CSV row into an Enquiry object, handling all field conversions
     * including parsing of date-time fields and managing optional reply data.
     * </p>
     * 
     * @param enquiryData List of string arrays representing enquiry data from CSV
     * @return A map of deserialized Enquiry objects, keyed by their IDs
     */
    private Map<String, Enquiry> deserializeEnquiries(List<String[]> enquiryData) {
        Map<String, Enquiry> enquiryMap = new HashMap<>();
        if (enquiryData == null)
            return enquiryMap;

        for (String[] row : enquiryData) {
            if (row.length < 7) {
                System.err.println("Skipping invalid enquiry row: " + String.join(",", row));
                continue;
            }
            try {
                String enquiryId = row[0];
                String userNric = row[1];
                // Handle potentially empty projectId string -> null object
                String projectId = (row[2] == null || row[2].trim().isEmpty()) ? null : row[2].trim();
                String content = row[3];
                // Handle potentially empty reply string -> null object
                String reply = (row[4] == null || row[4].trim().isEmpty()) ? null : row[4].trim();
                // Parse boolean safely
                boolean isReplied = Boolean.parseBoolean(row[5]);
                LocalDate submissionDate = FileUtil.parseLocalDate(row[6]);
                if (submissionDate == null) {
                    System.err.println("Skipping enquiry row due to invalid submission date: " + row[6]);
                    continue;
                }

                Enquiry enquiry = new Enquiry(enquiryId, userNric, projectId, content, submissionDate);

                // If it's marked as replied, try to parse the reply date (might be missing or
                // invalid)
                if (isReplied && reply != null && row.length > 7 && row[7] != null && !row[7].trim().isEmpty()) {
                    LocalDate replyDate = FileUtil.parseLocalDate(row[7]);
                    if (replyDate != null) {
                        enquiry.addReply(reply, replyDate);
                    } else {
                        System.err.println(
                                "Warning: Enquiry " + enquiryId + " marked replied but reply date is invalid/missing.");
                    }
                }
                // Ensure content is set even if reply handling was complex
                enquiry.setContent(content);

                enquiryMap.put(enquiryId, enquiry);
            } catch (Exception e) {
                System.err.println("Error parsing enquiry row: " + String.join(",", row) + " - " + e.getMessage());
            }
        }
        return enquiryMap;
    }

    /**
     * Converts a collection of Enquiry objects to a list of string arrays for CSV
     * serialization.
     * <p>
     * Each string array represents one row in the CSV file, with elements ordered
     * according to the CSV_HEADER definition.
     * </p>
     * 
     * @param enqsToSerialize The collection of enquiries to serialize
     * @return A list of string arrays representing the enquiries in CSV format
     */
    private List<String[]> serializeEnquiries(Map<String, Enquiry> enqsToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
        if (enqsToSerialize == null)
            return serializedData;

        for (Enquiry enquiry : enqsToSerialize.values()) {
            String replyDateStr = "";
            // Only format reply date if the enquiry is actually replied and date is not
            // null
            if (enquiry.isReplied() && enquiry.getReplyDate() != null) {
                replyDateStr = FileUtil.formatLocalDate(enquiry.getReplyDate());
            }

            serializedData.add(new String[] {
                    enquiry.getEnquiryId(),
                    enquiry.getUserNric(),
                    enquiry.getProjectId() == null ? "" : enquiry.getProjectId(),
                    enquiry.getContent() == null ? "" : enquiry.getContent(),
                    enquiry.getReply() == null ? "" : enquiry.getReply(),
                    String.valueOf(enquiry.isReplied()),
                    FileUtil.formatLocalDate(enquiry.getSubmissionDate()),
                    replyDateStr
            });
        }
        return serializedData;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Removes the enquiry with the specified ID from both the in-memory cache
     * and the CSV file. If no enquiry exists with the specified ID, no action is
     * taken.
     * </p>
     */
    @Override
    public void deleteById(String enquiryId) throws DataAccessException {
        if (enquiryId == null || enquiryId.trim().isEmpty()) {
            System.err.println("Warning: Attempted to delete enquiry with null or empty ID.");
            return;
        }
        Enquiry removedEnquiry = enquiries.remove(enquiryId);
        if (removedEnquiry != null) {
            System.out.println("Deleted enquiry from memory: " + enquiryId);
            try {
                // Persist the change by saving the current state of the map
                this.saveAll(this.enquiries); // Reuses the saveAll logic with the helper
            } catch (DataAccessException e) {
                System.err.println("Error persisting deletion for enquiry: " + enquiryId);
                // Re-throw the exception
                throw e;
            }
        } else {
            System.out.println("Enquiry with ID '" + enquiryId + "' not found for deletion.");
        }
    }
}