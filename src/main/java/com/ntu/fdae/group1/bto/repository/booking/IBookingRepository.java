package com.ntu.fdae.group1.bto.repository.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.repository.IRepository;

public interface IBookingRepository extends IRepository<Booking, String> {
    Booking findByApplicantNric(String nric); // Nullable
}
