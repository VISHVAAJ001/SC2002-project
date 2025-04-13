package com.ntu.fdae.group1.bto.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;

/**
 * Abstract base class for common UI functionalities in the BTO Management
 * System.
 * <p>
 * This class provides a set of reusable UI components and input handling
 * methods
 * that are used across different user interfaces in the system. It centralizes
 * common operations like prompting for input, displaying messages, and
 * formatting
 * data for display.
 * </p>
 * <p>
 * All specific UI classes should extend this class to inherit its functionality
 * and maintain a consistent user experience throughout the application.
 * </p>
 */
public abstract class BaseUI {
    /**
     * Scanner object for reading user input from the console.
     * This is initialized in the constructor and used by all input prompting
     * methods.
     */
    protected Scanner scanner;

    /**
     * Standard date formatter used consistently across the UI for displaying
     * and parsing dates in ISO format (YYYY-MM-DD).
     */
    protected static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Constructs a BaseUI with the specified Scanner for input operations.
     *
     * @param scanner The Scanner object to use for reading user input
     * @throws IllegalArgumentException if scanner is null
     */
    public BaseUI(Scanner scanner) {
        if (scanner == null) {
            throw new IllegalArgumentException("Scanner cannot be null");
        }
        this.scanner = scanner;
    }

    /**
     * Displays a standard message to the console.
     * 
     * @param message The message to display.
     */
    public void displayMessage(String message) {
        System.out.println(message);
    }

    /**
     * Displays an error message, typically formatted differently.
     * 
     * @param message The error message to display.
     */
    protected void displayError(String message) {
        System.err.println("ERROR: " + message);
    }

    /**
     * Prompts the user for string input.
     * 
     * @param prompt The message to display before input.
     * @return The string entered by the user.
     */
    public String promptForInput(String prompt) {
        System.out.print(prompt + " ");
        return scanner.nextLine();
    }

    /**
     * Prompts the user for an integer input with basic error handling.
     * 
     * @param prompt The message to display before input.
     * @return The integer entered by the user, or -1 if input is invalid.
     */
    public int promptForInt(String prompt) {
        System.out.print(prompt + " ");
        int input = -1; // Default invalid value
        try {
            input = scanner.nextInt();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number.");
            // Consume the invalid input
        } finally {
            scanner.nextLine(); // Always consume the rest of the line
        }
        return input;
    }

    /**
     * Prompts the user for a double input with basic error handling.
     * 
     * @param prompt The message to display before input.
     * @return The double entered by the user, or -1.0 if input is invalid.
     */
    public double promptForDouble(String prompt) {
        System.out.print(prompt + " ");
        double input = -1.0; // Default invalid value
        try {
            input = scanner.nextDouble();
        } catch (InputMismatchException e) {
            System.err.println("Invalid input. Please enter a number.");
            // Consume the invalid input
        } finally {
            scanner.nextLine(); // Always consume the rest of the line
        }
        return input;
    }

    /**
     * Prompts the user for a date input with basic error handling.
     * 
     * @param prompt The message to display before input.
     * @return The LocalDate entered by the user, or null if input is invalid.
     */
    public LocalDate promptForDate(String prompt) {
        LocalDate date = null;
        DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE;

        while (date == null) { // Loop until a valid date is parsed
            System.out.print(prompt + " (YYYY-MM-DD): "); // Show prompt inside loop
            String dateStr = scanner.nextLine().trim();

            if (dateStr.isEmpty()) { // Handle empty input specifically
                displayError("Date input cannot be empty. Please try again.");
                continue; // Go to next loop iteration
            }

            try {
                date = LocalDate.parse(dateStr, formatter); // Assign to 'date' ONLY if parsing succeeds
            } catch (DateTimeParseException e) {
                displayError("Invalid date format. Please use YYYY-MM-DD.");
                // Loop will continue as 'date' is still null
            }
        }
        return date; // Return the valid date
    }

