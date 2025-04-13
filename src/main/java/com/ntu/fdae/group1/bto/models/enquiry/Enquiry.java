package com.ntu.fdae.group1.bto.models.enquiry;

import java.time.LocalDate;

/**
 * Represents a customer enquiry in the BTO system.
 * <p>
 * This class models enquiries submitted by users seeking information or
 * assistance
 * about BTO projects or the application process. Each enquiry tracks its
 * submission
 * details, content, and any replies provided by HDB staff.
 * </p>
 */
public class Enquiry {
    /**
     * Unique identifier for this enquiry.
     */
    private String enquiryId;

    /**
     * NRIC of the user who submitted the enquiry.
     */
    private String userNric;

    /**
     * The ID of the project this enquiry is related to, or null if it's a general
     * enquiry.
     */
    private String projectId;

    /**
     * The content or question of the enquiry.
     */
    private String content;

    /**
     * The reply provided by HDB staff, or null if no reply has been provided.
     */
    private String reply;

    /**
     * Flag indicating whether the enquiry has been replied to.
     */
    private boolean isReplied = false;

    /**
     * The date when the enquiry was submitted.
     */
    private LocalDate submissionDate;

    /**
     * The date when a reply was provided, or null if no reply has been provided.
     */
    private LocalDate replyDate;

    /**
     * Constructs a new Enquiry with the specified details.
     *
     * @param enquiryId      Unique identifier for this enquiry
     * @param userNric       NRIC of the user submitting the enquiry
     * @param projectId      ID of the related project, or null for general
     *                       enquiries
     * @param content        The content or question of the enquiry
     * @param submissionDate Date when the enquiry was submitted
     */
    public Enquiry(String enquiryId, String userNric, String projectId, String content, LocalDate submissionDate) {
        this.enquiryId = enquiryId;
        this.userNric = userNric;
        this.projectId = projectId;
        this.content = content;
        this.submissionDate = submissionDate;
    }

    /**
     * Gets the unique identifier for this enquiry.
     *
     * @return The enquiry ID
     */
    public String getEnquiryId() {
        return enquiryId;
    }

    /**
     * Gets the NRIC of the user who submitted this enquiry.
     *
     * @return The user's NRIC
     */
    public String getUserNric() {
        return userNric;
    }

    /**
     * Gets the ID of the project this enquiry is related to.
     *
     * @return The project ID, or null if this is a general enquiry
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Gets the content or question of this enquiry.
     *
     * @return The enquiry content
     */
    public String getContent() {
        return content;
    }

    /**
     * Updates the content of this enquiry.
     * 
     * @param content The new content to set
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Gets the reply provided by HDB staff.
     *
     * @return The reply text, or null if no reply has been provided
     */
    public String getReply() {
        return reply;
    }

    /**
     * Checks if this enquiry has been replied to.
     *
     * @return true if a reply has been provided, false otherwise
     */
    public boolean isReplied() {
        return isReplied;
    }

    /**
     * Gets the date when this enquiry was submitted.
     *
     * @return The submission date
     */
    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Gets the date when a reply was provided.
     *
     * @return The reply date, or null if no reply has been provided
     */
    public LocalDate getReplyDate() {
        return replyDate;
    }

    /**
     * Adds a reply to this enquiry.
     * <p>
     * When a reply is added, the enquiry is marked as replied and the reply date is
     * recorded.
     * </p>
     *
     * @param replyContent The content of the reply
     * @param replyDate    The date when the reply was provided
     */
    public void addReply(String replyContent, LocalDate replyDate) {
        this.reply = replyContent;
        this.replyDate = replyDate;
        this.isReplied = true;
    }

    /**
     * Edits the content of this enquiry.
     * <p>
     * This is an alternative method to setContent() with a more descriptive name.
     * </p>
     *
     * @param newContent The new content to set
     */
    public void editContent(String newContent) {
        this.content = newContent;
    }
}