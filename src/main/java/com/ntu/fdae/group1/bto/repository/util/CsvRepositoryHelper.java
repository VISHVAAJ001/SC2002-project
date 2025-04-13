package com.ntu.fdae.group1.bto.repository.util;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;

/**
 * Helper class that provides common CSV file operations for repositories.
 * <p>
 * This class encapsulates the logic for reading from and writing to CSV files,
 * providing a reusable component for repository implementations. It handles
 * serialization and deserialization between entity objects and CSV format.
 * </p>
 * 
 * @param <ID> The type of identifier used for entities
 * @param <T>  The entity type this helper manages
 */
public class CsvRepositoryHelper<ID, T> {
    private final String filePath;
    private final String[] csvHeader;
    private final Function<List<String[]>, Map<ID, T>> deserializer;
    private final Function<Map<ID, T>, List<String[]>> serializer;

    /**
     * Constructs a new CsvRepositoryHelper with the specified parameters.
     *
     * @param filePath     The path to the CSV file
     * @param csvHeader    The header row for the CSV file
     * @param deserializer Function that takes List&lt;String[]&gt; read from CSV
     *                     and returns Map&lt;ID, T&gt;
     * @param serializer   Function that takes the current Map&lt;ID, T&gt; and
     *                     returns List&lt;String[]&gt; to be written
     */
    public CsvRepositoryHelper(String filePath, String[] csvHeader,
            Function<List<String[]>, Map<ID, T>> deserializer,
            Function<Map<ID, T>, List<String[]>> serializer) {
        this.filePath = filePath;
        this.csvHeader = csvHeader;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    /**
     * Loads data from the CSV file and converts it to a map of entities.
     * <p>
     * This method reads the CSV file at the configured path, skips the header row,
     * and uses the provided deserializer function to convert the CSV data into
     * entity objects.
     * </p>
     *
     * @return A map of entities, keyed by their identifiers
     * @throws DataAccessException If an error occurs while reading or parsing the
     *                             CSV file
     */
    public Map<ID, T> loadData() throws DataAccessException {
        try {
            List<String[]> rawData = FileUtil.readCsvLines(filePath);
            return deserializer.apply(rawData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading data from file: " + filePath + " - " + e.getMessage(), e);
        } catch (Exception e) { // Catch potential deserialization errors too
            throw new DataAccessException("Error deserializing data from file: " + filePath + " - " + e.getMessage(),
                    e);
        }
    }

    /**
     * Saves a map of entities to the CSV file.
     * <p>
     * This method uses the provided serializer function to convert entity objects
     * into CSV data, then writes that data to the configured file path with the
     * specified header.
     * </p>
     *
     * @param entities The map of entities to save
     * @throws DataAccessException If an error occurs while writing to the CSV file
     */
    public void saveData(Map<ID, T> entities) throws DataAccessException {
        try {
            List<String[]> serializedData = serializer.apply(entities);
            FileUtil.writeCsvLines(filePath, serializedData, csvHeader);
        } catch (IOException e) {
            throw new DataAccessException("Error saving data to file: " + filePath + " - " + e.getMessage(), e);
        } catch (Exception e) { // Catch potential serialization errors
            throw new DataAccessException("Error serializing data for file: " + filePath + " - " + e.getMessage(), e);
        }
    }
}