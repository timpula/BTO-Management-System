package views;

import controllers.*;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HDBOfficerView {
    private Scanner scanner;
    private HDBOfficerController officerController;
    private UserController userController;
    private ProjectController projectController;
    private ApplicationController applicationController;
    private EnquiryController enquiryController;

    public HDBOfficerView() {
        scanner = new Scanner(System.in);
        officerController = new HDBOfficerController();
        userController = new UserController();
        projectController = new ProjectController();
        applicationController = new ApplicationController();
        enquiryController = new EnquiryController();
    }

    public void displayDashboard(HDBOfficer officer) {

        System.out.println("\n==========================================");
        System.out.println("         HDB OFFICER DASHBOARD");
        System.out.println("==========================================");
        System.out.println("Welcome, " + officer.getName() + "!");

        int choice;
        do {
            System.out.println("\n1. Register for Project");
            System.out.println("2. View Registration Status");
            System.out.println("3. View Assigned Project");
            System.out.println("4. Process Applications");
            System.out.println("5. Manage Flats");
            System.out.println("6. Reply to Enquiries");
            System.out.println("7. Update Profile");
            System.out.println("8. Search Applicant by NRIC");
            System.out.println("9. Manage Flat Selection");
            System.out.println("10. Generate Booking Receipt");
            System.out.println("11. Set Project Filters");
            System.out.println("12. Change Password");
            System.out.println("13. Logout");
            System.out.print("Please select an option: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayRegisterForProject(officer);
                    break;
                case 2:
                    displayViewRegistrationStatus(officer);
                    break;
                case 3:
                    displayViewAssignedProject(officer);
                    break;
                case 4:
                    displayProcessApplications(officer);
                    break;
                case 5:
                    displayManageFlats(officer);
                    break;
                case 6:
                    displayReplyToEnquiries(officer);
                    break;
                case 7:
                    displayUpdateProfile(officer);
                    break;
                case 8:
                    displaySearchApplicant(officer);
                    break;
                case 9:
                    displayManageFlatSelection(officer);
                    break;
                case 10:
                    displayGenerateReceipt(officer);
                    break;
                case 11:
                    displaySetFilters(officer);
                    break;
                case 12:
                    changePassword(officer);
                    break;
                case 13:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (choice != 13);
    }

    private void displayRegisterForProject(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         REGISTER FOR PROJECT");
        System.out.println("==========================================");

        // ✅ S: Show current project assignment and status
        System.out.println("DEBUG: Assigned project ID = " + officer.getAssignedProjectId());
        System.out.println("DEBUG: Registration status = " + officer.getRegistrationStatus());

        // Check if officer is already assigned to a project
        if (officer.getAssignedProjectId() != null && "Approved".equalsIgnoreCase(officer.getRegistrationStatus())) {
            System.out.println("You are already assigned to a project: " + officer.getAssignedProjectId());
            return;
        }

        // Display available projects
        List<Project> projects = projectController.viewAllProjects();
        List<Project> availableProjects = projects.stream()
                .filter(p -> p.getAvailableOfficerSlots() > 0)
                .collect(java.util.stream.Collectors.toList());

        if (availableProjects.isEmpty()) {
            System.out.println("No projects available for registration.");
            return;
        }

        System.out.println("Available Projects:");
        for (int i = 0; i < availableProjects.size(); i++) {
            Project project = availableProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " (" + project.getNeighborhood() + ") - " +
                    project.getAvailableOfficerSlots() + " slots available");
        }

        System.out.print("Select a project (enter number): ");
        int projectChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (projectChoice < 1 || projectChoice > availableProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Project selectedProject = availableProjects.get(projectChoice - 1);

        // Confirm registration
        System.out.println("You are registering for: " + selectedProject.getProjectName());
        System.out.print("Confirm registration? (Y/N): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            boolean success = officerController.registerForProject(officer.getNric(), selectedProject.getProjectId());
            if (success) {
                System.out.println("Registration submitted successfully!");
            } else {
                System.out.println("Failed to submit registration. Please try again later.");
            }
        } else {
            System.out.println("Registration cancelled.");
        }
    }

    private void displayViewRegistrationStatus(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         REGISTRATION STATUS");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null) {
            System.out.println("You haven't registered for any project yet.");
            return;
        }

        Project project = projectController.getProjectDetails(officer.getAssignedProjectId());
        String status = officerController.viewRegistrationStatus(officer.getNric(), officer.getAssignedProjectId());

        System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("Status: " + status);
    }

    private void displayViewAssignedProject(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         ASSIGNED PROJECT");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        Project project = officerController.viewAssignedProject(officer.getNric());

        if (project == null) {
            System.out.println("Assigned project not found in the system.");
            return;
        }
        System.out.println("Project ID: " + project.getProjectId());
        System.out.println("Name: " + project.getProjectName());
        System.out.println("Neighborhood: " + project.getNeighborhood());
        System.out.println("Application Period: " + project.getApplicationOpeningDate() + " to "
                + project.getApplicationClosingDate());

        System.out.println("\nFlat Types Available:");
        for (String flatType : project.getFlatTypeUnits().keySet()) {
            System.out.println("- " + flatType + ": " + project.getFlatTypeUnits().get(flatType) + " units");
        }
    }

    private void displayProcessApplications(HDBOfficer officer) {
    System.out.println("\n==========================================");
    System.out.println("         PROCESS APPLICATIONS");
    System.out.println("==========================================");

    if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
        System.out.println("You are not assigned to any project yet.");
        return;
    }

    List<Application> applications = applicationController.getApplicationsByProject(officer.getAssignedProjectId());

    if (applications.isEmpty()) {
        System.out.println("No applications to process.");
        return;
    }

    // Create a clean list of pending applications (removing debug statements)
    List<Application> pendingApplications = new ArrayList<>();
    for (Application app : applications) {
        // Skip officer's own applications
        if (app.getApplicantNRIC().equalsIgnoreCase(officer.getNric())) {
            continue;
        }
        
        // Only include pending applications
        if (app.getStatus().equalsIgnoreCase("Pending")) {
            pendingApplications.add(app);
        }
    }

    if (pendingApplications.isEmpty()) {
        System.out.println("No pending applications to process.");
        return;
    }

    // Rest of the method...
}

    private void displayManageFlats(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         MANAGE FLATS");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        Project project = officerController.viewAssignedProject(officer.getNric());

        System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("\nCurrent Flat Units:");

        for (String flatType : project.getFlatTypeUnits().keySet()) {
            System.out.println("- " + flatType + ": " + project.getFlatTypeUnits().get(flatType) + " units");
        }

        System.out.println("\nSelect option:");
        System.out.println("1. Update Flat Remaining Count");
        System.out.println("2. Back to Dashboard");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            System.out.println("\nSelect flat type to update:");

            int i = 1;
            List<String> flatTypes = new java.util.ArrayList<>(project.getFlatTypeUnits().keySet());
            for (String flatType : flatTypes) {
                System.out.println(i + ". " + flatType);
                i++;
            }

            System.out.print("Enter flat type number: ");
            int flatChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (flatChoice < 1 || flatChoice > flatTypes.size()) {
                System.out.println("Invalid selection.");
                return;
            }

            String selectedFlatType = flatTypes.get(flatChoice - 1);

            System.out.print("Enter new count for " + selectedFlatType + ": ");
            int newCount = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            if (newCount < 0) {
                System.out.println("Count cannot be negative.");
                return;
            }

            boolean updated = officerController.updateFlatRemaining(project.getProjectId(), selectedFlatType, newCount);

            if (updated) {
                System.out.println("Flat count updated successfully!");
            } else {
                System.out.println("Failed to update flat count.");
            }
        }
    }

    private void displayReplyToEnquiries(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         REPLY TO ENQUIRIES");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        List<Enquiry> enquiries = enquiryController.viewEnquiriesByProject(officer.getAssignedProjectId());

        if (enquiries.isEmpty()) {
            System.out.println("No enquiries for your project.");
            return;
        }

        System.out.println("Pending Enquiries:");
        List<Enquiry> pendingEnquiries = enquiries.stream()
                .filter(e -> e.getReply() == null)
                .collect(java.util.stream.Collectors.toList());

        if (pendingEnquiries.isEmpty()) {
            System.out.println("No pending enquiries to reply.");
            return;
        }

        for (int i = 0; i < pendingEnquiries.size(); i++) {
            Enquiry enquiry = pendingEnquiries.get(i);
            User user = userController.viewUserDetails(enquiry.getUserNRIC());

            System.out.println((i + 1) + ". Enquiry ID: " + enquiry.getEnquiryId());
            System.out.println("   From: " + user.getName() + " (" + user.getUserType() + ")");
            System.out.println("   Date: " + enquiry.getEnquiryDate());
            System.out.println("   Content: " + enquiry.getContent());
            System.out.println();
        }

        System.out.print("Select an enquiry to reply (enter number): ");
        int enquiryChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (enquiryChoice < 1 || enquiryChoice > pendingEnquiries.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Enquiry selectedEnquiry = pendingEnquiries.get(enquiryChoice - 1);

        System.out.println("\nEnquiry Content: " + selectedEnquiry.getContent());
        System.out.print("Enter your reply: ");
        String reply = scanner.nextLine();

        boolean success = enquiryController.replyToEnquiry(selectedEnquiry.getEnquiryId(), reply);

        if (success) {
            System.out.println("Reply sent successfully!");
        } else {
            System.out.println("Failed to send reply.");
        }
    }

    private void displayUpdateProfile(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         UPDATE PROFILE");
        System.out.println("==========================================");

        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        boolean changed = userController.changePassword(officer.getNric(), currentPassword, newPassword);

        if (changed) {
            System.out.println("Password changed successfully!");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }

    }

    // New method to search for applicants by NRIC
    private void displaySearchApplicant(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         SEARCH APPLICANT BY NRIC");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        System.out.print("Enter applicant's NRIC: ");
        String nric = scanner.nextLine().trim();

        // 🔒 Prevent officer from searching their own NRIC
        if (officer.getNric().equals(nric)) {
            System.out.println("You cannot search for your own application.");
            return;
        }

        Applicant applicant = (Applicant) officerController.retrieveApplicantByNRIC(nric);

        if (applicant == null) {
            System.out.println("No applicant found with NRIC: " + nric);
            return;
        }

        // Get the application for this applicant in the officer's project
        Application application = applicationController.getApplicationByApplicantAndProject(nric,
                officer.getAssignedProjectId());

        if (application == null) {
            System.out.println("This applicant has no application for your project.");
            return;
        }

        System.out.println("\nApplicant Details:");
        System.out.println("Name: " + applicant.getName());
        System.out.println("NRIC: " + applicant.getNric());
        System.out.println("Age: " + applicant.getAge());
        System.out.println("Marital Status: " + applicant.getMaritalStatus());

        System.out.println("\nApplication Details:");
        System.out.println("Application ID: " + application.getApplicationId());
        System.out.println("Status: " + application.getStatus());
        System.out.println("Application Date: " + application.getApplicationDate());
        System.out.println("Flat Type (if selected): "
                + (application.getFlatType() != null ? application.getFlatType() : "Not selected yet"));

        System.out.println("\nWhat would you like to do?");
        System.out.println("1. Process Application");
        System.out.println("2. Back to Dashboard");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (choice == 1) {
            // Only allow processing if application is in "Successful" status
            if (application.getStatus().equals("Successful")) {
                displayManageFlatSelectionForApplicant(officer, application, applicant);
            } else {
                System.out.println(
                        "This application is not in 'Successful' status and cannot be processed for flat selection.");
            }
        }
    }

    // New method to manage flat selection
    private void displayManageFlatSelection(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         MANAGE FLAT SELECTION");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        // Get successful applications for this project
        List<Application> applications = applicationController.getApplicationsByProject(officer.getAssignedProjectId());
        List<Application> successfulApplications = applications.stream()
                .filter(a -> a.getStatus().equals("Successful"))
                .collect(java.util.stream.Collectors.toList());

        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications to process for flat selection.");
            return;
        }

        System.out.println("Successful Applications:");
        for (int i = 0; i < successfulApplications.size(); i++) {
            Application app = successfulApplications.get(i);
            Applicant applicant = (Applicant) officerController.retrieveApplicantByNRIC(app.getApplicantNRIC());

            System.out.println((i + 1) + ". Application ID: " + app.getApplicationId());
            System.out.println("   Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
            System.out.println("   Flat Type (if selected): "
                    + (app.getFlatType() != null ? app.getFlatType() : "Not selected yet"));
            System.out.println();
        }

        System.out.print("Select an application for flat selection (enter number): ");
        int appChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (appChoice < 1 || appChoice > successfulApplications.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Application selectedApp = successfulApplications.get(appChoice - 1);
        Applicant applicant = (Applicant) officerController.retrieveApplicantByNRIC(selectedApp.getApplicantNRIC());

        displayManageFlatSelectionForApplicant(officer, selectedApp, applicant);
    }

    // Helper method to handle flat selection for a specific applicant
    private void displayManageFlatSelectionForApplicant(HDBOfficer officer, Application application,
            Applicant applicant) {
        System.out.println("\n==========================================");
        System.out.println("         FLAT SELECTION");
        System.out.println("==========================================");

        Project project = officerController.viewAssignedProject(officer.getNric());

        System.out.println("Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
        System.out.println("Project: " + project.getProjectName());

        System.out.println("\nAvailable Flat Types:");
        List<String> flatTypes = new java.util.ArrayList<>(project.getFlatTypeUnits().keySet());
        for (int i = 0; i < flatTypes.size(); i++) {
            String flatType = flatTypes.get(i);
            int remaining = project.getFlatTypeUnits().get(flatType);
            System.out.println((i + 1) + ". " + flatType + " (" + remaining + " remaining)");
        }

        System.out.print("Select flat type for this applicant (enter number): ");
        int flatChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (flatChoice < 1 || flatChoice > flatTypes.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        String selectedFlatType = flatTypes.get(flatChoice - 1);

        // Check if flat type has available units
        if (project.getFlatTypeUnits().get(selectedFlatType) <= 0) {
            System.out.println("No units of this flat type are available. Please select another type.");
            return;
        }

        System.out.println("\nConfirm flat selection:");
        System.out.println("Applicant: " + applicant.getName());
        System.out.println("Project: " + project.getProjectName());
        System.out.println("Selected Flat Type: " + selectedFlatType);
        System.out.print("Confirm? (Y/N): ");

        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("Y")) {
            // Update application with flat type
            boolean updated = officerController.updateFlatSelection(application.getApplicationId(), selectedFlatType);

            // Update applicant profile
            boolean profileUpdated = officerController.updateApplicantProfile(applicant.getNric(),
                    project.getProjectId(), selectedFlatType);

            // Update application status to "Booked"
            boolean statusUpdated = officerController.updateApplicationStatus(application.getApplicationId(), "Booked");

            // Update flat count
            int currentCount = project.getFlatTypeUnits().get(selectedFlatType);
            boolean flatCountUpdated = officerController.updateFlatRemaining(project.getProjectId(), selectedFlatType,
                    currentCount - 1);

            if (updated && profileUpdated && statusUpdated && flatCountUpdated) {
                System.out.println("Flat selection completed successfully!");
                System.out.println("Application status updated to 'Booked'");
                System.out.println("Applicant profile updated with flat type: " + selectedFlatType);
                System.out.println("Flat count updated.");

                // Generate receipt automatically
                Receipt receipt = officerController.generateBookingReceipt(application.getApplicationId());
                System.out.println("\nBooking Receipt Generated:");
                System.out.println("Receipt ID: " + receipt.getReceiptId());
                System.out.println(
                        "Applicant: " + receipt.getApplicantName() + " (NRIC: " + receipt.getApplicantNRIC() + ")");
                System.out.println("Project: " + receipt.getProjectName());
                System.out.println("Flat Type: " + receipt.getFlatType());
                System.out.println("Booking Date: " + receipt.getBookingDate());
            } else {
                System.out.println("Failed to complete flat selection. Please try again.");
            }
        } else {
            System.out.println("Flat selection cancelled.");
        }
    }

    // New method to generate receipt
    private void displayGenerateReceipt(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         GENERATE BOOKING RECEIPT");
        System.out.println("==========================================");

        if (officer.getAssignedProjectId() == null || !officer.getRegistrationStatus().equals("Approved")) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        System.out.print("Enter application ID: ");
        String applicationId = scanner.nextLine();

        Application application = applicationController.getApplicationById(applicationId);

        if (application == null) {
            System.out.println("Application not found.");
            return;
        }

        if (!application.getProjectId().equals(officer.getAssignedProjectId())) {
            System.out.println("This application is not for your assigned project.");
            return;
        }

        if (!application.getStatus().equals("Booked")) {
            System.out.println("This application is not in 'Booked' status. Cannot generate receipt.");
            return;
        }

        Receipt receipt = officerController.generateBookingReceipt(applicationId);

        if (receipt == null) {
            System.out.println("Failed to generate receipt.");
            return;
        }

        System.out.println("\nBooking Receipt Generated:");
        System.out.println("Receipt ID: " + receipt.getReceiptId());
        System.out.println("Applicant: " + receipt.getApplicantName() + " (NRIC: " + receipt.getApplicantNRIC() + ")");
        System.out.println("Age: " + receipt.getApplicantAge());
        System.out.println("Marital Status: " + receipt.getMaritalStatus());
        System.out.println("Project: " + receipt.getProjectName());
        System.out.println("Flat Type: " + receipt.getFlatType());
        System.out.println("Booking Date: " + receipt.getBookingDate());

        System.out.println("\nWould you like to print this receipt? (Y/N): ");
        String printChoice = scanner.nextLine();

        if (printChoice.equalsIgnoreCase("Y")) {
            System.out.println("Receipt sent to printer.");
            // Add actual printing logic here if needed
        }
    }

    private void displaySetFilters(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         SET PROJECT FILTERS");
        System.out.println("==========================================");

        System.out.print("Enter neighborhood to filter by (leave blank for no filter): ");
        String neighborhood = scanner.nextLine();

        System.out.print("Enter flat type to filter by (e.g., 2-Room, 3-Room; leave blank for no filter): ");
        String flatType = scanner.nextLine();

        // Use controller to set filters
        officerController.setFilters(officer.getNric(), neighborhood, flatType);

        System.out.println("Filters updated successfully!");
    }

    private void changePassword(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         CHANGE PASSWORD");
        System.out.println("==========================================");

        System.out.print("Enter current password: ");
        String currentPassword = scanner.nextLine();
        System.out.print("Enter new password: ");
        String newPassword = scanner.nextLine();

        boolean changed = officerController.changePassword(officer.getNric(), currentPassword, newPassword);

        if (changed) {
            System.out.println("Password changed successfully!");
            System.out.println("You will now be logged out for security reasons.");
        } else {
            System.out.println("Failed to change password. Please check your current password.");
        }
    }

}