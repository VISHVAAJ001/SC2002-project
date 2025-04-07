package com.ntu.fdae.group1.bto.views;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.ntu.fdae.group1.bto.controllers.*;
import com.ntu.fdae.group1.bto.controllers.enquiry.EnquiryController;
import com.ntu.fdae.group1.bto.models.enquiry.Enquiry;
import com.ntu.fdae.group1.bto.models.project.Application;
import com.ntu.fdae.group1.bto.models.project.OfficerRegistration;
import com.ntu.fdae.group1.bto.models.project.Project;
import com.ntu.fdae.group1.bto.models.project.ProjectFlatInfo;
import com.ntu.fdae.group1.bto.models.user.HDBManager;
import com.ntu.fdae.group1.bto.controllers.project.ApplicationController;
import com.ntu.fdae.group1.bto.controllers.project.ProjectController;
import com.ntu.fdae.group1.bto.controllers.project.ReportController;
import com.ntu.fdae.group1.bto.controllers.project.OfficerRegistrationController;
import com.ntu.fdae.group1.bto.enums.ApplicationStatus;
import com.ntu.fdae.group1.bto.enums.FlatType;
import com.ntu.fdae.group1.bto.enums.MaritalStatus;
import com.ntu.fdae.group1.bto.exceptions.ApplicationException;
import com.ntu.fdae.group1.bto.exceptions.InvalidInputException;
import com.ntu.fdae.group1.bto.exceptions.RegistrationException;
import com.ntu.fdae.group1.bto.services.*;

public class HDBManagerUI extends BaseUI {
    private HDBManager user;
    private ProjectController projectController;
    private ApplicationController appController;
    private OfficerRegistrationController officerRegController;
    private EnquiryController enquiryController;
    private ReportController reportController;

    public HDBManagerUI(HDBManager user, ProjectController projectController, ApplicationController appController,
            OfficerRegistrationController officerRegController,
            EnquiryController enquiryController, ReportController reportController) {

        this.user = user;
        this.projectController = projectController;
        this.appController = appController;
        this.officerRegController = officerRegController;
        this.enquiryController = enquiryController;
        this.reportController = reportController;
    }

    public void displayMainMenu() {
        boolean keepRunning = true;
        while (keepRunning) {
            displayHeader("HDB Manager Menu - Welcome " + (user != null ? user.getName() : "User"));

            System.out.println("--- Manager Project Role ---");
            System.out.println("[1] Manage My Projects (Create/Edit/Delete/Visbility)"); // Combined
            System.out.println("[2] View All Created Projects");
            System.out.println("[3] View My Managed Projects");
            System.out.println("-------------------------------------");
            System.out.println("--- Manager Tasks ---");
            System.out.println("[4] Review Officer Registration (Pending/Approved)");
            System.out.println("[5] Review Pending BTO Applications (Approve/Reject)");
            System.out.println("[6] Review Pending Application Withdrawals (Approve/Reject)");
            System.out.println("-------------------------------------");
            System.out.println("--- Communication & Reports ---");
            System.out.println("[7] View/Reply Enquiries");
            System.out.println("[8] Generate Booking Report");
            System.out.println("-------------------------------------");
            System.out.println("--- Account ---");
            System.out.println("[9] Change Password");
            System.out.println("-------------------------------------");
            System.out.println("[0] Logout");
            System.out.println("=====================================");

            int choice = promptForInt("Enter your choice: ");

            try {
                switch (choice) {
                    case 1: handleManageProjects(); break;
                    case 2: handleViewAllProjects(); break;
                    case 3: handleViewMyProjects(); break;

                    case 4: handleReviewOfficerRegistrations(); break;
                    case 5: handleReviewApplications(); break;
                    case 6: handleReviewWithdrawals(); break;

                    case 7: handleViewReplyEnquiries(); break;
                    case 8: handleGenerateReport(); break; 

                    case 9: handleChangePassword(); break; 
                    case 0:
                        displayMessage("Logging out...");
                        keepRunning = false;
                        break;
                    default:
                        displayError("Invalid choice. Please try again.");
                }
            } catch (Exception e) {
                displayError("An error occurred: " + e.getMessage());
                // e.printStackTrace(); // For debugging
            }

            if (keepRunning && choice != 0) {
                pause();
            }

        }
    }
    private void handleManageProjects() throws RegistrationException, InvalidInputException {
         displayHeader("Manage BTO Projects");
         System.out.println("1. Create New Project");
         System.out.println("2. Edit Existing Project");
         System.out.println("3. Delete Project"); 
         System.out.println("4. Toggle Project Visibility");
         System.out.println("0. Back to Main Menu");
         int choice = promptForInt("Enter choice: ");

         switch(choice) {
             case 1: handleCreateProject(); break;
             case 2: handleEditProject(); break;
             case 3: handleDeleteProject(); break;
             case 4: handleToggleVisibility(); break;
             case 0: break;
             default: displayError("Invalid choice.");
         }
    }

