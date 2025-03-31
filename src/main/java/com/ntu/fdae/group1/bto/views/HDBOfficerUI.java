package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.*;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.models.user.HDBOfficer;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ReportController;
import com.ntu.fdae.group1.bto.controllers.user.OfficerController;

public class HDBOfficerUI extends BaseUI {
    private HDBOfficer user;
    private ProjectController projectController;
    private OfficerController officerController;
    private EnquiryController enquiryController;

    public HDBOfficerUI(HDBOfficer user, ProjectController projectController,
            OfficerController officerController, EnquiryController enquiryController) {
        this.user = user;
        this.projectController = projectController;
        this.officerController = officerController;
        this.enquiryController = enquiryController;
    }

    public void displayMainMenu() {
        displayMessage("Welcome HDB Officer: " + user.getName());
        // menu logic here
    }
}
