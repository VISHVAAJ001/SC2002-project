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
 * Helper class for displaying Booking related lists and details.
 */
public class BookingUIHelper {

    private final BaseUI baseUI;
    private final UserController userController; // For getting applicant names

    public BookingUIHelper(BaseUI baseUI, UserController userController) {
        this.baseUI = Objects.requireNonNull(baseUI, "BaseUI cannot be null for BookingUIHelper");
        this.userController = Objects.requireNonNull(userController, "UserController cannot be null for BookingUIHelper");
    }

    /**
     * Displays a list of bookings and prompts for selection.
     * @param bookings List of Booking objects to display.
     * @param title Title for the list header.
     * @return The selected Booking object, or null if cancelled or list is empty.
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
                    this.baseUI.formatDateSafe(booking.getBookingDate())
            ));
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
     * @param receiptInfo The BookingReceiptInfo object containing receipt data.
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