     private void handleCreateProject() throws RegistrationException, InvalidInputException {
         displayHeader("Create New BTO Project");
         String name = promptForInput("Enter Project Name: ");
         String neighborhood = promptForInput("Enter Neighborhood: ");
         LocalDate openDate = promptForDate("Enter Application Opening Date (YYYY-MM-DD): ");
         LocalDate closeDate = promptForDate("Enter Application Closing Date (YYYY-MM-DD): ");
         int officerSlots = promptForInt("Enter Max HDB Officer Slots (1-10): ");

         Map<FlatType, ProjectFlatInfo> flatInfoMap = new HashMap<>();
          System.out.println("--- Enter Flat Details ---");
          for (FlatType type : List.of(FlatType.TWO_ROOM, FlatType.THREE_ROOM)) {
                int totalUnits = promptForInt("Enter Total Units for " + type.name() + ": ");
                double price = promptForDouble("Enter Price for " + type.name() + ": "); 
                flatInfoMap.put(type, new ProjectFlatInfo(type, totalUnits, totalUnits, 0.0)); // Initial remaining = total
          }

         // Call controller, which validates and calls service
         Project createdProject = projectController.createProject(user, name, neighborhood, flatInfoMap, openDate, closeDate, officerSlots);
         // Service layer should print success message, or controller can return status
         if (createdProject != null) {
             displayMessage("Project '" + createdProject.getProjectName() + "' created successfully with ID: " + createdProject.getProjectId());
         } else {
             displayError("Project creation failed (check logs or previous errors)."); // Should ideally not happen if exception is thrown
         }
     }

     private void handleEditProject() throws InvalidInputException {
         displayHeader("Edit Existing Project");
         List<Project> myProjects = projectController.getManagedProjects(user);
         Project projectToEdit = selectProjectFromList(myProjects, "Select Project to Edit");
         if (projectToEdit == null) return;

         displayStaffProjectDetails(projectToEdit);

         displayMessage("Enter new details (leave blank or enter ' ' to keep current):"); 
         String name = promptForInput("New Project Name ["+projectToEdit.getProjectName()+"]: ");
         String neighborhood = promptForInput("New Neighborhood ["+projectToEdit.getNeighborhood()+"]: ");

         LocalDate openDate = promptForDateOrKeep("New Opening Date (YYYY-MM-DD) ["+projectToEdit.getOpeningDate()+"]:", projectToEdit.getOpeningDate());
         LocalDate closeDate = promptForDateOrKeep("New Closing Date (YYYY-MM-DD) ["+projectToEdit.getClosingDate()+"]:", projectToEdit.getClosingDate());

         int officerSlots = promptForIntOrKeep("New Max Officer Slots ["+projectToEdit.getMaxOfficerSlots()+"] (1-10):", projectToEdit.getMaxOfficerSlots());


         boolean success = projectController.editProject(user, projectToEdit.getProjectId(),
                 name.isBlank() ? projectToEdit.getProjectName() : name.trim(),
                 neighborhood.isBlank() ? projectToEdit.getNeighborhood() : neighborhood.trim(),
                 openDate, // Use the potentially kept date
                 closeDate, // Use the potentially kept date
                 officerSlots); // Use the potentially kept slots

         if(success){
            displayMessage("Project updated successfully.");
         } else {
            displayError("Project update failed."); // Ideally, Controller/Service should provide reason
         }
     }

