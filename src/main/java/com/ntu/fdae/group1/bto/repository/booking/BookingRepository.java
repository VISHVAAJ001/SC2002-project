package com.ntu.fdae.group1.bto.repository.booking;

import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BookingRepository implements IBookingRepository {
    private static final String BOOKING_FILE_PATH = "resources/bookings.csv";

    private Map<String, Booking> bookings;

    public BookingRepository() {
        this.bookings = new HashMap<>();
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
        bookings.put(booking.getBookingId(), booking);
        saveAll(bookings);
    }

    @Override
    public void saveAll(Map<String, Booking> entities) {
        this.bookings = entities;
        try {
            FileUtil.writeCsvLines(BOOKING_FILE_PATH, serializeBookings(), getBookingCsvHeader());
        } catch (IOException e) {
            throw new DataAccessException("Error saving bookings to file: " + e.getMessage(), e);
        }
    }

    @Override
    public Map<String, Booking> loadAll() throws DataAccessException {
        try {
            List<String[]> bookingData = FileUtil.readCsvLines(BOOKING_FILE_PATH);
            bookings = deserializeBookings(bookingData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading bookings from file: " + e.getMessage(), e);
        }
        return bookings;
    }

    @Override
    public Booking findByApplicantNric(String nric) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicantNRIC().equals(nric)) {
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

    // Helper methods for serialization/deserialization
    private String[] getBookingCsvHeader() {
        return new String[] {
                "bookingId", "applicationId", "applicantNric", "projectId",
                "bookedFlatType", "bookingDate"
        };
    }

    private Map<String, Booking> deserializeBookings(List<String[]> bookingData) {
        Map<String, Booking> bookingMap = new HashMap<>();

        if (bookingData == null || bookingData.isEmpty()) {
            return bookingMap;
        }

        for (String[] row : bookingData) {
            if (row.length < 6)
                continue; // Skip invalid rows

            try {
                String bookingId = row[0];
                String applicationId = row[1];
                String applicantNric = row[2];
                String projectId = row[3];
                FlatType flatType = FileUtil.parseEnum(FlatType.class, row[4]);
                LocalDate bookingDate = FileUtil.parseLocalDate(row[5]);

                // Create the booking
                Booking booking = new Booking(
                        bookingId,
                        applicationId,
                        applicantNric,
                        projectId,
                        flatType,
                        bookingDate);

                bookingMap.put(bookingId, booking);
            } catch (IllegalArgumentException e) {
                System.err.println("Error parsing booking data: " + e.getMessage());
            }
        }

        return bookingMap;
    }

    private List<String[]> serializeBookings() {
        List<String[]> serializedData = new ArrayList<>();

        for (Booking booking : bookings.values()) {
            serializedData.add(new String[] {
                    booking.getBookingId(),
                    booking.getApplicationId(),
                    booking.getApplicantNRIC(),
                    booking.getProjectId(),
                    booking.getBookedFlatType().toString(),
                    FileUtil.formatLocalDate(booking.getBookingDate())
            });
        }

        return serializedData;
    }
}