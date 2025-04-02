package com.ntu.fdae.group1.bto.utils;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public final class FileUtils { // Make final, prevent instantiation if all methods are static

    private static final String CSV_DELIMITER = ","; // Or configure if needed
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("d/M/yyyy"); // Adjust pattern

    private FileUtils() {
    } // Private constructor for utility class

    /**
     * Reads all lines from a CSV file, skipping the header.
     * Assumes UTF-8 encoding.
     * 
     * @param filePath Path to the CSV file.
     * @return List of String arrays, where each array represents a row's columns.
     * @throws IOException If an I/O error occurs.
     */
    public static List<String[]> readCsvLines(String filePath) throws IOException {
        List<String[]> lines = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            // Decide how to handle missing files - maybe return empty list or throw
            // specific exception
            System.err.println("Warning: File not found: " + filePath);
            return lines; // Return empty list
        }
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // Skip header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Skip empty lines
                    // Simple split, may need more robust CSV parsing for quoted fields
                    lines.add(line.split(CSV_DELIMITER, -1)); // -1 to keep trailing empty strings
                }
            }
        }
        return lines;
    }

    /**
     * Writes data to a CSV file, overwriting existing content.
     * Assumes UTF-8 encoding.
     * 
     * @param filePath Path to the CSV file.
     * @param data     List of String arrays representing rows (excluding header).
     * @param header   Array of strings for the header row.
     * @throws IOException If an I/O error occurs.
     */
    public static void writeCsvLines(String filePath, List<String[]> data, String[] header) throws IOException {
        Path path = Paths.get(filePath);
        // Ensure parent directory exists (optional but good practice)
        Files.createDirectories(path.getParent());

        try (BufferedWriter writer = Files.newBufferedWriter(path, StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {
            // Write header
            writer.write(String.join(CSV_DELIMITER, header));
            writer.newLine();
            // Write data
            for (String[] row : data) {
                // Handle potential nulls in row array if necessary before joining
                writer.write(String.join(CSV_DELIMITER, row));
                writer.newLine();
            }
        }
    }

    /**
     * Safely parses a LocalDate from a string using the predefined format.
     * 
     * @param dateString The string to parse.
     * @return LocalDate object or null if parsing fails or input is null/empty.
     */
    public static LocalDate parseLocalDate(String dateString) {
        if (dateString == null || dateString.trim().isEmpty()) {
            return null;
        }
        try {
            return LocalDate.parse(dateString.trim(), DATE_FORMATTER);
        } catch (DateTimeParseException e) {
            System.err.println("Warning: Could not parse date: " + dateString + " - " + e.getMessage());
            return null; // Or throw custom exception
        }
    }

    /**
     * Formats a LocalDate into a string using the predefined format.
     * 
     * @param date The LocalDate object.
     * @return Formatted date string or empty string if date is null.
     */
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        try {
            return date.format(DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Warning: Could not format date: " + date + " - " + e.getMessage());
            return "";
        }
    }

    /**
     * Safely parses an enum value from a string (case-insensitive).
     * 
     * @param enumClass The class of the enum.
     * @param value     The string value to parse.
     * @param <E>       The enum type.
     * @return The enum constant or null if parsing fails or input is null/empty.
     */
    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value) {
        if (value == null || value.trim().isEmpty()) {
            return null;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase()); // Standard practice
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Could not parse enum " + enumClass.getSimpleName() + " from value: " + value);
            return null; // Or throw custom exception
        }
    }

    /**
     * Joins a list of strings with a specified delimiter. Handles null list
     * gracefully.
     * 
     * @param list      The list of strings.
     * @param delimiter The delimiter to use.
     * @return Joined string or empty string if list is null or empty.
     */
    public static String joinList(List<String> list, String delimiter) {
        if (list == null || list.isEmpty()) {
            return "";
        }
        // Filter out potential nulls/empty strings within the list if needed before
        // joining
        return list.stream()
                .filter(s -> s != null) // Example: ignore nulls in list
                .collect(Collectors.joining(delimiter));
    }

    /**
     * Splits a delimited string into a List of strings. Handles null/empty input.
     * 
     * @param str       The string to split.
     * @param delimiter The delimiter regex.
     * @return List of strings (potentially empty).
     */
    public static List<String> splitString(String str, String delimiter) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Use split with -1 limit to keep trailing empty strings if necessary
        return new ArrayList<>(Arrays.asList(str.split(delimiter, -1)));
    }

    // Add other generic helpers as needed (e.g., safe integer parsing)
    public static Integer parseIntOrDefault(String value, Integer defaultValue) {
        if (value == null || value.trim().isEmpty())
            return defaultValue;
        try {
            return Integer.parseInt(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse integer: " + value);
            return defaultValue;
        }
    }
}