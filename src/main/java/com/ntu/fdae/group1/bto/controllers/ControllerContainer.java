package com.ntu.fdae.group1.bto.controllers;

import com.ntu.fdae.group1.bto.controllers.booking.BookingController;
import com.ntu.fdae.group1.bto.controllers.booking.ReceiptController;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ReportController;
import com.ntu.fdae.group1.bto.controllers.user.AuthenticationController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.controllers.user.UserController;

public class ControllerContainer {
    public AuthenticationController authController;
    public UserController userController;
    public ProjectController projectController;
    public ApplicationController appController;
    public OfficerRegistrationController officerRegController;
    public BookingController bookingController;
    public ReceiptController receiptController;
    public EnquiryController enquiryController;
    public ReportController reportController;

    public ControllerContainer(AuthenticationController auth, UserController user, ProjectController proj,
            ApplicationController app,
            OfficerRegistrationController reg, BookingController book, ReceiptController receipt,
            EnquiryController enq, ReportController report) {
        this.authController = auth;
        this.userController = user;
        this.projectController = proj;
        this.appController = app;
        this.officerRegController = reg;
        this.bookingController = book;
        this.receiptController = receipt;
        this.enquiryController = enq;
        this.reportController = report;
    }
}
