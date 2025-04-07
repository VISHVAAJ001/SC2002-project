package com.ntu.fdae.group1.bto.controllers.project;

import java.util.Map;

import com.ntu.fdae.group1.bto.services.project.IReportService;

/**
 * Controller for report-related operations
 */
public class ReportController {
    private final IReportService reportService;

    /**
     * Constructs a new ReportController
     * 
     * @param reportService The report service to use
     */
    public ReportController(IReportService reportService) {
        this.reportService = reportService;
    }

    /**
     * Generates a booking report
     * 
     * @param filters Map of filter criteria
     * @return The generated report as a formatted string
     */
    public String generateBookingReport(Map<String, String> filters) {
        // The service layer handles generating the report based on filters
        return reportService.generateBookingReport(filters);
    }
}
