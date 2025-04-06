package com.ntu.fdae.group1.bto.repository.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import java.util.List;

public interface IBookingRepository {
    void save(Booking booking);
    Booking findById(String bookingId);
    Booking findByApplicantNric(String applicantNRIC);
    Booking findByApplicationId(String applicationId);
    List<Booking> findAll();
}
