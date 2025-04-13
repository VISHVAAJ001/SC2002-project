package com.ntu.fdae.group1.bto.repository.util;

import com.ntu.fdae.group1.bto.exceptions.DataAccessException;
import com.ntu.fdae.group1.bto.utils.FileUtil;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class CsvRepositoryHelper<ID, T> {

    private final String filePath;
    private final String[] header;
    // Function to convert raw CSV data (List<String[]>) into the entity map
    private final Function<List<String[]>, Map<ID, T>> deserializer;
    // Function to convert the entity map into raw CSV data (List<String[]>)
    private final Function<Map<ID, T>, List<String[]>> serializer;

    /**
     * Constructor for the helper.
     * @param filePath Path to the primary CSV file.
     * @param header The CSV header row.
     * @param deserializer Function that takes List<String[]> read from CSV and returns Map<ID, T>.
     * @param serializer Function that takes the current Map<ID, T> and returns List<String[]> to be written.
     */
    public CsvRepositoryHelper(String filePath,
                               String[] header,
                               Function<List<String[]>, Map<ID, T>> deserializer,
                               Function<Map<ID, T>, List<String[]>> serializer) {
        this.filePath = filePath;
        this.header = header;
        this.deserializer = deserializer;
        this.serializer = serializer;
    }

    /**
     * Loads data from the CSV file using the provided deserializer.
     * Handles file reading and exception wrapping.
     * @return The deserialized map of entities.
     * @throws DataAccessException if there's an error reading the file.
     */
    public Map<ID, T> loadData() throws DataAccessException {
        try {
            List<String[]> rawData = FileUtil.readCsvLines(filePath);
            return deserializer.apply(rawData);
        } catch (IOException e) {
            throw new DataAccessException("Error loading data from file: " + filePath + " - " + e.getMessage(), e);
        } catch (Exception e) { // Catch potential deserialization errors too
             throw new DataAccessException("Error deserializing data from file: " + filePath + " - " + e.getMessage(), e);
        }
    }

    /**
     * Saves the provided entity map to the CSV file using the provided serializer.
     * Handles file writing and exception wrapping.
     * @param entities The map of entities to save.
     * @throws DataAccessException if there's an error writing the file.
     */
    public void saveData(Map<ID, T> entities) throws DataAccessException {
        try {
            List<String[]> serializedData = serializer.apply(entities);
            FileUtil.writeCsvLines(filePath, serializedData, header);
        } catch (IOException e) {
            throw new DataAccessException("Error saving data to file: " + filePath + " - " + e.getMessage(), e);
        } catch (Exception e) { // Catch potential serialization errors 
            throw new DataAccessException("Error serializing data for file: " + filePath + " - " + e.getMessage(), e);
        }
    }
}