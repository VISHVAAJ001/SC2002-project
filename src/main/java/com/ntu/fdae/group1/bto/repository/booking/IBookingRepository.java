package com.ntu.fdae.group1.bto.repository.booking;

import java.util.List;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.repository.IRepository;

public interface IBookingRepository extends IRepository<Booking, String> {
    Booking findByApplicantNric(String nric); // Nullable

    Booking findByApplicationId(String applicationId); // Nullable

    List<Booking> findByProjectId(String projectId); // New method

}
