package com.ntu.fdae.group1.bto.models.booking;

import java.time.LocalDate;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;

public class BookingReceiptInfo {
    private String applicantName;
    private String applicantNric;
    private int applicantAge;
    private MaritalStatus applicantMaritalStatus;
    private String bookedFlatType;
    private String projectName;
    private String projectNeighborhood;
    private String bookingId;
    private LocalDate bookingDate;

    public BookingReceiptInfo(String applicantName, String applicantNric, int applicantAge,
            MaritalStatus applicantMaritalStatus, String bookedFlatType,
            String projectName, String projectNeighborhood,
            String bookingId, LocalDate bookingDate) {
        this.applicantName = applicantName;
        this.applicantNric = applicantNric;
        this.applicantAge = applicantAge;
        this.applicantMaritalStatus = applicantMaritalStatus;
        this.bookedFlatType = bookedFlatType;
        this.projectName = projectName;
        this.projectNeighborhood = projectNeighborhood;
        this.bookingId = bookingId;
        this.bookingDate = bookingDate;
    }

    public String getApplicantName() {
        return applicantName;
    }

    public String getApplicantNric() {
        return applicantNric;
    }

    public int getApplicantAge() {
        return applicantAge;
    }

    public MaritalStatus getApplicantMaritalStatus() {
        return applicantMaritalStatus;
    }

    public String getBookedFlatType() {
        return bookedFlatType;
    }

    public String getProjectName() {
        return projectName;
    }

    public String getProjectNeighborhood() {
        return projectNeighborhood;
    }

    public String getBookingId() {
        return bookingId;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }
}
