package com.ntu.fdae.group1.bto.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Abstract base class for common UI functionalities.
 */
public abstract class BaseUI {
    protected Scanner scanner;

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
        System.out.print(prompt + " (YYYY-MM-DD): ");
        String dateStr = scanner.nextLine();
        try {
            return LocalDate.parse(dateStr, DateTimeFormatter.ISO_LOCAL_DATE);
        } catch (DateTimeParseException e) {
            System.err.println("Invalid date format. Please use YYYY-MM-DD.");
            return null;
        }
    }

    /**
     * Prompts the user for an enum value with basic error handling.
     * 
     * @param <E>       The enum type.
     * @param prompt    The message to display before input.
     * @param enumClass The class of the enum.
     * @return The enum value entered by the user, or null if input is invalid.
     */
    public <E extends Enum<E>> E promptForEnum(String prompt, Class<E> enumClass) {
        System.out.print(prompt + " (Options: " + String.join(", ", getEnumNames(enumClass)) + "): ");
        String input = scanner.nextLine().toUpperCase();
        try {
            return Enum.valueOf(enumClass, input);
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input. Please enter one of the listed options.");
            return null;
        }
    }

    /**
     * Get string array of enum constant names.
     * 
     * @param <E>       The enum type.
     * @param enumClass The class of the enum.
     * @return Array of enum constant names.
     */
    private <E extends Enum<E>> String[] getEnumNames(Class<E> enumClass) {
        return java.util.Arrays.stream(enumClass.getEnumConstants())
                .map(Enum::name)
                .toArray(String[]::new);
    }

    /**
     * Prompts the user for confirmation (yes/no).
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
}