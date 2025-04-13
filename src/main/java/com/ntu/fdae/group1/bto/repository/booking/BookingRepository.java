package com.ntu.fdae.group1.bto.repository.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;
import com.ntu.fdae.group1.bto.repository.util.CsvRepositoryHelper;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRepository implements IBookingRepository {
    private static final String BOOKING_FILE_PATH = "resources/bookings.csv";
    private static final String[] BOOKING_CSV_HEADER = new String[] { 
        "bookingId", "applicationId", "applicantNric", "projectId",
        "bookedFlatType", "bookingDate"
    };

    private Map<String, Booking> bookings;
    private final CsvRepositoryHelper<String, Booking> csvHelper;
    
    public BookingRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                BOOKING_FILE_PATH,
                BOOKING_CSV_HEADER,
                this::deserializeBookings,
                this::serializeBookings 
        );
        // Load initial data
        try {
            this.bookings = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial booking load failed: " + e.getMessage());
            this.bookings = new HashMap<>(); // Start with empty map on failure
        }
    }

    @Override
    public Booking findById(String bookingId) {
        return bookings.get(bookingId);
    }

    @Override
    public Map<String, Booking> findAll() {
        return new HashMap<>(bookings);
    }

    @Override
    public void save(Booking booking) {
        if (booking == null || booking.getBookingId() == null) {
            System.err.println("Attempted to save null booking or booking with null ID");
            return;
        }
        bookings.put(booking.getBookingId(), booking);
        try {
           csvHelper.saveData(bookings);
        } catch (DataAccessException e) {
            System.err.println("Failed to save booking " + booking.getBookingId() + ": " + e.getMessage());
            throw e;
       }
    }

    @Override
    public void saveAll(Map<String, Booking> entities) {
        this.bookings = new HashMap<>(entities);
        try {
            csvHelper.saveData(bookings);
        } catch (DataAccessException e) {
             System.err.println("Failed to save all bookings: " + e.getMessage());
             throw e;
        }
    }

    @Override
    public Map<String, Booking> loadAll() throws DataAccessException {
        this.bookings = csvHelper.loadData();
        return new HashMap<>(bookings);
    }

    @Override
    public Booking findByApplicantNric(String nric) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicantNric().equals(nric)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public Booking findByApplicationId(String applicationId) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicationId().equals(applicationId)) {
                return booking;
            }
        }
        return null;
    }

    @Override
    public List<Booking> findByProjectId(String projectId) {
        List<Booking> projectBookings = new ArrayList<>();
        for (Booking booking : bookings.values()) {
            if (booking.getProjectId().equals(projectId)) {
                projectBookings.add(booking);
            }
        }
        return projectBookings;
    }

    private Map<String, Booking> deserializeBookings(List<String[]> bookingData) {
        Map<String, Booking> bookingMap = new HashMap<>();
         if (bookingData == null) return bookingMap;

        for (String[] row : bookingData) {
            if (row.length < 6) {
                 System.err.println("Skipping invalid booking row: " + String.join(",", row));
                 continue;
            }
            try {
                String bookingId = row[0];
                String applicationId = row[1];
                String applicantNric = row[2];
                String projectId = row[3];
                // Use parseEnum with null default if FlatType could be missing/invalid
                FlatType flatType = FileUtil.parseEnum(FlatType.class, row[4], null);
                 if (flatType == null) { // Handle case where flat type is essential and missing
                     System.err.println("Skipping booking row due to invalid flat type: " + row[4]);
                     continue;
                 }
                LocalDate bookingDate = FileUtil.parseLocalDate(row[5]);
                 if (bookingDate == null) { // Handle case where booking date is essential
                     System.err.println("Skipping booking row due to invalid booking date: " + row[5]);
                     continue;
                 }

                Booking booking = new Booking(
                        bookingId,
                        applicationId,
                        applicantNric,
                        projectId,
                        flatType,
                        bookingDate);

                bookingMap.put(bookingId, booking);
            } catch (Exception e) {
                 System.err.println("Error parsing booking row: " + String.join(",", row) + " - " + e.getMessage());
            }
        }
        return bookingMap;
    }

    // Method signature matches the Function expected by CsvRepositoryHelper
    private List<String[]> serializeBookings(Map<String, Booking> booksToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
         if (booksToSerialize == null) return serializedData;

        for (Booking booking : booksToSerialize.values()) {
            serializedData.add(new String[] {
                    booking.getBookingId(),
                    booking.getApplicationId(),
                    booking.getApplicantNric(),
                    booking.getProjectId(),
                    // Ensure bookedFlatType isn't null before calling toString()
                    booking.getBookedFlatType() != null ? booking.getBookedFlatType().toString() : "",
                    FileUtil.formatLocalDate(booking.getBookingDate()) // Util handles null date
            });
        }
        return serializedData;
    }
}