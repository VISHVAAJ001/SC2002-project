package com.ntu.fdae.group1.bto.services.project;

import java.util.Map;

/**
 * Service interface for report generation operations in the BTO Management
 * System.
 * <p>
 * This interface defines the contract for generating various reports related to
 * BTO housing projects, applications, and bookings. It provides methods for HDB
 * staff
 * to retrieve statistical data and insights about the system's operations.
 * </p>
 * <p>
 * Reports can include metrics such as application counts, booking statuses,
 * project popularity, and other statistical information useful for
 * administrative
 * decision-making.
 * </p>
 */
public interface IReportService {
    /**
     * Generates a report about bookings
     * 
     * @param filters Map of filter criteria
     * @return The generated report as a formatted string
     */
    String generateBookingReport(Map<String, String> filters);
}