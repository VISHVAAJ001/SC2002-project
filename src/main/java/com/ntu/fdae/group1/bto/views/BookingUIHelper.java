package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.user.UserController;
import com.ntu.fdae.group1.bto.models.booking.Booking;
import com.ntu.fdae.group1.bto.models.booking.BookingReceiptInfo;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Helper class for booking-related UI operations in the BTO Management System.
 * <p>
 * This class provides reusable UI components for displaying and managing
 * booking
 * data, including:
 * - Displaying lists of bookings with applicant information
 * - Facilitating user selection of bookings from a list
 * - Formatting and displaying booking receipt information
 * </p>
 * <p>
 * The helper follows a composition pattern, working with a BaseUI instance for
 * common UI operations and a UserController for retrieving applicant
 * information.
 * </p>
 */
public class BookingUIHelper {

    /**
     * The base UI component for common UI operations.
     */
    private final BaseUI baseUI;

    /**
     * The controller for retrieving user/applicant information.
     */
    private final UserController userController;

    /**
     * Constructs a new BookingUIHelper with the specified dependencies.
     *
     * @param baseUI         An instance of BaseUI for console I/O operations
     * @param userController Controller to fetch user information for bookings
     * @throws NullPointerException if either parameter is null
     */
    public BookingUIHelper(BaseUI baseUI, UserController userController) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null for BookingUIHelper");
        this.userController = Objects.requireNonNull(userController,
                "UserController cannot be null for BookingUIHelper");
    }

    /**
     * Displays a list of bookings and prompts the user to select one.
     * <p>
     * This method presents a formatted list of bookings with key information
     * including:
     * - Booking ID
     * - Applicant NRIC and name
     * - Flat type
     * - Booking date
     * </p>
     * <p>
     * The method returns the selected booking object or null if the user cancels
     * the selection or if the provided booking list is empty.
     * </p>
     *
     * @param bookings List of Booking objects to display
     * @param title    Title for the list header
     * @return The selected Booking object, or null if cancelled or list is empty
     */
    public Booking selectBookingFromList(List<Booking> bookings, String title) {
        this.baseUI.displayHeader(title);
        Map<Integer, Booking> bookingMap = new HashMap<>();

        if (bookings == null || bookings.isEmpty()) {
            this.baseUI.displayMessage("No bookings to display in this list.");
            return null;
        }

        AtomicInteger counter = new AtomicInteger(1);
        bookings.forEach(booking -> {
            String applicantName = userController.getUserName(booking.getApplicantNric()); // Use helper
            this.baseUI.displayMessage(String.format(
                    "[%d] BookingID: %-8s | NRIC: %-9s (%s) | Flat: %-9s | Date: %s",
                    counter.get(),
                    booking.getBookingId(),
                    booking.getApplicantNric(),
                    applicantName,
                    booking.getBookedFlatType(),
                    this.baseUI.formatDateSafe(booking.getBookingDate())));
            bookingMap.put(counter.getAndIncrement(), booking);
        });

        this.baseUI.displayMessage("[0] Cancel / Back");
        this.baseUI.displayMessage("----------------------------------");

        int choice = this.baseUI.promptForInt("Select booking number: ");
        if (choice <= 0 || !bookingMap.containsKey(choice)) {
            if (choice != 0)
                this.baseUI.displayError("Invalid selection.");
            this.baseUI.displayMessage("Operation cancelled.");
            return null;
        }
        return bookingMap.get(choice);
    }

    /**
     * Displays the formatted details of a booking receipt.
     * <p>
     * This method presents a comprehensive receipt for a booking, including:
     * - Booking ID and date
     * - Applicant personal information (name, NRIC, age, marital status)
     * - Project details (name, neighborhood)
     * - Flat type information
     * </p>
     * <p>
     * The method handles null receipt information gracefully by displaying an error
     * message instead of attempting to format a null object.
     * </p>
     *
     * @param receiptInfo The BookingReceiptInfo object containing receipt data
     */
    public void displayBookingReceipt(BookingReceiptInfo receiptInfo) {
        if (receiptInfo == null) {
            this.baseUI.displayError("No receipt information to display.");
            return;
        }
        this.baseUI.displayMessage("\n--- Booking Receipt ---");
        this.baseUI.displayMessage("Booking ID: " + receiptInfo.getBookingId());
        this.baseUI.displayMessage("Booking Date: " + this.baseUI.formatDateSafe(receiptInfo.getBookingDate()));
        this.baseUI.displayMessage("-----------------------");
        this.baseUI.displayMessage("Applicant Name: " + receiptInfo.getApplicantName());
        this.baseUI.displayMessage("Applicant NRIC: " + receiptInfo.getApplicantNric());
        this.baseUI.displayMessage("Applicant Age: " + receiptInfo.getApplicantAge()); // Assumes age is int/String
        this.baseUI.displayMessage("Marital Status: " + receiptInfo.getApplicantMaritalStatus()); // Assumes enum/String
        this.baseUI.displayMessage("-----------------------");
        this.baseUI.displayMessage("Project Name: " + receiptInfo.getProjectName());
        this.baseUI.displayMessage("Neighbourhood: " + receiptInfo.getProjectNeighborhood());
        this.baseUI.displayMessage("Booked Flat Type: " + receiptInfo.getBookedFlatType()); // Assumes enum/String
        this.baseUI.displayMessage("--- End of Receipt ---\n");
    }
}