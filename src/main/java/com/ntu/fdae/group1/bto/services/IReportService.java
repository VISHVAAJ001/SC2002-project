package com.ntu.fdae.group1.bto.services;

import java.util.Map;

public interface IReportService {
    /**
     * Generates a report about bookings
     * 
     * @param filters Map of filter criteria
     * @return The generated report as a formatted string
     */
    String generateBookingReport(Map<String, String> filters);
}
