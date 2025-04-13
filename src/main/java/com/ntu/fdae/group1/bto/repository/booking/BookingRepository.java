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

/**
 * Implementation of the IBookingRepository interface that persists Booking
 * entities
 * to a CSV file.
 * <p>
 * This repository manages booking data, providing CRUD operations and
 * specialized
 * queries for booking management in the BTO system. It uses a CSV file as the
 * persistent storage mechanism, with in-memory caching for efficient access.
 * </p>
 * <p>
 * The repository maintains thread safety for its internal state and handles
 * serialization and deserialization of booking data to and from the CSV format.
 * </p>
 */
public class BookingRepository implements IBookingRepository {
    private static final String BOOKING_FILE_PATH = "resources/bookings.csv";
    private static final String[] BOOKING_CSV_HEADER = new String[] {
            "bookingId", "applicationId", "applicantNric", "projectId",
            "bookedFlatType", "bookingDate"
    };

    private Map<String, Booking> bookings;
    private final CsvRepositoryHelper<String, Booking> csvHelper;

    /**
     * Constructs a new BookingRepository.
     * <p>
     * Initializes the repository with a CsvRepositoryHelper configured for
     * Booking entities and attempts to load existing booking data from the CSV
     * file.
     * If the initial data load fails, an empty booking collection is created.
     * </p>
     */
    public BookingRepository() {
        this.csvHelper = new CsvRepositoryHelper<>(
                BOOKING_FILE_PATH,
                BOOKING_CSV_HEADER,
                this::deserializeBookings,
                this::serializeBookings);
        // Load initial data
        try {
            this.bookings = this.csvHelper.loadData();
        } catch (DataAccessException e) {
            System.err.println("Initial booking load failed: " + e.getMessage());
            this.bookings = new HashMap<>(); // Start with empty map on failure
        }
    }

    /**
     * {@inheritDoc}
     * <p>
     * Retrieves a booking by its unique identifier from the in-memory cache.
     * Returns null if no booking exists with the specified ID.
     * </p>
     */
    @Override
    public Booking findById(String bookingId) {
        return bookings.get(bookingId);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Returns a defensive copy of the bookings map to prevent external
     * modification of the repository's internal state.
     * </p>
     */
    @Override
    public Map<String, Booking> findAll() {
        return new HashMap<>(bookings);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Saves a booking to both the in-memory cache and the CSV file.
     * Validates that the booking and its ID are not null before saving.
     * The method will terminate early without throwing an exception if
     * validation fails, but will throw any DataAccessExceptions from the
     * underlying storage mechanism.
     * </p>
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * Replaces all existing bookings with the provided collection and
     * persists them to the CSV file. Creates a defensive copy of the
     * provided map to maintain repository encapsulation.
     * </p>
     */
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

    /**
     * {@inheritDoc}
     * <p>
     * Reloads all booking data from the CSV file into the in-memory cache,
     * replacing any existing data. Returns a defensive copy of the loaded
     * bookings.
     * </p>
     */
    @Override
    public Map<String, Booking> loadAll() throws DataAccessException {
        this.bookings = csvHelper.loadData();
        return new HashMap<>(bookings);
    }

    /**
     * {@inheritDoc}
     * <p>
     * Searches the in-memory booking collection for a booking with the
     * specified applicant NRIC. Returns the first matching booking or
     * null if none is found.
     * </p>
     */
    @Override
    public Booking findByApplicantNric(String nric) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicantNric().equals(nric)) {
                return booking;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Searches the in-memory booking collection for a booking with the
     * specified application ID. Returns the first matching booking or
     * null if none is found.
     * </p>
     */
    @Override
    public Booking findByApplicationId(String applicationId) {
        for (Booking booking : bookings.values()) {
            if (booking.getApplicationId().equals(applicationId)) {
                return booking;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     * <p>
     * Searches the in-memory booking collection for bookings associated
     * with the specified project ID. Returns a list of matching bookings.
     * </p>
     */
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

    /**
     * Deserializes booking data from a list of CSV rows into a map of Booking
     * entities.
     * <p>
     * Each row is expected to contain the following fields in order:
     * bookingId, applicationId, applicantNric, projectId, bookedFlatType,
     * bookingDate.
     * Rows with missing or invalid data are skipped.
     * </p>
     *
     * @param bookingData the list of CSV rows to deserialize
     * @return a map of Booking entities keyed by bookingId
     */
    private Map<String, Booking> deserializeBookings(List<String[]> bookingData) {
        Map<String, Booking> bookingMap = new HashMap<>();
        if (bookingData == null)
            return bookingMap;

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

    /**
     * Serializes a map of Booking entities into a list of CSV rows.
     * <p>
     * Each row will contain the following fields in order:
     * bookingId, applicationId, applicantNric, projectId, bookedFlatType,
     * bookingDate.
     * </p>
     *
     * @param booksToSerialize the map of Booking entities to serialize
     * @return a list of CSV rows representing the serialized Booking entities
     */
    private List<String[]> serializeBookings(Map<String, Booking> booksToSerialize) {
        List<String[]> serializedData = new ArrayList<>();
        if (booksToSerialize == null)
            return serializedData;

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