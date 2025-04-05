package com.ntu.fdae.group1.bto.controllers.booking;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.services.booking.IReceiptService;

public class ReceiptController {
    // class ReceiptController {
    // - receiptService : IReceiptService;
    // + ReceiptController(receiptService: IReceiptService);
    // + getBookingReceiptInfo(officer: HDBOfficer, booking: Booking) :
    // BookingReceiptInfo <<throws DataAccessException>>;
    // }

    private final IReceiptService receiptService;

    public ReceiptController(IReceiptService receiptService) {
        this.receiptService = receiptService;
    }

    public BookingReceiptInfo getBookingReceiptInfo(HDBOfficer officer, Booking booking) throws DataAccessException {
        // TODO: Implement the logic to retrieve booking receipt information
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
