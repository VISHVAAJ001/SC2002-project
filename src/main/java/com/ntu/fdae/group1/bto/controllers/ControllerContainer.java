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

/**
 * Container class that aggregates all controllers used in the BTO application.
 * <p>
 * This class serves as a central repository for all controller instances,
 * providing a convenient way to access and manage controllers throughout the
 * application. It facilitates dependency injection and promotes clean
 * architectural design by decoupling controller access from their
 * implementation.
 * </p>
 * 
 * The container holds references to controllers handling various aspects of the
 * application including:
 * <ul>
 * <li>User authentication and management</li>
 * <li>BTO project operations</li>
 * <li>Application processing</li>
 * <li>Officer registration</li>
 * <li>Unit booking and receipts</li>
 * <li>Customer enquiries</li>
 * <li>System reporting</li>
 * </ul>
 * 
 * <p>
 * All controllers are initialized through the constructor, ensuring they are
 * properly set up when the container is created.
 * </p>
 */
public class ControllerContainer {
    /** Controller handling user authentication, login, and session management. */
    public AuthenticationController authController;

    /** Controller for user profile management and user-related operations. */
    public UserController userController;

    /**
     * Controller managing BTO projects, including creation, updates, and queries.
     */
    public ProjectController projectController;

    /** Controller for BTO housing applications and application processing. */
    public ApplicationController appController;

    /** Controller handling officer registration requests and approval workflows. */
    public OfficerRegistrationController officerRegController;

    /**
     * Controller managing flat booking operations including selection and
     * confirmation.
     */
    public BookingController bookingController;

    /**
     * Controller for generating and managing booking receipts and payment records.
     */
    public ReceiptController receiptController;

    /** Controller handling customer enquiries and support requests. */
    public EnquiryController enquiryController;

    /** Controller for generating system reports and analytics. */
    public ReportController reportController;

    /**
     * Constructs a new ControllerContainer with all required controller instances.
     *
     * @param auth    The authentication controller for user login and session
     *                management
     * @param user    The user management controller for profile operations
     * @param proj    The project controller for BTO project operations
     * @param app     The application controller for housing applications
     * @param reg     The officer registration controller for staff onboarding
     * @param book    The booking controller for flat selection and reservation
     * @param receipt The receipt controller for payment documentation
     * @param enq     The enquiry controller for customer support requests
     * @param report  The report controller for system analytics and reporting
     */
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
