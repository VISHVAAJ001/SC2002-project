package com.ntu.fdae.group1.bto.repository;

import com.ntu.fdae.group1.bto.models.project.Booking;

public interface IBookingRepository extends IRepository<Booking, String> {
    Booking findByApplicantNric(String nric); // Nullable
}