     private void handleDeleteProject() {
          displayHeader("Delete Project");
          List<Project> myProjects = projectController.getManagedProjects(user);
          Project projectToDelete = selectProjectFromList(myProjects, "Select Project to Delete");
          if (projectToDelete == null) return;

          if (promptForConfirmation("WARNING: Deleting a project might be irreversible and subject to rules (e.g., no active applications). Proceed? (yes/no): ")) {
              boolean success = projectController.deleteProject(user, projectToDelete.getProjectId());
              if (success) {
                  displayMessage("Project deletion request processed."); // Depend on repo implementation
              } else {
                  displayError("Project deletion failed or not allowed (check logs/previous errors).");
              }
          } else {
              displayMessage("Deletion cancelled.");
          }
     }

     private void handleToggleVisibility() {
           displayHeader("Toggle Project Visibility");
           List<Project> myProjects = projectController.getManagedProjects(user);
           Project projectToToggle = selectProjectFromList(myProjects, "Select Project to Toggle Visibility");
           if (projectToToggle == null) return;

           displayMessage("Current visibility: " + (projectToToggle.isVisible() ? "ON" : "OFF"));
            if (promptForConfirmation("Toggle visibility for project '" + projectToToggle.getProjectName() + "'? (yes/no): ")) {
                 boolean success = projectController.toggleVisibility(user, projectToToggle.getProjectId());
                 if(success) {
                     displayMessage("Visibility toggled successfully.");
                 } else {
                     displayError("Failed to toggle visibility.");
                 }
            } else {
                displayMessage("Operation cancelled.");
            }
     }

     private void handleViewAllProjects() {
         displayHeader("All BTO Projects");
         List<Project> allProjects = projectController.getAllProjects(user); // Pass user for potential authorization check
         // TODO: Add Filtering options here based on PDF (location, flat types etc) - requires more UI logic
          if (allProjects.isEmpty()) {
              displayMessage("No projects found in the system.");
          } else {
               Project selected = selectProjectFromList(allProjects, "All Projects");
               if(selected != null) {
                   displayStaffProjectDetails(selected);
               }
          }
     }

     private void handleViewMyProjects() {
          displayHeader("My Managed BTO Projects");
          List<Project> myProjects = projectController.getManagedProjects(user);
           if (myProjects.isEmpty()) {
              displayMessage("You are not managing any projects.");
          } else {
               Project selected = selectProjectFromList(myProjects, "My Managed Projects");
               if(selected != null) {
                   displayStaffProjectDetails(selected);
               }
          }
     }


    private void handleReviewOfficerRegistrations() throws RegistrationException {
        displayHeader("Review Pending Officer Registrations");
        List<OfficerRegistration> pendingRegs = officerRegController.getPendingRegistrations(user);

        if (pendingRegs.isEmpty()) {
            displayMessage("No pending officer registrations found.");
            return;
        }

        Map<Integer, OfficerRegistration> regMap = displayOfficerRegList(pendingRegs, "Pending Officer Registrations");
        if (regMap.isEmpty()) return; // Nothing to select

        int choice = promptForInt("Select registration number to review (or 0 to go back): ");
        if (choice == 0 || !regMap.containsKey(choice)) {
            if(choice != 0) displayError("Invalid selection.");
            return;
        }

        OfficerRegistration selectedReg = regMap.get(choice);
        boolean approve = promptForConfirmation("Approve registration for Officer " + selectedReg.getOfficerNric() + " for project " + selectedReg.getProjectId() + "? (yes/no): ");

        boolean success = officerRegController.reviewRegistration(user, selectedReg.getRegistrationId(), approve);
        if(success) {
            displayMessage("Registration review processed successfully.");
        } 
    }

