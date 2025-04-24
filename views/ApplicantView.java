package views;

import controllers.*;
import models.*;

import java.util.List;
import java.util.Scanner;

public class ApplicantView{
    private Scanner scanner;
    private ApplicantController applicantController;
    private UserController userController;
    private ApplicationController applicationController;
    private ProjectController projectController;
    private EnquiryController enquiryController;
    private String currentUserNRIC; // To store the current user's NRIC

    public ApplicantView() {
        scanner = new Scanner(System.in);
        applicationController = new ApplicationController(); // Initialize applicationController first
        userController = new UserController();
        projectController = new ProjectController();
        enquiryController = new EnquiryController();
        applicantController = new ApplicantController(applicationController, userController, projectController);
    }

    public void displayDashboard(String nric) {
        this.currentUserNRIC = nric; // Store the NRIC for later use

        System.out.println("\n==========================================");
        System.out.println("         APPLICANT DASHBOARD");
        System.out.println("==========================================");
        System.out.println("Welcome, " + applicantController.getApplicantName(nric) + "!");
        System.out.println("User Type: " + applicantController.getApplicantUserType(nric));
        System.out.println("NRIC: " + nric);
        System.out.println("Age: " + applicantController.getApplicantAge(nric));
        System.out.println("Marital Status: " + applicantController.getApplicantMaritalStatus(nric));
        System.out.println("==========================================");

        int choice;
        do {
            System.out.println("\n1. View Eligible Projects");
            System.out.println("2. Apply for Project");
            System.out.println("3. View Application Status");
            System.out.println("4. Request Withdrawal");
            System.out.println("5. Submit Enquiry");
            System.out.println("6. View My Enquiries");
            System.out.println("7. Edit Enquiry");
            System.out.println("8. Delete Enquiry");
            System.out.println("9. Set Project Filters");
            System.out.println("10. Change Password");
            System.out.println("11. Logout");
            System.out.print("Please select an option: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayEligibleProjects();
                    break;
                case 2:
                    displayApplyForProject();
                    break;
                case 3:
                    displayApplicationStatus();
                    break;
                case 4:
                    displayWithdrawalRequest();
                    break;
                case 5:
                    displaySubmitEnquiry();
                    break;
                case 6:
                    displayViewEnquiries();
                    break;
                case 7:
                    displayEditEnquiry();
                    break;
                case 8:
                    displayDeleteEnquiry();
                    break;
                case 9:
                    displaySetFilters();
                    break;
                case 10:
                    changePassword();
                    return;
                case 11:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (choice != 11);
    }

    private void displayEligibleProjects() {
        System.out.println("\n==========================================");
        System.out.println("         ELIGIBLE PROJECTS");
        System.out.println("==========================================");
    
        // Get filtered projects through the controller
        List<Project> eligibleProjects = applicantController.filterProjects(currentUserNRIC);
    
        if (eligibleProjects == null || eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found with the current filters.");
            return;
        }
    
        System.out.println("ID\tName\tNeighborhood\tApplication Period");
    
        for (Project project : eligibleProjects) {
            System.out.println(project.getProjectId() + "\t" +
                    project.getProjectName() + "\t" +
                    project.getNeighborhood() + "\t" +
                    project.getApplicationOpeningDate() + " to " +
                    project.getApplicationClosingDate());
    
            System.out.println("Available Flat Types:");
            for (String flatType : project.getFlatTypeUnits().keySet()) {
                System.out.println("- " + flatType + ": " + project.getFlatTypeUnits().get(flatType) + " units");
            }
            System.out.println();
        }
    }

    private void displayApplyForProject() {
        System.out.println("\n==========================================");
        System.out.println("         APPLY FOR PROJECT");
        System.out.println("==========================================");

        // Check if applicant already has an active application
        Application currentApplication = applicantController.getCurrentApplication(currentUserNRIC);
        if (currentApplication != null) {
            System.out.println("You already have an active application. You can only apply for one project at a time.");
            return;
        }

        // Display eligible projects
        List<Project> eligibleProjects = applicantController.viewEligibleProjects(currentUserNRIC);

        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects found.");
            return;
        }

        System.out.println("Available Projects:");
        for (int i = 0; i < eligibleProjects.size(); i++) {
            Project project = eligibleProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        }

        System.out.print("Select a project (enter number): ");
        int projectChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (projectChoice < 1 || projectChoice > eligibleProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Project selectedProject = eligibleProjects.get(projectChoice - 1);

        // Check eligibility
        if (!applicantController.checkEligibility(currentUserNRIC, selectedProject)) {
            System.out.println("Sorry, you are not eligible for this project.");
            return;
        }

        // Confirm application
        System.out.println("You are applying for: " + selectedProject.getProjectName());
        System.out.print("Confirm application? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean success = applicantController.applyForProject(currentUserNRIC, selectedProject.getProjectId());
            if (success) {
                System.out.println("Application submitted successfully!");
            } else {
                System.out.println("Failed to submit application. Please try again later.");
            }
        } else {
            System.out.println("Application cancelled.");
        }
    }

    private void displayApplicationStatus() {
        System.out.println("\n==========================================");
        System.out.println("         APPLICATION STATUS");
        System.out.println("==========================================");
    
        // Call the controller to get application status
        Application application = applicantController.viewApplicationStatus(currentUserNRIC);
    
        if (application == null) {
            System.out.println("You have no active applications.");
            return;
        }
    
        // Retrieve project details for the application
        Project project = projectController.getProjectDetails(application.getProjectId());
    
        // Retrieve the applicant details
        Applicant applicant = applicantController.getApplicantByNRIC(currentUserNRIC);
    
        // Display application details
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("Application Date: " + application.getApplicationDate());
        System.out.println("Status: " + application.getStatus());
    
        // Display booked flat type from the applicant
        if (applicant.getBookedFlatType() != null && !applicant.getBookedFlatType().isEmpty()) {
            System.out.println("Booked Flat Type: " + applicant.getBookedFlatType());
        } else {
            System.out.println("Booked Flat Type: Not yet selected");
        }
    }

    private void displayWithdrawalRequest() {
        System.out.println("\n==========================================");
        System.out.println("         WITHDRAWAL REQUEST");
        System.out.println("==========================================");

        Application application = applicationController.getApplicationByNRIC(currentUserNRIC);

        if (application == null) {
            System.out.println("You have no active applications to withdraw.");
            return;
        }
        // Display application details
        Project project = projectController.getProjectDetails(application.getProjectId());
        System.out.println("Current Application:");
        System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("Status: " + application.getStatus());

        // Confirm withdrawal
        System.out.print("Are you sure you want to withdraw this application? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean success = applicantController.requestWithdrawal(currentUserNRIC, application.getApplicationId());
            if (success) {
                System.out.println("Withdrawal request submitted successfully!");
            } else {
                System.out.println("Failed to submit withdrawal request. Please try again later.");
            }
        } else {
            System.out.println("Withdrawal request cancelled.");
        }
    }

    private void displaySubmitEnquiry() {
        System.out.println("\n==========================================");
        System.out.println("         SUBMIT ENQUIRY");
        System.out.println("==========================================");
    
        // Retrieve eligible projects for the applicant
        List<Project> eligibleProjects = applicantController.viewEligibleProjects(currentUserNRIC);
    
        if (eligibleProjects.isEmpty()) {
            System.out.println("No eligible projects available for enquiry.");
            return;
        }
    
        System.out.println("Select a project for your enquiry:");
        for (int i = 0; i < eligibleProjects.size(); i++) {
            Project project = eligibleProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        }
    
        System.out.print("Enter project number: ");
        int projectChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        if (projectChoice < 1 || projectChoice > eligibleProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }
    
        Project selectedProject = eligibleProjects.get(projectChoice - 1);
    
        // Get enquiry content
        System.out.println("Enter your enquiry about " + selectedProject.getProjectName() + ":");
        String content = scanner.nextLine();
    
        // Delegate enquiry creation to the EnquiryController
        boolean success = enquiryController.createEnquiry(currentUserNRIC, selectedProject.getProjectId(), content);
    
        if (success) {
            System.out.println("Enquiry submitted successfully!");
        } else {
            System.out.println("Failed to submit enquiry. Please try again later.");
        }
    }

    private void displayViewEnquiries() {
        System.out.println("\n==========================================");
        System.out.println("         MY ENQUIRIES");
        System.out.println("==========================================");

        List<Enquiry> enquiries = enquiryController.viewEnquiries(currentUserNRIC);

        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries.");
            return;
        }

        for (Enquiry enquiry : enquiries) {
            Project project = projectController.getProjectDetails(enquiry.getProjectId());

            System.out.println("ID: " + enquiry.getEnquiryId());
            System.out.println("Project: " + project.getProjectName());
            System.out.println("Date: " + enquiry.getEnquiryDate());
            System.out.println("Content: " + enquiry.getContent());

            if (enquiry.getReply() != null) {
                System.out.println("Reply: " + enquiry.getReply());
                System.out.println("Reply Date: " + enquiry.getReplyDate());
            } else {
                System.out.println("Reply: Pending");
            }

            System.out.println();
        }
    }

    private void displayEditEnquiry() {
        System.out.println("\n==========================================");
        System.out.println("         EDIT ENQUIRY");
        System.out.println("==========================================");

        // Retrieve all enquiries for the applicant
        List<Enquiry> enquiries = enquiryController.viewEnquiries(currentUserNRIC);

        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries to edit.");
            return;
        }

        // Display enquiries
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". " + enquiry.getContent() + " (Project: " + enquiry.getProjectId() + ")");
        }

        System.out.print("Select an enquiry to edit (enter number): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice < 1 || choice > enquiries.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Enquiry selectedEnquiry = enquiries.get(choice - 1);

        // Prompt for new content
        System.out.println("Current Content: " + selectedEnquiry.getContent());
        System.out.print("Enter new content: ");
        String newContent = scanner.nextLine();

        // Update the enquiry
        boolean success = enquiryController.editEnquiry(selectedEnquiry.getEnquiryId(), newContent);

        if (success) {
            System.out.println("Enquiry updated successfully!");
        } else {
            System.out.println("Failed to update enquiry. Please try again later.");
        }
    }

    private void displayDeleteEnquiry() {
        System.out.println("\n==========================================");
        System.out.println("         DELETE ENQUIRY");
        System.out.println("==========================================");

        // Retrieve all enquiries for the applicant
        List<Enquiry> enquiries = enquiryController.viewEnquiries(currentUserNRIC);

        if (enquiries.isEmpty()) {
            System.out.println("You have no enquiries to delete.");
            return;
        }

        // Display enquiries
        for (int i = 0; i < enquiries.size(); i++) {
            Enquiry enquiry = enquiries.get(i);
            System.out.println((i + 1) + ". " + enquiry.getContent() + " (Project: " + enquiry.getProjectId() + ")");
        }

        System.out.print("Select an enquiry to delete (enter number): ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice < 1 || choice > enquiries.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Enquiry selectedEnquiry = enquiries.get(choice - 1);

        // Confirm deletion
        System.out.print("Are you sure you want to delete this enquiry? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean success = enquiryController.deleteEnquiry(selectedEnquiry.getEnquiryId());

            if (success) {
                System.out.println("Enquiry deleted successfully!");
            } else {
                System.out.println("Failed to delete enquiry.");
            }
        } else {
            System.out.println("Deletion cancelled.");
        }
    }

    private void displaySetFilters() {
        System.out.println("\n==========================================");
        System.out.println("         SET PROJECT FILTERS");
        System.out.println("==========================================");

        System.out.print("Enter neighborhood to filter by (leave blank for no filter): ");
        String neighborhood = scanner.nextLine();

        System.out.print("Enter flat type to filter by (e.g., 2-Room, 3-Room; leave blank for no filter): ");
        String flatType = scanner.nextLine();

        // Use controller to set filters
        applicantController.setFilters(currentUserNRIC, neighborhood, flatType);

        System.out.println("Filters updated successfully!");
    }

    public void changePassword() {
        System.out.println("\n==========================================");
        System.out.println("         CHANGE PASSWORD");
        System.out.println("==========================================");

        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        boolean changed = applicantController.changePassword(currentUserNRIC, currentPassword, newPassword);

        if (changed) {
            System.out.println("Password changed successfully!");
            System.out.println("You will now be logged out for security reasons.");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }
    }


}