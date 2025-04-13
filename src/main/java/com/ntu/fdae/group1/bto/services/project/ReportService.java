package com.ntu.fdae.group1.bto.services.project;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.enums.FlatType; // Import Enums
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.models.booking.Booking; // Import Models
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.user.User;
import com.ntu.fdae.group1.bto.repository.booking.IBookingRepository;
import com.ntu.fdae.group1.bto.repository.project.IApplicationRepository; // Keep if needed later
import com.ntu.fdae.group1.bto.repository.project.IProjectRepository;
import com.ntu.fdae.group1.bto.repository.user.IUserRepository;

/**
 * Implementation of the IReportService interface that provides report
 * generation
 * functionality for the BTO Management System.
 * <p>
 * This service is responsible for generating various reports about bookings,
 * applications, and projects. It pulls data from multiple repositories to
 * create
 * comprehensive reports that can be filtered based on different criteria such
 * as
 * flat type, project name, age, and marital status.
 * </p>
 * <p>
 * The service aggregates data from different sources and formats it into
 * readable
 * reports suitable for administrative analysis and decision making.
 * </p>
 */
public class ReportService implements IReportService {
    /**
     * Repository for accessing application data in reports.
     */
    private final IApplicationRepository applicationRepo;

    /**
     * Repository for accessing booking data in reports.
     */
    private final IBookingRepository bookingRepo;

    /**
     * Repository for accessing project data in reports.
     */
    private final IProjectRepository projectRepo;

    /**
     * Repository for accessing user data in reports.
     */
    private final IUserRepository userRepo;

    /**
     * Constructs a new ReportService with the repositories needed for report
     * generation.
     *
     * @param appRepo  Repository for application data
     * @param bookRepo Repository for booking data
     * @param projRepo Repository for project data
     * @param userRepo Repository for user data
     * @throws NullPointerException if any repository except appRepo is null
     */
    public ReportService(IApplicationRepository appRepo, IBookingRepository bookRepo, IProjectRepository projRepo,
            IUserRepository userRepo) {
        // this.applicationRepo = appRepo;
        this.bookingRepo = Objects.requireNonNull(bookRepo, "Booking Repository cannot be null");
        this.projectRepo = Objects.requireNonNull(projRepo, "Project Repository cannot be null");
        this.userRepo = Objects.requireNonNull(userRepo, "User Repository cannot be null");
        this.applicationRepo = appRepo;
    }