    private void handleReviewApplications() throws ApplicationException {
        displayHeader("Review Pending BTO Applications");
        List<Application> pendingApps = appController.getApplicationsByStatus(user, ApplicationStatus.PENDING);

         if (pendingApps.isEmpty()) {
             displayMessage("No pending applications found globally.");
             return;
         }
         // Filter only applications for projects managed by this manager
         List<Project> myProjects = projectController.getManagedProjects(user);
         Set<String> myProjectIds = myProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());
         List<Application> relevantApps = pendingApps.stream()
                                            .filter(app -> myProjectIds.contains(app.getProjectId()))
                                            .collect(Collectors.toList());

         if (relevantApps.isEmpty()) {
             displayMessage("No pending applications found for the projects you manage.");
             return;
         }

         Map<Integer, Application> appMap = displayApplicationList(relevantApps, "Pending Applications for Your Projects");
         if (appMap.isEmpty()) return;

         int choice = promptForInt("Select application number to review (or 0 to go back): ");
         if (choice == 0 || !appMap.containsKey(choice)) {
              if(choice != 0) displayError("Invalid selection.");
             return;
         }

         Application selectedApp = appMap.get(choice);
         boolean approve = promptForConfirmation("Approve application " + selectedApp.getApplicationId() + " for Applicant " + selectedApp.getApplicantNric() + "? (yes/no): ");

