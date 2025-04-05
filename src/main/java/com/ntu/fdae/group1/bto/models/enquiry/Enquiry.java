package com.ntu.fdae.group1.bto.models.enquiry;

import java.time.LocalDate;

public class Enquiry {
    private String enquiryId;
    private String userNric;
    private String projectId;
    private String content;
    private String reply;
    private boolean isReplied = false;
    private LocalDate submissionDate;
    private LocalDate replyDate;

    public Enquiry(String enquiryId, String userNric, String projectId, String content, LocalDate submissionDate) {
        this.enquiryId = enquiryId;
        this.userNric = userNric;
        this.projectId = projectId;
        this.content = content;
        this.submissionDate = submissionDate;
    }

    public String getEnquiryId() {
        return enquiryId;
    }

    public String getUserNric() {
        return userNric;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReply() {
        return reply;
    }

    public boolean isReplied() {
        return isReplied;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public LocalDate getReplyDate() {
        return replyDate;
    }

    public void addReply(String replyContent, LocalDate replyDate) {
        this.reply = replyContent;
        this.replyDate = replyDate;
        this.isReplied = true;
    }

    public void editContent(String newContent) {
        this.content = newContent;
    }
}