    /**
     * Generates a detailed booking report that can be filtered by various criteria.
     * <p>
     * This method creates a formatted text report of bookings, including applicant
     * details, project information, and booking specifics. The report can be
     * filtered
     * by flat type, project name, applicant age, and marital status.
     * </p>
     *
     * @param filters A map of filter criteria where keys are filter names
     *                (FLAT_TYPE,
     *                PROJECT_NAME, AGE, MARITAL_STATUS) and values are filter
     *                values
     * @return A formatted string containing the booking report
     * @throws NullPointerException if the filters map is null
     */
    @Override
    public String generateBookingReport(Map<String, String> filters) {
        Objects.requireNonNull(filters, "Filters map cannot be null"); // Accept empty map

        StringBuilder report = new StringBuilder();
        String headerFormat = "%-11s | %-15s | %-20s | %-3s | %-10s | %-10s%n";
        String rowFormat = "%-11s | %-15s | %-20s | %-3d | %-10s | %-10s%n"; // Added NRIC
        report.append("--- BTO Booking Report ---\n");
        report.append("Filters Applied: ");
        if (filters.isEmpty()) {
            report.append("None\n");
        } else {
            String filterString = filters.entrySet().stream()
                    .map(entry -> entry.getKey() + ": " + entry.getValue())
                    .collect(Collectors.joining(", "));
            report.append(filterString).append("\n");
        }
        report.append("-----------------------------------------------------------------------------------\n");
        report.append(String.format(headerFormat, "Booking ID", "Applicant NRIC", "Project Name", "Age", "Marital",
                "Flat Type"));
        report.append("-----------------------------------------------------------------------------------\n");

        // 1. Fetch all necessary data
        // We need all bookings, then look up user/project for each matching booking
        Map<String, Booking> allBookingsMap = bookingRepo.findAll();
        if (allBookingsMap == null || allBookingsMap.isEmpty()) {
            report.append("No bookings found in the system.\n");
            report.append("--- End of Report ---\n");
            return report.toString();
        }
        List<Booking> allBookings = new ArrayList<>(allBookingsMap.values()); // Work with a list

        int recordCount = 0;

        // 2. Iterate and Filter
        for (Booking booking : allBookings) {
            // Get associated data - handle cases where data might be missing
            User user = userRepo.findById(booking.getApplicantNric()); // Assuming Booking has getApplicantNric()
            Project project = projectRepo.findById(booking.getProjectId()); // Assuming Booking has getProjectId()

            if (user == null) {
                System.err.println("Report Warning: Skipping booking " + booking.getBookingId() + " - Applicant User ("
                        + booking.getApplicantNric() + ") not found.");
                continue;
            }
            if (project == null) {
                System.err.println("Report Warning: Skipping booking " + booking.getBookingId() + " - Project ("
                        + booking.getProjectId() + ") not found.");
                continue;
            }

            // Apply Filters
            boolean passesFilters = true; // Assume pass initially

            // Filter by Flat Type
            if (filters.containsKey("FLAT_TYPE")) {
                String filterFlatTypeStr = filters.get("FLAT_TYPE");
                try {
                    FlatType filterFlatType = FlatType.valueOf(filterFlatTypeStr); // Use valueOf directly
                    if (booking.getBookedFlatType() != filterFlatType) {
                        passesFilters = false;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(
                            "Report Warning: Invalid FLAT_TYPE filter value '" + filterFlatTypeStr + "' skipped.");
                    // Decide: skip this booking or ignore the filter? Let's ignore filter.
                }
            }

            // Filter by Project Name (Exact Match)
            if (passesFilters && filters.containsKey("PROJECT_NAME")) {
                String filterProjectName = filters.get("PROJECT_NAME");
                if (!project.getProjectName().equalsIgnoreCase(filterProjectName)) {
                    passesFilters = false;
                }
            }

            // Filter by Age (Exact Match)
            if (passesFilters && filters.containsKey("AGE")) {
                String filterAgeStr = filters.get("AGE");
                try {
                    int filterAge = Integer.parseInt(filterAgeStr);
                    if (user.getAge() != filterAge) {
                        passesFilters = false;
                    }
                } catch (NumberFormatException e) {
                    System.err.println("Report Warning: Invalid AGE filter value '" + filterAgeStr + "' skipped.");
                }
            }

            // Filter by Marital Status
            if (passesFilters && filters.containsKey("MARITAL_STATUS")) {
                String filterMaritalStr = filters.get("MARITAL_STATUS");
                try {
                    MaritalStatus filterMarital = MaritalStatus.valueOf(filterMaritalStr); // Use valueOf directly
                    if (user.getMaritalStatus() != filterMarital) {
                        passesFilters = false;
                    }
                } catch (IllegalArgumentException e) {
                    System.err.println(
                            "Report Warning: Invalid MARITAL_STATUS filter value '" + filterMaritalStr + "' skipped.");
                }
            }

            // 3. Append if passes filters
            if (passesFilters) {
                recordCount++;
                report.append(String.format(rowFormat,
                        booking.getBookingId(),
                        user.getNric(), // Include NRIC for clarity
                        project.getProjectName(),
                        user.getAge(),
                        user.getMaritalStatus(),
                        booking.getBookedFlatType()));
            }
        } // End loop through bookings

        // 4. Add Summary
        report.append("-----------------------------------------------------------------------------------\n");
        report.append("Total Records Matching Filters: ").append(recordCount).append("\n");
        report.append("--- End of Report ---\n");

        return report.toString();
    }
}