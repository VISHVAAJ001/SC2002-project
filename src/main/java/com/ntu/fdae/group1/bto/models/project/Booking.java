package com.ntu.fdae.group1.bto.models.project;

import java.time.LocalDate;

public class Booking {
    private String bookingId;
    private String applicationId;
    private String applicantNRIC;
    private String projectId;
    private String bookedFlatType;
    private LocalDate bookingDate;

    public Booking(String bookingId, String applicationId, String applicantNRIC, String projectId,
            String bookedFlatType, LocalDate bookingDate) {
        this.bookingId = bookingId;
        this.applicationId = applicationId;
        this.applicantNRIC = applicantNRIC;
        this.projectId = projectId;
        this.bookedFlatType = bookedFlatType;
        this.bookingDate = bookingDate;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getApplicationId() {
        return applicationId;
    }

    public String getApplicantNRIC() {
        return applicantNRIC;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getBookedFlatType() {
        return bookedFlatType;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }
}
