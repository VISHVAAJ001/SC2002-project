package com.ntu.fdae.group1.bto.services.booking;

import java.util.Map;

import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.Booking;

public interface IDataManager {

	void saveApplications(Map<String, Application> applicationRepo);
	// define

	void saveBookings(Map<String, Booking> bookingRepo);

}
