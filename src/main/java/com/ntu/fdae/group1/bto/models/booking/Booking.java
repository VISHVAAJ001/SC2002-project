package com.ntu.fdae.group1.bto.models.booking;

import com.ntu.fdae.group1.bto.enums.FlatType;
import java.time.LocalDate;

/**
 * Represents a booking made by an applicant for a flat in a BTO project.
 * <p>
 * A booking is created after a successful application has been processed and
 * the
 * applicant has selected a specific flat unit. It serves as a record of the
 * booking transaction between the applicant and HDB.
 * </p>
 */
public class Booking {
    /**
     * Unique identifier for the booking.
     */
    private String bookingId;

    /**
     * Reference to the application ID associated with this booking.
     */
    private String applicationId;

    /**
     * NRIC of the applicant who made the booking.
     */
    private String applicantNric;

    /**
     * ID of the project where the flat is booked.
     */
    private String projectId;

    /**
     * Type of flat that has been booked.
     */
    private FlatType bookedFlatType;

    /**
     * Date when the booking was made.
     */
    private LocalDate bookingDate;

    /**
     * Constructs a new Booking with all required fields.
     * 
     * @param bookingId      Unique identifier for the booking
     * @param applicationId  Reference to the associated application ID
     * @param applicantNric  NRIC of the applicant who made the booking
     * @param projectId      ID of the project where the flat is booked
     * @param bookedFlatType Type of flat that has been booked
     * @param bookingDate    Date when the booking was made
     */
    public Booking(String bookingId, String applicationId, String applicantNric, String projectId,
            FlatType bookedFlatType, LocalDate bookingDate) {
        this.bookingId = bookingId;
        this.applicationId = applicationId;
        this.applicantNric = applicantNric;
        this.projectId = projectId;
        this.bookedFlatType = bookedFlatType;
        this.bookingDate = bookingDate;
    }

    /**
     * Gets the unique identifier for this booking.
     * 
     * @return The booking ID
     */
    public String getBookingId() {
        return bookingId;
    }

    /**
     * Gets the application ID associated with this booking.
     * 
     * @return The application ID
     */
    public String getApplicationId() {
        return applicationId;
    }

    /**
     * Gets the NRIC of the applicant who made this booking.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the ID of the project where the flat is booked.
     * 
     * @return The project ID
     */
    public String getProjectId() {
        return projectId;
    }

    /**
     * Gets the type of flat that has been booked.
     * 
     * @return The booked flat type
     */
    public FlatType getBookedFlatType() {
        return bookedFlatType;
    }

    /**
     * Gets the date when the booking was made.
     * 
     * @return The booking date
     */
    public LocalDate getBookingDate() {
        return bookingDate;
    }
}
