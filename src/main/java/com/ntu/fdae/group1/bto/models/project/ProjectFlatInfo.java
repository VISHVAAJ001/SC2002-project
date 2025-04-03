package com.ntu.fdae.group1.bto.models.project;

public class ProjectFlatInfo {
    private String typeName;
    private int totalUnits;
    private int remainingUnits;
    private double price;

    public ProjectFlatInfo(String typeName, int totalUnits, int remainingUnits, double price) {
        this.typeName = typeName;
        this.totalUnits = totalUnits;
        this.remainingUnits = remainingUnits;
        this.price = price;
    }

    public String getTypeName() {
        return typeName;
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
}
