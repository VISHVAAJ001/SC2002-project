package com.ntu.fdae.group1.bto.repository.enquiry;

import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EnquiryRepository implements IEnquiryRepository {
    private static final String ENQUIRY_FILE_PATH = "resources/enquiries.csv";

    private Map<String, Enquiry> enquiries;

    public EnquiryRepository() {
        this.enquiries = new HashMap<>();
    }

    @Override
    public Enquiry findById(String enquiryId) {
        return enquiries.get(enquiryId);
    }

    @Override
    public Map<String, Enquiry> findAll() {
        return new HashMap<>(enquiries);
    }

    @Override
    public void save(Enquiry enquiry) {
        enquiries.put(enquiry.getEnquiryId(), enquiry);
        saveAll(enquiries);
    }

    @Override
    public void saveAll(Map<String, Enquiry> entities) {
        this.enquiries = entities;
        try {
            FileUtil.writeCsvLines(ENQUIRY_FILE_PATH, serializeEnquiries(), getEnquiryCsvHeader());
        } catch (IOException e) {
            throw new DataAccessException("Error saving enquiries to file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Enquiry> loadAll() throws DataAccessException {
        try {
            List<String[]> enquiryData = FileUtil.readCsvLines(ENQUIRY_FILE_PATH);
            enquiries = deserializeEnquiries(enquiryData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading enquiries from file: " + e.getMessage(), e);
        }
        return enquiries;
    }

    @Override
    public List<Enquiry> findByUserNric(String nric) {
        return enquiries.values().stream()
                .filter(enquiry -> enquiry.getUserNric().equals(nric))
                .collect(Collectors.toList());
    }

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

    // Helper methods for serialization/deserialization
    private String[] getEnquiryCsvHeader() {
        return new String[] {
                "enquiryId", "userNric", "projectId", "content", "reply",
                "isReplied", "submissionDate", "replyDate"
        };
    }

    private Map<String, Enquiry> deserializeEnquiries(List<String[]> enquiryData) {
        Map<String, Enquiry> enquiryMap = new HashMap<>();

        if (enquiryData == null || enquiryData.isEmpty()) {
            return enquiryMap;
        }

        for (String[] row : enquiryData) {
            if (row.length < 7)
                continue; // Skip invalid rows

            try {
                String enquiryId = row[0];
                String userNric = row[1];
                String projectId = row[2].trim().isEmpty() ? null : row[2];
                String content = row[3];
                String reply = row[4].trim().isEmpty() ? null : row[4];
                boolean isReplied = Boolean.parseBoolean(row[5]);
                LocalDate submissionDate = FileUtil.parseLocalDate(row[6]);

                // Create the enquiry
                Enquiry enquiry = new Enquiry(enquiryId, userNric, projectId, content, submissionDate);

                // Set reply if exists
                if (isReplied && reply != null && row.length > 7) {
                    LocalDate replyDate = FileUtil.parseLocalDate(row[7]);
                    enquiry.addReply(reply, replyDate);
                }

                enquiryMap.put(enquiryId, enquiry);
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing enquiry data: " + e.getMessage());
            }
        }

        return enquiryMap;
    }

    private List<String[]> serializeEnquiries() {
        List<String[]> serializedData = new ArrayList<>();

        for (Enquiry enquiry : enquiries.values()) {
            String projectId = enquiry.getProjectId() == null ? "" : enquiry.getProjectId();
            String reply = enquiry.getReply() == null ? "" : enquiry.getReply();
            String replyDate = "";

            if (enquiry.isReplied() && enquiry.getReplyDate() != null) {
                replyDate = FileUtil.formatLocalDate(enquiry.getReplyDate());
            }

            serializedData.add(new String[] {
                    enquiry.getEnquiryId(),
                    enquiry.getUserNric(),
                    projectId,
                    enquiry.getContent(),
                    reply,
                    String.valueOf(enquiry.isReplied()),
                    FileUtil.formatLocalDate(enquiry.getSubmissionDate()),
                    replyDate
            });
        }

        return serializedData;
    }
}