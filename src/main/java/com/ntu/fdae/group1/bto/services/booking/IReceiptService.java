package com.ntu.fdae.group1.bto.services.booking;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;

public interface IReceiptService {

    /**
     * Gathers necessary information and constructs a BookingReceiptInfo object
     * based on the provided Booking details.
     * Handles fetching related User and Project data.
     *
     * @param booking The core Booking object.
     * @return A populated BookingReceiptInfo DTO.
     * @throws DataAccessException If required User or Project data cannot be found.
     */
    BookingReceiptInfo generateBookingReceipt(Booking booking) throws DataAccessException;
}