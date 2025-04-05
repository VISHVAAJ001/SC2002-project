package com.ntu.fdae.group1.bto.models.project;

import com.ntu.fdae.group1.bto.enums.FlatType;

public class ProjectFlatInfo {
    private FlatType flatType;
    private int totalUnits;
    private int remainingUnits;
    private double price;

    public ProjectFlatInfo(FlatType flatType, int totalUnits, int remainingUnits, double price) {
        this.flatType = flatType;
        this.totalUnits = totalUnits;
        this.remainingUnits = remainingUnits;
        this.price = price;
    }

    public boolean decreaseRemainingUnits() {
        if (remainingUnits > 0) {
            remainingUnits--;
            return true;
        }
        return false;
    }

    public void increaseRemainingUnits() {
        remainingUnits++;
    }

    public String getTypeName() {
        return flatType.toString();
    }

    public int getTotalUnits() {
        return totalUnits;
    }

    public int getRemainingUnits() {
        return remainingUnits;
    }

    public double getPrice() {
        return price;
    }

    public FlatType getFlatType() {
        return flatType;
    }
}
