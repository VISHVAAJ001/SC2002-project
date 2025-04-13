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

/**
 * Utility class providing common file operations, CSV handling, and data
 * parsing
 * capabilities for the BTO Management System.
 * 
 * This class offers static methods for:
 * <ul>
 * <li>Reading from and writing to CSV files</li>
 * <li>Parsing dates and enum values from strings</li>
 * <li>Handling string operations common in file processing</li>
 * <li>Safely parsing numeric types with default values</li>
 * </ul>
 * 
 * <p>
 * The class uses UTF-8 encoding for all file operations and ISO-8601 format
 * for date handling. It is designed to be used throughout the application for
 * consistent file and data manipulation.
 * </p>
 */
public final class FileUtil { // Make final, prevent instantiation if all methods are static

    private static final String CSV_DELIMITER = ","; // Or configure if needed
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private FileUtil() {
    }

    /**
     * Reads all lines from a CSV file, skipping the header.
     * 
     * This method reads a CSV file, processes each line by:
     * <ul>
     * <li>Skipping the header line</li>
     * <li>Splitting each line by the CSV delimiter</li>
     * <li>Removing double quotes from each field</li>
     * <li>Ignoring empty lines</li>
     * </ul>
     * All file operations use UTF-8 encoding.
     * 
     * 
     * @param filePath Path to the CSV file
     * @return List of String arrays, where each array represents a row's columns
     * @throws IOException If an I/O error occurs reading from the file
     */
    public static List<String[]> readCsvLines(String filePath) throws IOException {
        List<String[]> lines = new ArrayList<>();
        Path path = Paths.get(filePath);
        if (!Files.exists(path)) {
            // Handle missing files (e.g., return empty list or throw specific exception)
            System.err.println("Warning: File not found: " + filePath);
            return lines; // Return empty list
        }
        try (BufferedReader reader = Files.newBufferedReader(path)) {
            // Skip header line
            reader.readLine();
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) { // Skip empty lines
                    // Split the line based on the CSV delimiter
                    String[] fields = line.split(CSV_DELIMITER, -1); // -1 to keep trailing empty strings

                    // Remove double quotes from each field
                    for (int i = 0; i < fields.length; i++) {
                        fields[i] = fields[i].replace("\"", "");
                    }

                    // Add the cleaned fields to the list
                    lines.add(fields);
                }
            }
        }
        return lines;
    }

    /**
     * Writes data to a CSV file, overwriting existing content.
     * 
     * This method handles the complete process of writing data to a CSV file:
     * <ul>
     * <li>Creates parent directories if they don't exist</li>
     * <li>Writes the header row first</li>
     * <li>Writes each data row, joining column values with the CSV delimiter</li>
     * <li>Overwrites the file if it already exists</li>
     * </ul>
     * All file operations use UTF-8 encoding.
     * 
     * 
     * @param filePath Path to the CSV file to write to
     * @param data     List of String arrays representing rows (excluding header)
     * @param header   Array of strings for the header row
     * @throws IOException If an I/O error occurs writing to the file
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
     * Safely parses a LocalDate from a string using the predefined ISO date format.
     * 
     * This method handles all potential parsing issues:
     * <ul>
     * <li>Returns null for null or empty input strings</li>
     * <li>Trims the input string before parsing</li>
     * <li>Logs a warning if the date cannot be parsed</li>
     * </ul>
     * 
     * 
     * @param dateString The string representation of a date to parse
     * @return LocalDate object if successful, or null if parsing fails or input is
     *         invalid
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
     * Formats a LocalDate into a string using the predefined ISO date format.
     * 
     * This method handles:
     * <ul>
     * <li>Returning an empty string for null dates</li>
     * <li>Logging a warning if formatting fails</li>
     * <li>Providing today's date as fallback if formatting fails</li>
     * </ul>
     * 
     * 
     * @param date The LocalDate object to format
     * @return Formatted date string, empty string for null dates, or current date
     *         if formatting fails
     */
    public static String formatLocalDate(LocalDate date) {
        if (date == null) {
            return "";
        }
        try {
            return date.format(DATE_FORMATTER);
        } catch (Exception e) {
            System.err.println("Warning: Could not format date: " + date + " - " + e.getMessage());
            return LocalDate.now().format(DATE_FORMATTER);
        }
    }

    /**
     * Safely parses an enum value from a string (case-insensitive).
     * 
     * This method attempts to convert a string to an enum constant by:
     * <ul>
     * <li>Converting the string to uppercase</li>
     * <li>Trimming whitespace</li>
     * <li>Returning null for null or empty input strings</li>
     * <li>Logging a warning if the enum value cannot be parsed</li>
     * </ul>
     * 
     * 
     * @param enumClass The class of the enum
     * @param value     The string value to parse
     * @param <E>       The enum type
     * @return The enum constant if successful, or null if parsing fails or input is
     *         invalid
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
     * Safely parses an enum value from a string (case-insensitive) with a default
     * value.
     * 
     * This method extends {@link #parseEnum(Class, String)} by providing a default
     * value
     * when parsing fails. It:
     * <ul>
     * <li>Returns the default value for null or empty input strings</li>
     * <li>Logs a warning if the enum value cannot be parsed</li>
     * <li>Returns the default value if parsing fails</li>
     * </ul>
     * 
     * 
     * @param enumClass    The class of the enum
     * @param value        The string value to parse
     * @param defaultValue The value to return if parsing fails or input is invalid
     * @param <E>          The enum type
     * @return The enum constant if successful, or the defaultValue if parsing fails
     */
    public static <E extends Enum<E>> E parseEnum(Class<E> enumClass, String value, E defaultValue) {
        if (value == null || value.trim().isEmpty()) {
            // If the input string is invalid, return the provided default
            return defaultValue;
        }
        try {
            return Enum.valueOf(enumClass, value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Warning: Could not parse enum " + enumClass.getSimpleName()
                    + " from value '" + value + "'. Using default: " + defaultValue);
            return defaultValue;
        }
    }

    /**
     * Joins a list of strings with a specified delimiter.
     * 
     * This method:
     * <ul>
     * <li>Filters out null values from the list</li>
     * <li>Handles null or empty lists by returning an empty string</li>
     * <li>Joins all remaining strings with the specified delimiter</li>
     * </ul>
     * 
     * 
     * @param list      The list of strings to join
     * @param delimiter The delimiter to use between elements
     * @return A single string with all elements joined, or empty string for
     *         null/empty lists
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
     * Splits a delimited string into a List of strings.
     * 
     * This method:
     * <ul>
     * <li>Handles null or empty input by returning an empty list</li>
     * <li>Uses the specified delimiter as a regex for splitting</li>
     * <li>Preserves trailing empty strings by using -1 as the limit parameter</li>
     * </ul>
     * 
     * 
     * @param str       The string to split
     * @param delimiter The delimiter regex
     * @return List of strings resulting from the split operation (potentially
     *         empty)
     */
    public static List<String> splitString(String str, String delimiter) {
        if (str == null || str.trim().isEmpty()) {
            return new ArrayList<>();
        }
        // Use split with -1 limit to keep trailing empty strings if necessary
        return new ArrayList<>(Arrays.asList(str.split(delimiter, -1)));
    }

    /**
     * Safely parses an Integer from a string with a fallback default value.
     * 
     * This method:
     * <ul>
     * <li>Returns the default value for null or empty input strings</li>
     * <li>Trims the input string before parsing</li>
     * <li>Logs a warning if the number cannot be parsed</li>
     * <li>Returns the default value if parsing fails</li>
     * </ul>
     * 
     * 
     * @param value        The string to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed integer or the default value
     */
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

    /**
     * Safely parses a Double from a string with a fallback default value.
     * 
     * This method:
     * <ul>
     * <li>Returns the default value for null or empty input strings</li>
     * <li>Trims the input string before parsing</li>
     * <li>Logs a warning if the number cannot be parsed</li>
     * <li>Returns the default value if parsing fails</li>
     * </ul>
     * 
     * 
     * @param value        The string to parse
     * @param defaultValue The default value to return if parsing fails
     * @return The parsed double or the default value
     */
    public static Double parseDoubleOrDefault(String value, Double defaultValue) {
        if (value == null || value.trim().isEmpty())
            return defaultValue;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            System.err.println("Warning: Could not parse double: " + value);
            return defaultValue;
        }
    }
}