    /**
     * Prompts the user to select an item from a list of enum values.
     * <p>
     * Displays a numbered menu of enum options and processes the user's selection.
     * The user can cancel the selection by choosing option 0.
     * </p>
     *
     * @param <E>           The enum type
     * @param prompt        The message to display before showing options
     * @param enumClass     The class of the enum
     * @param allowedValues The list of enum values to display as options
     * @return The selected enum value, or null if the selection was cancelled
     */
    public <E extends Enum<E>> E promptForEnum(String prompt, Class<E> enumClass, List<E> allowedValues) {
        // Validate input list
        if (allowedValues == null || allowedValues.isEmpty()) {
            displayError("Cannot prompt for selection: No allowed enum values provided.");
            return null; // Or throw an IllegalArgumentException
        }

        int choice;
        final int cancelOptionNumber = 0; // Standard cancel option

        while (true) { // Loop until valid input or cancel
            displayMessage(prompt);
            AtomicInteger counter = new AtomicInteger(1); // For 1-based display index

            // Display the allowed enum values
            allowedValues
                    .forEach(value -> displayMessage("[" + counter.getAndIncrement() + "] " + formatEnumName(value)) // Use
                                                                                                                     // helper
                                                                                                                     // for
                                                                                                                     // formatting
                    );

            // Display the cancel option
            displayMessage("[" + cancelOptionNumber + "] Cancel / Back");
            displayMessage("---------------------------------");

            choice = promptForInt("Enter your choice:"); // Use robust promptForInt

            // Process the choice
            if (choice == cancelOptionNumber) {
                displayMessage("Selection cancelled.");
                return null;
            }

            if (choice > 0 && choice <= allowedValues.size()) {
                // Valid enum choice, return the selected value (adjusting for 0-based list
                // index)
                return allowedValues.get(choice - 1);
            }

            // If none of the above, the choice was invalid
            displayError("Invalid selection. Please enter a number from the list.");
            // Loop continues
        }
    }

    /**
     * Formats an enum constant for user-friendly display.
     * <p>
     * Converts the enum name from uppercase with underscores to a title case
     * format with spaces. For example, "TWO_ROOM" becomes "Two room".
     * </p>
     *
     * @param enumConstant The enum constant to format
     * @return A user-friendly string representation of the enum constant
     */
    protected String formatEnumName(Enum<?> enumConstant) {
        if (enumConstant == null)
            return "";
        // Simple example: Replace underscore with space, title case maybe?
        String name = enumConstant.name().replace('_', ' ');
        // Basic title case - improve if needed
        if (name.length() > 0) {
            name = Character.toUpperCase(name.charAt(0)) + name.substring(1).toLowerCase();
        }
        return name;
        // Or just return enumConstant.name(); for basic "TWO_ROOM" display
    }

    /**
     * Prompts the user for confirmation (Y/N).
     * 
     * @param prompt The message to display before input.
     * @return True if user confirms (Y/y), false otherwise.
     */
    public boolean promptForConfirmation(String prompt) {
        System.out.print(prompt + " (Y/N): ");
        String input = scanner.nextLine();
        return input.equalsIgnoreCase("Y");
    }

    /**
     * Pauses execution until the user presses Enter.
     */
    protected void pause() {
        System.out.print("\nPress Enter to continue...");
        scanner.nextLine();
    }

    /**
     * Displays a standard menu header.
     * 
     * @param title The title of the menu.
     */
    protected void displayHeader(String title) {
        System.out.println("\n-----------------------------------------");
        System.out.println(title);
        System.out.println("-----------------------------------------");
    }

    /**
     * Displays a list of items with custom formatting.
     * 
     * @param <T>       The type of items in the list.
     * @param items     The list of items to display.
     * @param formatter Function to convert each item to a string.
     */
    protected <T> void displayList(List<T> items, Function<T, String> formatter) {
        if (items == null || items.isEmpty()) {
            System.out.println("No items to display.");
            return;
        }

        for (int i = 0; i < items.size(); i++) {
            System.out.println((i + 1) + ". " + formatter.apply(items.get(i)));
        }
    }

    /**
     * Clears the console. Platform dependent, often avoided in simple CLIs.
     */
    protected void clearConsole() {
        try {
            final String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                System.out.print("\033[H\033[2J"); // ANSI escape code
                System.out.flush();
            }
        } catch (final Exception e) {
            System.out.println("Error clearing console: " + e.getMessage());
        }
    }

    /**
     * Formats a LocalDate safely for display, handling null values.
     * <p>
     * Uses the standard DATE_FORMATTER to format the date, or returns "N/A"
     * if the date is null.
     * </p>
     *
     * @param date The LocalDate to format
     * @return The formatted date string, or "N/A" if date is null
     */
    protected String formatDateSafe(LocalDate date) {
        return (date == null) ? "N/A" : DATE_FORMATTER.format(date);
    }
}