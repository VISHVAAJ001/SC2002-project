package com.ntu.fdae.group1.bto.controllers.project;

import com.ntu.fdae.group1.bto.models.*;
import java.util.Map;

public class ReportController {
    private Map<String, Application> applicationRepo;
    private Map<String, Booking> bookingRepo;
    private Map<String, Project> projectRepo;
    private Map<String, User> userRepo;

    public ReportController(Map<String, Application> appMap, Map<String, Booking> bookMap, Map<String, Project> projMap, Map<String, User> userMap){
        this.applicationRepo = appMap;
        this.bookingRepo = bookMap;
        this.projectRepo = projMap;
        this.userRepo = userMap;
    }

    public String generateBookingReport(Map<String, String> filters){

    }

}
