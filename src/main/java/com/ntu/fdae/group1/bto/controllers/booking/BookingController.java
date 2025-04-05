package com.ntu.fdae.group1.bto.controllers.booking;

import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.BookingException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IBookingService;

public class BookingController {
    // class BookingController {
    // - bookingService : IBookingService;
    // + BookingController(bookingService: IBookingService);
    // + createBooking(officer: HDBOfficer, applicantNric: String, flatType:
    // FlatType) : Booking <<throws BookingException, InvalidInputException>>;
    // }

    private final IBookingService bookingService;

    public BookingController(IBookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Booking createBooking(HDBOfficer officer, String applicantNric, FlatType flatType)
            throws BookingException, InvalidInputException {
        return bookingService.performBooking(officer, applicantNric, flatType);
    }
}
