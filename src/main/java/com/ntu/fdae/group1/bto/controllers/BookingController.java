package com.ntu.fdae.group1.bto.controllers;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IBookingService;

public class BookingController {
    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Booking createBooking(HDBOfficer officer, String applicantNRIC, String flatTypeStr) throws BookingException {
        FlatType flatType;
        try {
            flatType = FlatType.valueOf(flatTypeStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new BookingException("Invalid flat type provided: " + flatTypeStr);
        }
        return bookingService.performBooking(officer, applicantNRIC, flatType);
    }
}
