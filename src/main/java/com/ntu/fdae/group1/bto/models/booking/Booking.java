package com.ntu.fdae.group1.bto.models.booking;

import com.ntu.fdae.group1.bto.enums.FlatType;
import java.time.LocalDate;

public class Booking {
    private String bookingId;
    private String applicationId;
    private String applicantNric;
    private String projectId;
    private FlatType bookedFlatType;
    private LocalDate bookingDate;

    public Booking(String bookingId, String applicationId, String applicantNric, String projectId,
            FlatType bookedFlatType, LocalDate bookingDate) {
        this.bookingId = bookingId;
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
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

    public String getApplicantNric() {
        return applicantNric;
    }

    public String getProjectId() {
        return projectId;
    }

    public FlatType getBookedFlatType() {
        return bookedFlatType;
    }

    public LocalDate getBookingDate() {
        return bookingDate;
    }
}
