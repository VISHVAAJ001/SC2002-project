package com.ntu.fdae.group1.bto.views;

import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.models.user.Applicant;
// import com.ntu.fdae.group1.bto.views.BaseUI;

public class ApplicantUI extends BaseUI {
    private Applicant user;
    private ProjectController projectController;
    private ApplicationController appController;
    private EnquiryController enquiryController;

    public ApplicantUI(Applicant user, ProjectController projectController, ApplicationController appController,
            EnquiryController enquiryController) {
        this.user = user;
        this.projectController = projectController;
        this.appController = appController;
        this.enquiryController = enquiryController;
    }

    public void displayMainMenu() {
        displayMessage("Welcome Applicant: " + user.getName());
        // menu logic here
    }
}
