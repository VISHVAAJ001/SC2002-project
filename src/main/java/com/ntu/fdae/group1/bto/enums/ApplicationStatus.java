package com.ntu.fdae.group1.bto.enums;

public enum ApplicationStatus {
    // A: Status transitions must follow a defined order:
    // •Valid: Pending → Success → Booked
    // •Not allowed: Pending → Booked or Success → Pending
    // •For unsuccessful applications: Pending → Unsuccessful
    // •For withdrawals before booking: Pending → Success → Unsuccessful
    // •For withdrawals after booking: Pending → Success → Booked → Unsuccessful
    PENDING, SUCCESSFUL, UNSUCCESSFUL, BOOKED
}
