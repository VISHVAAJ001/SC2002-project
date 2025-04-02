package com.ntu.fdae.group1.bto.enums;

public enum MaritalStatus {
    SINGLE("Single"),
    MARRIED("Married");

    private final String status;

    MaritalStatus(String status) {
        this.status = status;
    }

    public String getStatus() {
        return status;
    }

    @Override
    public String toString() {
        return status;
    }

    // public static MaritalStatus valueOfIgnoreCase(String status) {
    // return valueOf(status.toUpperCase());
    // }
}
