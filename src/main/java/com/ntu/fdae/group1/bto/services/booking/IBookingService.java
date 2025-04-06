package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;

/**
 * Defines the booking operations for the BTO system.
 */
public interface IBookingService {
    /**
     * Creates a booking for an applicant.
     *
     * @param officer       The officer creating the booking.
     * @param applicantNRIC The NRIC of the applicant.
     * @param flatType      The type of flat to book.
     * @return The created booking.
     * @throws BookingException if booking cannot be performed.
     */
    Booking performBooking(HDBOfficer officer, String applicantNRIC, FlatType flatType) throws BookingException;
}
