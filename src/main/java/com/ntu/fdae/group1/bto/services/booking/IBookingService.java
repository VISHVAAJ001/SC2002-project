package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.exceptions.BookingException;

public interface IBookingService {
    Booking performBooking(HDBOfficer officer, String applicantNRIC, String flatType) throws BookingException;
}
