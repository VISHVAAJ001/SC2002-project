package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.FlatType;

/**
 * Represents detailed information about a specific flat type within a BTO
 * project.
 * <p>
 * This class models the characteristics of a particular flat type available in
 * a project,
 * including its type (e.g., 3-room, 4-room), inventory information, and
 * pricing.
 * It provides methods to track and update the availability of units as
 * applications
 * are processed and approved.
 * </p>
 * <p>
 * The class manages the total and remaining units of a specific flat type,
 * ensuring
 * that inventory is correctly adjusted during the application and booking
 * processes.
 * </p>
 */
public class ProjectFlatInfo {
    /**
     * The type of flat (e.g., 2-ROOM, 3-ROOM, 4-ROOM, 5-ROOM).
     */
    private FlatType flatType;

    /**
     * The total number of units of this flat type in the project.
     */
    private int totalUnits;

    /**
     * The number of units of this flat type that are still available for
     * application.
     */
    private int remainingUnits;

    /**
     * The price of this flat type in Singapore dollars.
     */
    private double price;

    /**
     * Constructs a new ProjectFlatInfo with the specified details.
     *
     * @param flatType       The type of flat (e.g., 2-ROOM, 3-ROOM)
     * @param totalUnits     The total number of units of this flat type in the
     *                       project
     * @param remainingUnits The number of units still available for application
     * @param price          The price of this flat type in Singapore dollars
     */
    public ProjectFlatInfo(FlatType flatType, int totalUnits, int remainingUnits, double price) {
        this.flatType = flatType;
        this.totalUnits = totalUnits;
        this.remainingUnits = remainingUnits;
        this.price = price;
    }

    /**
     * Decreases the number of remaining units by one, simulating a flat being
     * reserved.
     * <p>
     * This method is typically called when an application is approved, reducing the
     * available inventory. It prevents the remaining units count from going below
     * zero.
     * </p>
     *
     * @return true if a unit was successfully reserved, false if no units were
     *         available
     */
    public boolean decreaseRemainingUnits() {
        if (remainingUnits > 0) {
            remainingUnits--;
            return true;
        }
        return false;
    }

    /**
     * Increases the number of remaining units by one, simulating a flat being
     * released.
     * <p>
     * This method is typically called when an application is withdrawn or
     * cancelled,
     * returning a previously reserved unit to the available inventory.
     * </p>
     */
    public void increaseRemainingUnits() {
        remainingUnits++;
    }

    /**
     * Gets the name of this flat type as a string.
     *
     * @return The string representation of the flat type
     */
    public String getTypeName() {
        return flatType.toString();
    }

    /**
     * Gets the total number of units of this flat type in the project.
     *
     * @return The total number of units
     */
    public int getTotalUnits() {
        return totalUnits;
    }

    /**
     * Gets the number of units of this flat type that are still available for
     * application.
     *
     * @return The number of remaining units
     */
    public int getRemainingUnits() {
        return remainingUnits;
    }

    /**
     * Gets the price of this flat type.
     *
     * @return The price in Singapore dollars
     */
    public double getPrice() {
        return price;
    }

    /**
     * Gets the flat type enum value.
     *
     * @return The flat type
     */
    public FlatType getFlatType() {
        return flatType;
    }
}
