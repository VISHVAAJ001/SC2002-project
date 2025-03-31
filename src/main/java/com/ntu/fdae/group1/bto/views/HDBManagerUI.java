package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.*;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ReportController;
import com.ntu.fdae.group1.bto.controllers.user.OfficerController;

public class HDBManagerUI extends BaseUI {
    private HDBManager user;
    private ProjectController projectController;
    private ApplicationController appController;
    private OfficerController officerController;
    private EnquiryController enquiryController;
    private ReportController reportController;

    public HDBManagerUI(HDBManager user, ProjectController projectController, ApplicationController appController,
            OfficerController officerController,
            EnquiryController enquiryController, ReportController reportController) {

        this.user = user;
        this.projectController = projectController;
        this.appController = appController;
        this.officerController = officerController;
        this.enquiryController = enquiryController;
        this.reportController = reportController;
    }

    public void displayMainMenu() {
        displayMessage("Welcome HDB Manager: " + user.getName());

    }
}
