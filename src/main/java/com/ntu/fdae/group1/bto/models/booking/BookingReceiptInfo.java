package com.ntu.fdae.group1.bto.models.booking;

import java.time.LocalDate;

import com.ntu.fdae.group1.bto.enums.MaritalStatus;

/**
 * Data Transfer Object (DTO) containing all information needed for a booking
 * receipt.
 * <p>
 * This class consolidates information from various sources (booking, applicant,
 * project)
 * into a single object that can be used to display or print a booking receipt.
 * It serves as a comprehensive record of a booking transaction.
 * </p>
 * <p>
 * The BookingReceiptInfo includes:
 * - Receipt identification details
 * - Booking information
 * - Applicant information
 * - Project and flat details
 * - Financial information including prices and payment deadlines
 * </p>
 */
public class BookingReceiptInfo {
    /**
     * Full name of the applicant who made the booking.
     */
    private String applicantName;

    /**
     * NRIC of the applicant who made the booking.
     */
    private String applicantNric;

    /**
     * Age of the applicant who made the booking.
     */
    private int applicantAge;

    /**
     * Marital status of the applicant who made the booking.
     */
    private MaritalStatus applicantMaritalStatus;

    /**
     * Type of flat that has been booked (e.g., "3-Room", "4-Room").
     */
    private String bookedFlatType;

    /**
     * Name of the project where the flat is booked.
     */
    private String projectName;

    /**
     * Location or address of the project.
     */
    private String projectNeighborhood;

    /**
     * Reference to the booking ID associated with this receipt.
     */
    private String bookingId;

    /**
     * Date when the booking was made.
     */
    private LocalDate bookingDate;

    /**
     * Constructor for BookingReceiptInfo.
     * 
     * @param applicantName          Full name of the applicant
     * @param applicantNric          NRIC of the applicant
     * @param applicantAge           Age of the applicant
     * @param applicantMaritalStatus Marital status of the applicant
     * @param bookedFlatType         Type of flat that has been booked
     * @param projectName            Name of the project
     * @param projectNeighborhood    Location or address of the project
     * @param bookingId              Reference to the booking ID
     * @param bookingDate            Date when the booking was made
     */
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

    /**
     * Gets the full name of the applicant.
     * 
     * @return The applicant's name
     */
    public String getApplicantName() {
        return applicantName;
    }

    /**
     * Gets the NRIC of the applicant.
     * 
     * @return The applicant's NRIC
     */
    public String getApplicantNric() {
        return applicantNric;
    }

    /**
     * Gets the age of the applicant.
     * 
     * @return The applicant's age
     */
    public int getApplicantAge() {
        return applicantAge;
    }

    /**
     * Gets the marital status of the applicant.
     * 
     * @return The applicant's marital status
     */
    public MaritalStatus getApplicantMaritalStatus() {
        return applicantMaritalStatus;
    }

    /**
     * Gets the type of flat that has been booked.
     * 
     * @return The booked flat type
     */
    public String getBookedFlatType() {
        return bookedFlatType;
    }

    /**
     * Gets the name of the project.
     * 
     * @return The project name
     */
    public String getProjectName() {
        return projectName;
    }

    /**
     * Gets the location or address of the project.
     * 
     * @return The project neighborhood
     */
    public String getProjectNeighborhood() {
        return projectNeighborhood;
    }

    /**
     * Gets the booking ID associated with this receipt.
     * 
     * @return The booking ID
     */
    public String getBookingId() {
        return bookingId;
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