         boolean success = appController.reviewApplication(user, selectedApp.getApplicationId(), approve);
         if(success) {
            displayMessage("Application review processed successfully.");
         } 
    }

    private void handleReviewWithdrawals() throws ApplicationException {
         displayHeader("Review Pending Application Withdrawals");
         // Fetch apps that *could* have withdrawals (PENDING or SUCCESSFUL)
         List<Application> allPotentialApps = new ArrayList<>(appController.getApplicationsByStatus(user, ApplicationStatus.PENDING));
         allPotentialApps.addAll(appController.getApplicationsByStatus(user, ApplicationStatus.SUCCESSFUL));

         // Filter for those with withdrawal requests AND managed by this manager
          List<Project> myProjects = projectController.getManagedProjects(user);
          Set<String> myProjectIds = myProjects.stream().map(Project::getProjectId).collect(Collectors.toSet());

         List<Application> pendingWithdrawals = allPotentialApps.stream()
                 .filter(app -> app.getRequestedWithdrawalDate() != null && myProjectIds.contains(app.getProjectId()))
                 .collect(Collectors.toList());

         if (pendingWithdrawals.isEmpty()) {
             displayMessage("No pending withdrawal requests found for the projects you manage.");
             return;
         }

         Map<Integer, Application> appMap = displayApplicationList(pendingWithdrawals, "Pending Withdrawal Requests for Your Projects");
         if(appMap.isEmpty()) return;

          int choice = promptForInt("Select application number to review withdrawal (or 0 to go back): ");
          if (choice == 0 || !appMap.containsKey(choice)) {
               if(choice != 0) displayError("Invalid selection.");
              return;
          }

          Application selectedApp = appMap.get(choice);
          boolean approve = promptForConfirmation("Approve withdrawal for application " + selectedApp.getApplicationId() + "? (yes/no): ");

          boolean success = appController.reviewWithdrawal(user, selectedApp.getApplicationId(), approve);
          if(success) {
             displayMessage("Withdrawal review processed successfully.");
          } 
    }

    private void handleViewReplyEnquiries() throws InvalidInputException {
         displayHeader("View/Reply Enquiries");
         List<Enquiry> allEnquiries = enquiryController.viewAllEnquiries(user); // Manager sees all

          if (allEnquiries.isEmpty()) {
             displayMessage("No enquiries found in the system.");
             return;
         }

         Map<Integer, Enquiry> enquiryMap = displayEnquiryList(allEnquiries, "All Enquiries (Sorted by Unreplied First)");
         if(enquiryMap.isEmpty()) return;

          int choice = promptForInt("Select enquiry number to reply (or 0 to go back): ");
          if (choice == 0 || !enquiryMap.containsKey(choice)) {
               if(choice != 0) displayError("Invalid selection.");
              return;
          }

          Enquiry selectedEnq = enquiryMap.get(choice);

          if (selectedEnq.isReplied()) {
              displayMessage("This enquiry has already been replied to.");
              return; 
          }

          // Preliminary permission check (Service layer does final check)
           boolean canReply = false;
            if(selectedEnq.getProjectId() == null) {
                canReply = true; // General enquiry
            } else {
                Project proj = projectController.findProjectById(selectedEnq.getProjectId());
                if(proj != null && proj.getManagerNric().equals(user.getNric())) {
                    canReply = true; // Manager in charge
                }
            }
            if (!canReply) {
                 displayError("You may not have permission to reply to this specific enquiry.");
                 return; 
            }


          String reply = promptForInput("Enter your reply: ");
          boolean success = enquiryController.replyToEnquiry(user, selectedEnq.getEnquiryId(), reply);
          if(success) {
             displayMessage("Reply submitted successfully.");
          } 
    }

    private void handleGenerateReport() {
        displayHeader("Generate Booking Report");
        Map<String, String> filters = new HashMap<>();

        displayMessage("Enter filter criteria (leave blank to ignore):");
        // --- Flat Type Filter ---
        String flatTypeInput = promptForInput("Filter by Flat Type (TWO_ROOM, THREE_ROOM): ").toUpperCase();
         if (!flatTypeInput.isBlank()) {
             try {
                 filters.put("FLAT_TYPE", FlatType.valueOf(flatTypeInput).name());
             } catch (IllegalArgumentException e) {
                  displayError("Invalid flat type '" + flatTypeInput + "'. Ignoring filter.");
             }
         }
         // --- Project Name Filter ---
        String projectNameFilter = promptForInput("Filter by Project Name (exact match): ");
         if (!projectNameFilter.isBlank()) filters.put("PROJECT_NAME", projectNameFilter);
         // --- Age Filter ---
        String ageFilter = promptForInput("Filter by Applicant Age (exact match): ");
         if (!ageFilter.isBlank()) {
             try {
                 Integer.parseInt(ageFilter); 
                 filters.put("AGE", ageFilter);
             } catch (NumberFormatException e){
                 displayError("Invalid age '" + ageFilter + "'. Ignoring filter.");
             }
         }
         // --- Marital Status Filter ---
        String maritalStatusInput = promptForInput("Filter by Marital Status (SINGLE, MARRIED): ").toUpperCase();
         if (!maritalStatusInput.isBlank()) {
             try {
                 filters.put("MARITAL_STATUS", MaritalStatus.valueOf(maritalStatusInput).name());
             } catch (IllegalArgumentException e) {
                 displayError("Invalid marital status '" + maritalStatusInput + "'. Ignoring filter.");
             }
         }

        displayMessage("Generating report with filters: " + filters);
        String report = reportController.generateBookingReport(user, filters);
        displayMessage("\n--- Report Start ---");
        System.out.println(report); 
        displayMessage("--- Report End ---");
        // pause(); 
    }

     private void handleChangePassword() {
         displayHeader("Change Password");
         String newPassword = promptForInput("Enter new password: ");
         String confirmPassword = promptForInput("Confirm new password: ");

         if (!newPassword.equals(confirmPassword)) {
             displayError("Passwords do not match.");
             return;
         }
         if (newPassword.isEmpty()) {
              displayError("Password cannot be empty.");
              return;
         }

         // Consider adding complexity rules here 

         boolean success = authController.changePassword(user, newPassword); // Assuming authController exists and has this method

         if (success) {
             displayMessage("Password changed successfully.");
         } else {
             displayError("Password change failed. Please try again later.");
         }
         // pause(); 
     }

    // --- Helper Methods for Displaying Lists and Details ---
     private Project selectProjectFromList(List<Project> projects, String listTitle) {
         if (projects == null || projects.isEmpty()) {
             displayMessage("No projects found matching the criteria.");
             return null;
         }
         displayHeader(listTitle);
         Map<Integer, Project> projectMap = new HashMap<>();
         int index = 1;
         for (Project p : projects) {
             System.out.printf("%d. %s (%s) - %s [%s]%n", index,
                     p.getProjectName(), p.getProjectId(), p.getNeighborhood(), p.isVisible() ? "Visible" : "Hidden");
             projectMap.put(index, p);
             index++;
         }
         System.out.println("0. Cancel");

         int choice = -1;
         while (choice < 0 || choice >= index) {
             choice = promptForInt("Select project number (or 0 to cancel):");
             if (choice == 0) return null;
             if (choice < 0 || choice >= index || !projectMap.containsKey(choice)) {
                 displayError("Invalid selection.");
                 choice = -1; // Reset choice to loop again
             }
         }
         return projectMap.get(choice);
     }

     private void displayStaffProjectDetails(Project project) {
         if (project == null) {
             displayError("Cannot display details for null project.");
             return;
         }
         displayHeader("Project Details: " + project.getProjectName());
         System.out.println("ID            : " + project.getProjectId());
         System.out.println("Neighborhood  : " + project.getNeighborhood());
         System.out.println("Manager NRIC  : " + project.getManagerNric());
         System.out.println("Visibility    : " + (project.isVisible() ? "ON" : "OFF"));
         System.out.println("Opening Date  : " + formatDate(project.getOpeningDate()));
         System.out.println("Closing Date  : " + formatDate(project.getClosingDate()));
         System.out.println("Max Officers  : " + project.getMaxOfficerSlots());
         System.out.println("Approved Off. : " + project.getApprovedOfficerNrics().size() + " / " + project.getMaxOfficerSlots());
         if(!project.getApprovedOfficerNrics().isEmpty()){
             System.out.println("  NRICs       : " + String.join(", ", project.getApprovedOfficerNrics()));
         }
         displayFlatInfoSection(project);
     }

     private void displayFlatInfoSection(Project project) {
          System.out.println("--- Flat Information ---");
          if (project.getFlatTypes() == null || project.getFlatTypes().isEmpty()) {
              System.out.println("  No flat information available.");
              return;
          }
          List<FlatType> displayOrder = List.of(FlatType.TWO_ROOM, FlatType.THREE_ROOM);
          for (FlatType type : displayOrder) {
               ProjectFlatInfo info = project.getFlatTypes().get(type);
               if (info != null) {
                    System.out.printf("  Type: %-10s | Total Units: %-4d | Remaining: %-4d%n", // | Price: $%.2f (Add if needed)
                            info.getFlatType(), info.getTotalUnits(), info.getRemainingUnits()
                    );
               }
          }
          System.out.println("------------------------");
     }

    // Helper to display a list of Applications and return a map for selection
    private Map<Integer, Application> displayApplicationList(List<Application> apps, String title) {
        displayHeader(title);
        Map<Integer, Application> appMap = new HashMap<>();
        if (apps == null || apps.isEmpty()) {
            displayMessage("No applications to display in this list.");
            return appMap; // Return empty map
        }

        int index = 1;
        for (Application app : apps) {
            Project proj = projectController.findProjectById(app.getProjectId()); // Fetch project for name
            String projName = (proj != null) ? proj.getProjectName() : "Unknown";
            String withdrawalStatus = app.getRequestedWithdrawalDate() != null ? " (Withdrawal Requested)" : "";
            System.out.printf("%d. AppID: %s | Applicant: %s | Project: %s (%s) | Status: %s%s | Pref: %s | Date: %s%n",
                    index, app.getApplicationId(), app.getApplicantNric(), projName, app.getProjectId(),
                    app.getStatus(), withdrawalStatus, app.getPreferredFlatType(), formatDate(app.getSubmissionDate()));
            appMap.put(index, app);
            index++;
        }
        System.out.println("0. Back");
        return appMap;
    }

    // Helper to display a list of Officer Registrations
    private Map<Integer, OfficerRegistration> displayOfficerRegList(List<OfficerRegistration> regs, String title) {
        displayHeader(title);
        Map<Integer, OfficerRegistration> regMap = new HashMap<>();
         if (regs == null || regs.isEmpty()) {
            displayMessage("No registrations to display in this list.");
            return regMap; // Return empty map
        }
        int index = 1;
        for (OfficerRegistration reg : regs) {
            Project proj = projectController.findProjectById(reg.getProjectId());
            String projName = (proj != null) ? proj.getProjectName() : "Unknown Project";
            System.out.printf("%d. RegID: %s | Officer NRIC: %s | Project: %s (%s) | Status: %s | Date: %s%n",
                    index, reg.getRegistrationId(), reg.getOfficerNric(), projName, reg.getProjectId(), reg.getStatus(), formatDate(reg.getRequestDate()));
            regMap.put(index, reg);
            index++;
        }
        System.out.println("0. Back");
        return regMap;
    }

    // Helper to display a list of Enquiries
    private Map<Integer, Enquiry> displayEnquiryList(List<Enquiry> enquiries, String title) {
        displayHeader(title);
        Map<Integer, Enquiry> enquiryMap = new HashMap<>();
        if (enquiries == null || enquiries.isEmpty()) {
            displayMessage("No enquiries to display in this list.");
            return enquiryMap; // Return empty map
        }

        // Sort unreplied first
        List<Enquiry> sortedEnquiries = enquiries.stream()
            .sorted(Comparator.comparing(Enquiry::isReplied)) 
            .collect(Collectors.toList());

        int index = 1;
        for (Enquiry enq : sortedEnquiries) {
            Project proj = (enq.getProjectId() != null) ? projectController.findProjectById(enq.getProjectId()) : null;
            String projName = (proj != null) ? proj.getProjectName() : "General";
            String repliedStatus = enq.isReplied() ? "[Replied]" : "[UNREPLIED]";
            System.out.printf("%d. %s EnqID: %s | User: %s | Project: %s (%s) | Date: %s%n   Q: %s%n",
                    index, repliedStatus, enq.getEnquiryId(), enq.getUserNric(), projName,
                    enq.getProjectId() == null ? "N/A" : enq.getProjectId(), formatDate(enq.getSubmissionDate()), enq.getContent());
            if (enq.isReplied()) {
                System.out.printf("   A: %s (on %s)%n", enq.getReply(), formatDate(enq.getReplyDate()));
            }
            enquiryMap.put(index, enq);
            index++;
        }
        System.out.println("0. Back");
        return enquiryMap;
    }

     // Helper method for parsing date, assuming it exists in BaseUI or needs implementation
     private LocalDate promptForDate(String prompt) {
         // Placeholder - implement robust date parsing in BaseUI/here
         while (true) {
            String input = promptForInput(prompt + " (YYYY-MM-DD): ");
            if (input.isBlank()) {
                displayError("Date cannot be blank.");
                continue;
            }
            try {
                 return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                 displayError("Invalid date format. Please use YYYY-MM-DD.");
            }
         }
     }

      // Helper method for parsing date OR allowing user to keep existing date
     private LocalDate promptForDateOrKeep(String prompt, LocalDate currentValue) {
         while (true) {
            String input = promptForInput(prompt + " (Enter YYYY-MM-DD or leave blank to keep '" + formatDate(currentValue) + "'): ");
            if (input.isBlank()) {
                 return currentValue; // Keep current
            }
            try {
                 return LocalDate.parse(input, DATE_FORMATTER);
            } catch (DateTimeParseException e) {
                 displayError("Invalid date format. Please use YYYY-MM-DD.");
            }
         }
     }

    // Helper to prompt for int or keep current value
    private int promptForIntOrKeep(String prompt, int currentValue) {
        while(true) {
            String input = promptForInput(prompt + " (Enter number or leave blank to keep '" + currentValue + "'): ");
            if(input.isBlank()) {
                return currentValue;
            }
            try {
                return Integer.parseInt(input);
            } catch (NumberFormatException e) {
                displayError("Invalid number format.");
            }
        }
    }

     // Helper for consistent date formatting
     private String formatDate(LocalDate date) {
         return (date == null) ? "N/A" : DATE_FORMATTER.format(date);
     }

}
