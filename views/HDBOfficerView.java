package views;

import controllers.*;
import models.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;

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
                    displayGenerateBookingReceipt(officer);
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
            // Update the officer's assigned project ID and registration status
            officer.setAssignedProjectId(selectedProject.getProjectId());
            officer.setRegistrationStatus("Pending");

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

    List<Registration> pendingRegs = officerController.getAllRegistrationsForOfficer(officer.getNric());

    if (pendingRegs.isEmpty()) {
        System.out.println("You have no pending registrations.");
        return;
    }

    for (Registration reg : pendingRegs) {
        Project project = projectController.getProjectDetails(reg.getProjectId());
        System.out.println("- Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("  Registration ID: " + reg.getRegistrationId());
        System.out.println("  Status: " + reg.getStatus());
        System.out.println("  Date: " + reg.getRegistrationDate());
        System.out.println();
    }
}

    private void displayViewAssignedProject(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         ASSIGNED PROJECTS");
        System.out.println("==========================================");

        List<Project> projects = officerController.getAllAssignedProjects(officer.getNric());
        if (projects == null || projects.isEmpty()) {
            System.out.println("No projects assigned.");
            return;
        }

        System.out.println("\nAssigned Projects:");
        for (Project project : projects) {
            System.out.println("\nProject Details:");
            System.out.println("Project Name: " + project.getProjectName());
            System.out.println("Project ID: " + project.getProjectId());
            System.out.println("Neighborhood: " + project.getNeighborhood());
            System.out.println("Available Flat Types:");
            for (Map.Entry<String, Integer> entry : project.getFlatTypeUnits().entrySet()) {
                System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " units");
            }
        }

        System.out.println("\nRegistration Status: " + officer.getRegistrationStatus());
    }

    private void displayProcessApplications(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         PROCESS APPLICATIONS");
        System.out.println("==========================================");

        List<Project> assignedProjects = officerController.getAllAssignedProjects(officer.getNric());
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        for (Project project : assignedProjects) {
            List<Application> applications = applicationController.getApplicationsByProject(project.getProjectId());
            
            if (!applications.isEmpty()) {
                System.out.println("\nApplications for " + project.getProjectName() + ":");
                
                for (Application app : applications) {
                    if (app.getStatus().equalsIgnoreCase("Pending")) {
                        User applicant = userController.viewUserDetails(app.getApplicantNRIC());
                        if (applicant != null) {
                            System.out.println("- Application ID: " + app.getApplicationId());
                            System.out.println("  Applicant: " + applicant.getName());
                            System.out.println("  NRIC: " + app.getApplicantNRIC());
                            System.out.println("  Flat Type: " + app.getFlatType());
                            System.out.println("  Status: " + app.getStatus());
                            System.out.println();
                        }
                    }
                }
            }
        }
    }

    private void displayManageFlats(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         MANAGE FLATS");
        System.out.println("==========================================");
    
        List<Project> assignedProjects = officerController.getAllAssignedProjects(officer.getNric());
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }
            System.out.println("\nAssigned Projects:");
        for (int i = 0; i < assignedProjects.size(); i++) {
            Project project = assignedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        }
    
        System.out.print("\nSelect a project to manage (enter number): ");
        int projChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        if (projChoice < 1 || projChoice > assignedProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }
    
        Project selectedProject = assignedProjects.get(projChoice - 1);
    
        System.out.println("\nManaging Project: " + selectedProject.getProjectName() + " (" + selectedProject.getNeighborhood() + ")");
        System.out.println("\nCurrent Flat Units:");
        for (Map.Entry<String, Integer> entry : selectedProject.getFlatTypeUnits().entrySet()) {
            System.out.println("- " + entry.getKey() + ": " + entry.getValue() + " units");
        }
    
        System.out.println("\nSelect option:");
        System.out.println("1. Update Flat Remaining Count");
        System.out.println("2. Back to Dashboard");
        System.out.print("Enter your choice: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline
    
        if (choice == 1) {
            System.out.println("\nSelect flat type to update:");
            List<String> flatTypes = new ArrayList<>(selectedProject.getFlatTypeUnits().keySet());
            for (int i = 0; i < flatTypes.size(); i++) {
                System.out.println((i + 1) + ". " + flatTypes.get(i));
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
    
            boolean updated = officerController.updateFlatRemaining(selectedProject.getProjectId(), selectedFlatType, newCount);
            System.out.println(updated ? "Flat count updated successfully!" : "Failed to update flat count.");
        }
    }
    

    private void displayReplyToEnquiries(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         REPLY TO ENQUIRIES");
        System.out.println("==========================================");

    // 1) Grab every project this officer is handling
    List<Project> assignedProjects = officerController.getAllAssignedProjects(officer.getNric());
    if (assignedProjects == null || assignedProjects.isEmpty()) {
        System.out.println("You are not assigned to any project yet.");
        return;
    }
    // 2) For each project, fetch & display all its enquiries
    for (Project project : assignedProjects) {
        System.out.println("\nProject: " + project.getProjectName()
                           + " (ID: " + project.getProjectId() + ")");
        List<Enquiry> enquiries = enquiryController.viewEnquiriesByProject(project.getProjectId());
        if (enquiries == null || enquiries.isEmpty()) {
            System.out.println("  No enquiries for this project.");
            continue;
        }
        for (Enquiry e : enquiries) {
            User user = userController.viewUserDetails(e.getUserNRIC());
            System.out.println("  Enquiry ID: " + e.getEnquiryId());
            System.out.println("    From: " 
                + (user != null ? user.getName() : e.getUserNRIC())
                + " (" + e.getUserNRIC() + ")");
            System.out.println("    Date:  " + e.getEnquiryDate());
            System.out.println("    Content: " + e.getContent());
            System.out.println("    Reply:   " 
                + (e.getReply() == null ? "No reply yet" : e.getReply()));
            System.out.println();
        }
    }

    // 3) Let the officer reply to one of them
    System.out.print("Enter Enquiry ID to reply (or press Enter to go back): ");
    String enquiryId = scanner.nextLine().trim();
    if (enquiryId.isEmpty()) return;

    System.out.print("Enter your reply: ");
    String reply = scanner.nextLine();

    boolean success = enquiryController.replyToEnquiry(enquiryId, reply);
    if (success) {
        System.out.println("Reply sent successfully!");
    } else {
        System.out.println("Failed to send reply. Please check the Enquiry ID and try again.");
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

        // Step 1: Retrieve all assigned projects for the officer
        List<Project> assignedProjects = officerController.getAllAssignedProjects(officer.getNric());
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }

        // Step 2: Display the list of assigned projects
        System.out.println("You are in charge of the following projects:");
        for (int i = 0; i < assignedProjects.size(); i++) {
            Project project = assignedProjects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() + " (ID: " + project.getProjectId() + ")");
        }

        // Step 3: Allow the officer to select a project
        System.out.print("Select a project to manage flat selection (enter number): ");
        int projectChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (projectChoice < 1 || projectChoice > assignedProjects.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Project selectedProject = assignedProjects.get(projectChoice - 1);

        // Step 4: Retrieve successful applications for the selected project
        List<Application> applications = applicationController.getApplicationsByProject(selectedProject.getProjectId());
        List<Application> successfulApplications = applications.stream()
                .filter(a -> "Successful".equalsIgnoreCase(a.getStatus()))
                .collect(Collectors.toList());

        // Step 5: Check if there are any successful applications
        if (successfulApplications.isEmpty()) {
            System.out.println("No successful applications to process for flat selection in this project.");
            return;
        }

        // Step 6: Display the list of successful applications
        System.out.println("\nSuccessful Applications:");
        for (int i = 0; i < successfulApplications.size(); i++) {
            Application app = successfulApplications.get(i);
            Applicant applicant = officerController.retrieveApplicantByNRIC(app.getApplicantNRIC());

            System.out.println((i + 1) + ". Application ID: " + app.getApplicationId());
            System.out.println("   Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
            System.out.println("   Flat Type (if selected): "
                    + (app.getFlatType() != null ? app.getFlatType() : "Not selected yet"));
            System.out.println();
        }

        // Step 7: Allow the officer to select an application for flat selection
        System.out.print("Select an application for flat selection (enter number): ");
        int appChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (appChoice < 1 || appChoice > successfulApplications.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        // Step 8: Proceed with flat selection for the selected application
        Application selectedApp = successfulApplications.get(appChoice - 1);
        Applicant applicant = officerController.retrieveApplicantByNRIC(selectedApp.getApplicantNRIC());

        displayManageFlatSelectionForApplicant(officer, selectedApp, applicant);
    }


    // Helper method to handle flat selection for a specific applicant
    private void displayManageFlatSelectionForApplicant(HDBOfficer officer, Application application, Applicant applicant) {
        System.out.println("\n==========================================");
        System.out.println("         FLAT SELECTION");
        System.out.println("==========================================");
    
        // Retrieve all projects the officer is managing
        List<Project> assignedProjects = officerController.getAllAssignedProjects(officer.getNric());
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any projects.");
            return;
        }
    
        // Step 1: Verify that the application belongs to one of the officer's assigned projects
        Project matchingProject = assignedProjects.stream()
                .filter(project -> project.getProjectId().equals(application.getProjectId()))
                .findFirst()
                .orElse(null);
    
        if (matchingProject == null) {
            System.out.println("Error: The application does not belong to any project you are managing.");
            System.out.println("Application Project ID: " + application.getProjectId());
            System.out.println("Your Assigned Projects: ");
            assignedProjects.forEach(project -> System.out.println("- " + project.getProjectName() + " (ID: " + project.getProjectId() + ")"));
            return;
        }
    
        System.out.println("Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
        System.out.println("Project: " + matchingProject.getProjectName());
    
        // Step 2: Check if the application specifies a flat type
        String requestedFlatType = application.getFlatType();
        if (requestedFlatType == null || requestedFlatType.isEmpty()) {
            System.out.println("The applicant has not specified a flat type in their application.");
            return;
        }
    
        // Step 3: Check if the requested flat type is available in the project
        Integer remainingUnits = matchingProject.getFlatTypeUnits().get(requestedFlatType);
        if (remainingUnits == null || remainingUnits <= 0) {
            System.out.println("The requested flat type (" + requestedFlatType + ") is not available in this project.");
            return;
        }
    
        // Step 4: Confirm flat selection
        System.out.println("\nRequested Flat Type: " + requestedFlatType + " (" + remainingUnits + " units remaining)");
        System.out.print("Confirm flat selection for this applicant? (Y/N): ");
        String confirm = scanner.nextLine();
    
        if (confirm.equalsIgnoreCase("Y")) {
    
            // Step 6: Update applicant profile
            applicant.setBookedFlatType(requestedFlatType); // Use setBookedFlatType
            applicant.setBookedProjectId(matchingProject.getProjectId()); // Use setBookedProjectId
            boolean profileUpdated = officerController.updateApplicantProfile(applicant.getNric(),
                    matchingProject.getProjectId(), requestedFlatType);
  
           application.setStatus("BOOKED"); // Update application status to "Booked",

            // Step 8: Update flat count
            int currentCount = matchingProject.getFlatTypeUnits().get(requestedFlatType);
            boolean flatCountUpdated = officerController.updateFlatRemaining(matchingProject.getProjectId(), requestedFlatType,
                    currentCount - 1);
    
            if (profileUpdated && flatCountUpdated) {
                System.out.println("Flat selection completed successfully!");
                System.out.println("Application status updated to 'Booked'");
                System.out.println("Applicant profile updated with flat type: " + requestedFlatType);
                System.out.println("Flat count updated.");

            } else {
                System.out.println("Failed to complete flat selection. Please try again.");
            }
        } else {
            System.out.println("Flat selection cancelled.");
        }
    }

    private void displayGenerateBookingReceipt(HDBOfficer officer) {
        System.out.println("\n==========================================");
        System.out.println("         GENERATE BOOKING RECEIPT");
        System.out.println("==========================================");
    
        // Step 1: Retrieve all assigned projects for the officer
        List<Project> assignedProjects = officerController.getAllAssignedProjects(officer.getNric());
        if (assignedProjects == null || assignedProjects.isEmpty()) {
            System.out.println("You are not assigned to any project yet.");
            return;
        }
    
        // Step 2: Display assigned projects
        System.out.println("\nYour Assigned Projects:");
        for (Project project : assignedProjects) {
            System.out.println("- " + project.getProjectName() + " (ID: " + project.getProjectId() + ")");
        }
    
        // Step 3: Select a project
        System.out.print("\nEnter Project ID to generate receipt for: ");
        String projectId = scanner.nextLine().trim();
    
        // Step 4: Retrieve applications for the selected project
        List<Application> applications = applicationController.getApplicationsByProject(projectId);
        List<Application> bookedApplications = applications.stream()
                .filter(app -> app.getStatus().equalsIgnoreCase("Booked"))
                .collect(Collectors.toList());
    
        if (bookedApplications.isEmpty()) {
            System.out.println("No booked applications found for this project.");
            return;
        }
    
        // Step 5: Generate and display receipts
        System.out.println("\nBooking Receipts:");
        for (Application app : bookedApplications) {
            Applicant applicant = officerController.retrieveApplicantByNRIC(app.getApplicantNRIC());
            Project project = projectController.getProjectDetails(app.getProjectId());
    
            if (applicant != null && project != null) {
                System.out.println("\n==========================================");
                System.out.println("Receipt for Applicant: " + applicant.getName());
                System.out.println("==========================================");
                System.out.println("Applicant Name: " + applicant.getName());
                System.out.println("NRIC: " + applicant.getNric());
                System.out.println("Age: " + applicant.getAge());
                System.out.println("Marital Status: " + applicant.getMaritalStatus());
                System.out.println("Flat Type Booked: " + app.getFlatType());
                System.out.println("Project Name: " + project.getProjectName());
                System.out.println("Neighborhood: " + project.getNeighborhood());
                System.out.println("==========================================");
            } else {
                System.out.println("Error: Could not retrieve details for application ID: " + app.getApplicationId());
            }
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