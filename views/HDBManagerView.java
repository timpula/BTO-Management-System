package views;

import controllers.*;
import models.*;

import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;


public class HDBManagerView {
    private Scanner scanner;
    private HDBManagerController managerController;
    private UserController userController;
    private ProjectController projectController;
    private RegistrationController registrationController;
    private ApplicationController applicationController;
    private ReportController reportController;

    public HDBManagerView() {
        scanner = new Scanner(System.in);
        managerController = new HDBManagerController();
        userController = new UserController();
        projectController = new ProjectController();
        registrationController = new RegistrationController();
        applicationController = new ApplicationController();
        reportController = new ReportController();

    }

    public void displayDashboard(HDBManager manager) {

        System.out.println("\n==========================================");
        System.out.println("         HDB MANAGER DASHBOARD");
        System.out.println("==========================================");
        System.out.println("Welcome, " + manager.getName() + "!");
        System.out.println("User Type: " + manager.getUserType());
        System.out.println("NRIC: " + manager.getNric());
        System.out.println("Age: " + manager.getAge());
        System.out.println("Marital Status: " + manager.getMaritalStatus());

        int choice;
        do {
            System.out.println("\n1. Create New Project");
            System.out.println("2. Manage Existing Projects");
            System.out.println("3. Approve Officer Registrations");
            System.out.println("4. Approve Applications");
            System.out.println("5. Approve Withdrawal Requests");
            System.out.println("6. Generate Reports");
            System.out.println("7. Update Profile");
            System.out.println("8. View All Projects");
            System.out.println("9. Logout");
            System.out.print("Please select an option: ");

            choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    displayCreateProject(manager);
                    break;
                case 2:
                    displayManageProjects(manager);
                    break;
                case 3:
                    displayApproveOfficerRegistrations(manager);
                    break;
                case 4:
                    displayApproveApplications(manager);
                    break;
                case 5:
                    displayApproveWithdrawalRequests(manager);
                    break;
                case 6:
                    displayGenerateReport(manager);
                    break;
                case 7:
                    displayUpdateProfile(manager);
                    break;
                case 8:
                    displayAllProjects();
                    break;
                case 9:
                    System.out.println("Logging out...");
                    return;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        } while (choice != 9);
    }

    private void displayCreateProject(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("         CREATE NEW PROJECT");
        System.out.println("==========================================");
    
        Project newProject = new Project();
        newProject.setCreatorNRIC(manager.getNric());
    
        try {
            // Set project name
            System.out.print("Enter project name: ");
            String projectName = scanner.nextLine();
            if (projectName.isEmpty()) {
                throw new IllegalArgumentException("Project name cannot be empty.");
            }
            newProject.setProjectName(projectName);
    
            // Set neighborhood
            System.out.print("Enter neighborhood: ");
            String neighborhood = scanner.nextLine();
            if (neighborhood.isEmpty()) {
                throw new IllegalArgumentException("Neighborhood cannot be empty.");
            }
            newProject.setNeighborhood(neighborhood);
    
            // Set application opening date
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false); // Ensure strict date parsing
            Date openingDate = null;
            while (openingDate == null) {
                try {
                    System.out.print("Enter application opening date (dd/MM/yyyy): ");
                    String openingDateStr = scanner.nextLine();
                    openingDate = dateFormat.parse(openingDateStr);
    
                    // Check if the opening date is in the past
                    if (openingDate.before(new Date())) {
                        System.out.println("Opening date cannot be in the past. Please try again.");
                        openingDate = null; // Reset to re-prompt
                    } else {
                        newProject.setApplicationOpeningDate(openingDate);
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy.");
                }
            }
    
            // Set application closing date
            Date closingDate = null;
            while (closingDate == null) {
                try {
                    System.out.print("Enter application closing date (dd/MM/yyyy): ");
                    String closingDateStr = scanner.nextLine();
                    closingDate = dateFormat.parse(closingDateStr);
    
                    // Check if the closing date is before the opening date
                    if (closingDate.before(openingDate)) {
                        System.out.println("Closing date cannot be before opening date. Please try again.");
                        closingDate = null; // Reset to re-prompt
                    } else {
                        newProject.setApplicationClosingDate(closingDate);
                    }
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Please use dd/MM/yyyy.");
                }
            }
    
            // Set number of HDB officer slots
            System.out.print("Enter number of HDB officer slots: ");
            int officerSlots = scanner.nextInt();
            scanner.nextLine(); // Consume newline
            if (officerSlots < 1 || officerSlots > 10) {
                throw new IllegalArgumentException("Number of officer slots must be greater than zero.");
            }
            newProject.setTotalOfficerSlots(officerSlots);
            newProject.setAvailableOfficerSlots(officerSlots);
    
            // Set flat types and units
            Map<String, Integer> flatTypeUnits = new HashMap<>();
            boolean addMoreTypes = true;
    
            while (addMoreTypes) {
                System.out.print("Enter flat type (e.g., 2-room, 3-room): ");
                String flatType = scanner.nextLine();
                if (flatType.isEmpty()) {
                    throw new IllegalArgumentException("Flat type cannot be empty.");
                }
    
                System.out.print("Enter number of units available: ");
                int unitCount = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                if (unitCount <= 0) {
                    throw new IllegalArgumentException("Number of units must be greater than zero.");
                }
    
                flatTypeUnits.put(flatType, unitCount);
    
                System.out.print("Add another flat type? (Y/N): ");
                String addMore = scanner.nextLine();
                addMoreTypes = addMore.equalsIgnoreCase("Y");
            }
            newProject.setFlatTypeUnits(flatTypeUnits);
    
            // Set project visibility (default to false)
            newProject.setVisibility(false);
    
            // Call the createProject function from ProjectController
            boolean success = projectController.createProject(newProject);
    
            if (success) {
                System.out.println("Project created successfully!");
            } else {
                System.out.println("Failed to create project. Please try again later.");
            }
    
        } catch (IllegalArgumentException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("An unexpected error occurred: " + e.getMessage());
        }
    }

    private void displayManageProjects(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("         MANAGE PROJECTS");
        System.out.println("==========================================");

        // Use the IFilter method filterProjects instead of undefined filterProjectsByCreator
        //projectController.viewAllProjects

        //List<Project> projects = managerController.filterProjects(manager.getNric());

        List<Project> all = projectController.viewAllProjects();
        List<Project> projects = all.stream()
            .filter(p -> p.getCreatorNRIC().equals(manager.getNric()))
            .collect(Collectors.toList());


        if (projects.isEmpty()) {
            System.out.println("No projects found.");
            return;
        }

        System.out.println("Your Projects:");
        for (int i = 0; i < projects.size(); i++) {
            Project project = projects.get(i);
            System.out.println((i + 1) + ". " + project.getProjectName() +
                    " (" + project.getNeighborhood() + ") - " +
                    (project.isVisible() ? "Visible" : "Hidden"));
        }

        System.out.print("Select a project to manage (enter number): ");
        int projectChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (projectChoice < 1 || projectChoice > projects.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Project selectedProject = projects.get(projectChoice - 1);

        System.out.println("\nProject Details:");
        System.out.println("ID: " + selectedProject.getProjectId());
        System.out.println("Name: " + selectedProject.getProjectName());
        System.out.println("Neighborhood: " + selectedProject.getNeighborhood());
        System.out.println("Application Period: " + selectedProject.getApplicationOpeningDate() +
                " to " + selectedProject.getApplicationClosingDate());
        System.out.println("Visibility: " + (selectedProject.isVisible() ? "Visible" : "Hidden"));
        System.out.println("Officer Slots: " + selectedProject.getAvailableOfficerSlots() +
                "/" + selectedProject.getTotalOfficerSlots());

        System.out.println("\nFlat Types:");
        for (String flatType : selectedProject.getFlatTypeUnits().keySet()) {
            System.out.println("- " + flatType + ": " + selectedProject.getFlatTypeUnits().get(flatType) + " units");
        }

        System.out.println("\nSelect action:");
        System.out.println("1. Edit Project");
        System.out.println("2. Toggle Visibility");
        System.out.println("3. Delete Project");
        System.out.println("4. Back to Dashboard");
        System.out.print("Enter your choice: ");

        int actionChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (actionChoice) {
            case 1:
                displayEditProject(selectedProject);
                break;
            case 2:
                boolean newVisibility = !selectedProject.isVisible();
                boolean toggled = projectController.toggleProjectVisibility(
                        selectedProject.getProjectId(), newVisibility);

                if (toggled) {
                    System.out.println("Project visibility changed to: " +
                            (newVisibility ? "Visible" : "Hidden"));
                } else {
                    System.out.println("Failed to toggle visibility.");
                }
                break;
            case 3:
                System.out.print("Are you sure you want to delete this project? (Y/N): ");
                String confirm = scanner.nextLine();

                if (confirm.equalsIgnoreCase("Y")) {
                    boolean deleted = projectController.deleteProject(selectedProject.getProjectId());

                    if (deleted) {
                        System.out.println("Project deleted successfully!");
                    } else {
                        System.out.println("Failed to delete project.");
                    }
                } else {
                    System.out.println("Delete operation cancelled.");
                }
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    private void displayEditProject(Project project) {
        System.out.println("\n==========================================");
        System.out.println("         EDIT PROJECT");
        System.out.println("==========================================");

        System.out.println("Current Project Details:");
        System.out.println("1. Name: " + project.getProjectName());
        System.out.println("2. Neighborhood: " + project.getNeighborhood());
        System.out.println("3. Application Opening Date: " + project.getApplicationOpeningDate());
        System.out.println("4. Application Closing Date: " + project.getApplicationClosingDate());
        System.out.println("5. Officer Slots: " + project.getTotalOfficerSlots());
        System.out.println("6. Flat Types");
        System.out.println("7. Go Back");

        System.out.print("Select field to edit: ");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter new project name: ");
                String newName = scanner.nextLine();
                project.setProjectName(newName);
                break;
            case 2:
                System.out.print("Enter new neighborhood: ");
                String newNeighborhood = scanner.nextLine();
                project.setNeighborhood(newNeighborhood);
                break;
            case 3:
                try {
                    System.out.print("Enter new application opening date (dd/MM/yyyy): ");
                    String openingDateStr = scanner.nextLine();
                    Date openingDate = new SimpleDateFormat("dd/MM/yyyy").parse(openingDateStr);
                    project.setApplicationOpeningDate(openingDate);
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Edit cancelled.");
                    return;
                }
                break;
            case 4:
                try {
                    System.out.print("Enter new application closing date (dd/MM/yyyy): ");
                    String closingDateStr = scanner.nextLine();
                    Date closingDate = new SimpleDateFormat("dd/MM/yyyy").parse(closingDateStr);
                    project.setApplicationClosingDate(closingDate);
                } catch (ParseException e) {
                    System.out.println("Invalid date format. Edit cancelled.");
                    return;
                }
                break;
            case 5:
                System.out.print("Enter new number of HDB officer slots: ");
                int newSlots = scanner.nextInt();
                scanner.nextLine(); // Consume newline
                project.setTotalOfficerSlots(newSlots);

                if (newSlots < project.getTotalOfficerSlots() - project.getAvailableOfficerSlots()) {
                    System.out.println("Warning: New slot count is less than assigned officers.");
                } else {
                    project.setAvailableOfficerSlots(
                            newSlots - (project.getTotalOfficerSlots() - project.getAvailableOfficerSlots()));
                }
                break;
            case 6:
                editFlatTypes(project);
                break;
            case 7:
                return;
            default:
                System.out.println("Invalid option.");
                return;
        }

        if (choice != 6 && choice != 7) {
            boolean success = projectController.editProject(project.getProjectId(), project);

            if (success) {
                System.out.println("Project updated successfully!");
            } else {
                System.out.println("Failed to update project.");
            }
        }
    }

    private void editFlatTypes(Project project) {
        System.out.println("\nCurrent Flat Types:");
        int i = 1;
        for (String flatType : project.getFlatTypeUnits().keySet()) {
            System.out.println(i + ". " + flatType + ": " + project.getFlatTypeUnits().get(flatType) + " units");
            i++;
        }

        System.out.println("\nSelect action:");
        System.out.println("1. Add new flat type");
        System.out.println("2. Edit existing flat type");
        System.out.println("3. Remove flat type");
        System.out.println("4. Go back");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter new flat type: ");
                String newType = scanner.nextLine();

                System.out.print("Enter number of units: ");
                int units = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                project.getFlatTypeUnits().put(newType, units);
                break;
            case 2:
                System.out.print("Enter number of flat type to edit: ");
                int typeChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (typeChoice < 1 || typeChoice > project.getFlatTypeUnits().size()) {
                    System.out.println("Invalid selection.");
                    return;
                }

                String[] flatTypes = project.getFlatTypeUnits().keySet().toArray(new String[0]);
                String selectedType = flatTypes[typeChoice - 1];

                System.out.print("Enter new number of units for " + selectedType + ": ");
                int newUnits = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                project.getFlatTypeUnits().put(selectedType, newUnits);
                break;
            case 3:
                System.out.print("Enter number of flat type to remove: ");
                int removeChoice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                if (removeChoice < 1 || removeChoice > project.getFlatTypeUnits().size()) {
                    System.out.println("Invalid selection.");
                    return;
                }

                String[] types = project.getFlatTypeUnits().keySet().toArray(new String[0]);
                String typeToRemove = types[removeChoice - 1];

                project.getFlatTypeUnits().remove(typeToRemove);
                break;
            case 4:
                return;
            default:
                System.out.println("Invalid option.");
                return;
        }

        boolean success = projectController.editProject(project.getProjectId(), project);

        if (success) {
            System.out.println("Flat types updated successfully!");
        } else {
            System.out.println("Failed to update flat types.");
        }
    }

    private void displayApproveOfficerRegistrations(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("     APPROVE OFFICER REGISTRATIONS");
        System.out.println("==========================================");

    // 1) Pending registrations
    List<Registration> pending = managerController.viewOfficerRegistrationsByStatus("Pending");
    System.out.println("Pending Officer Registrations:");
    if (pending.isEmpty()) {
        System.out.println("  (none)");
    } else {
        for (int i = 0; i < pending.size(); i++) {
            Registration reg = pending.get(i);
            User officer = userController.viewUserDetails(reg.getOfficerNRIC());
            Project project = projectController.getProjectDetails(reg.getProjectId());
            System.out.printf("  %d) %s | %s | %s%n",
                              i + 1,
                              reg.getRegistrationId(),
                              officer.getName(),
                              project.getProjectName());
        }
    }

    // 2) Approved registrations
    List<Registration> approved = managerController.viewOfficerRegistrationsByStatus("Approved");
    System.out.println("\nApproved Officer Registrations:");
    if (approved.isEmpty()) {
        System.out.println("  (none)");
    } else {
        for (Registration reg : approved) {
            User officer = userController.viewUserDetails(reg.getOfficerNRIC());
            Project project = projectController.getProjectDetails(reg.getProjectId());
            System.out.printf("  %s | %s | %s%n",
                              reg.getRegistrationId(),
                              officer.getName(),
                              project.getProjectName());
        }
    }

    // 3) Now let the manager process any pending registration
    if (!pending.isEmpty()) {
        System.out.print("\nSelect a PENDING registration to process (enter number), or 0 to go back: ");
        int choice = scanner.nextInt();
        scanner.nextLine();
        if (choice > 0 && choice <= pending.size()) {
            Registration sel = pending.get(choice - 1);
            User officer = userController.viewUserDetails(sel.getOfficerNRIC());
            Project project = projectController.getProjectDetails(sel.getProjectId());

            System.out.println("\nRegistration Details:");
            System.out.println("Officer: " + officer.getName() + " (NRIC: " + officer.getNric() + ")");
            System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
            System.out.println("Registration Date: " + sel.getRegistrationDate());

            System.out.println("\nSelect action:");
            System.out.println("1. Approve Registration");
            System.out.println("2. Reject Registration");
            System.out.println("3. Back to Dashboard");
            System.out.print("Enter your choice: ");

            int actionChoice = scanner.nextInt();
            scanner.nextLine();
            switch (actionChoice) {
                case 1:
                    if (managerController.approveOfficerRegistration(sel.getRegistrationId())) {
                        System.out.println("Registration approved successfully!");
                    } else {
                        System.out.println("Failed to approve registration.");
                    }
                    break;
                case 2:
                    if (managerController.rejectOfficerRegistration(sel.getRegistrationId())) {
                        System.out.println("Registration rejected successfully!");
                    } else {
                        System.out.println("Failed to reject registration.");
                    }
                    break;
                default:
                    // back to dashboard
            }
        }
    }
}

    private void displayApproveApplications(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("         APPROVE APPLICATIONS");
        System.out.println("==========================================");

        List<Application> pendingApplications = applicationController.getApplicationsByStatus("Pending");

        if (pendingApplications.isEmpty()) {
            System.out.println("No pending applications found.");
            return;
        }

        System.out.println("Pending Applications:");
        for (int i = 0; i < pendingApplications.size(); i++) {
            Application app = pendingApplications.get(i);
            User applicant = userController.viewUserDetails(app.getApplicantNRIC());
            Project project = projectController.getProjectDetails(app.getProjectId());

            System.out.println((i + 1) + ". Application ID: " + app.getApplicationId());
            System.out.println("   Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
            System.out.println("   Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
            System.out.println("   Application Date: " + app.getApplicationDate());
            System.out.println();
        }

        System.out.print("Select an application to process (enter number): ");
        int appChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (appChoice < 1 || appChoice > pendingApplications.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Application selectedApp = pendingApplications.get(appChoice - 1);
        User applicant = userController.viewUserDetails(selectedApp.getApplicantNRIC());
        Project project = projectController.getProjectDetails(selectedApp.getProjectId());

        System.out.println("\nApplication Details:");
        System.out.println("Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
        System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("Application Date: " + selectedApp.getApplicationDate());

        System.out.println("\nSelect action:");
        System.out.println("1. Approve Application");
        System.out.println("2. Reject Application");
        System.out.println("3. Back to Dashboard");
        System.out.print("Enter your choice: ");

        int actionChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (actionChoice) {
            case 1:
                boolean approved = managerController.approveApplication(selectedApp.getApplicationId());
                if (approved) {
                    System.out.println("Application approved successfully!");
                } else {
                    System.out.println("Failed to approve application.");
                }
                break;
            case 2:
                boolean rejected = managerController.rejectApplication(selectedApp.getApplicationId());
                if (rejected) {
                    System.out.println("Application rejected successfully!");
                } else {
                    System.out.println("Failed to reject application.");
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    private void displayApproveWithdrawalRequests(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("     APPROVE WITHDRAWAL REQUESTS");
        System.out.println("==========================================");

        // Note: In a real implementation, you would have a method to get withdrawal
        // requests
        // For now, we'll simulate it with applications that have status "Withdrawal
        // Requested"
        List<Application> withdrawalRequests = applicationController.getApplicationsByStatus("Withdrawal Requested");

        if (withdrawalRequests.isEmpty()) {
            System.out.println("No pending withdrawal requests found.");
            return;
        }

        System.out.println("Pending Withdrawal Requests:");
        for (int i = 0; i < withdrawalRequests.size(); i++) {
            Application app = withdrawalRequests.get(i);
            User applicant = userController.viewUserDetails(app.getApplicantNRIC());
            Project project = projectController.getProjectDetails(app.getProjectId());

            System.out.println((i + 1) + ". Application ID: " + app.getApplicationId());
            System.out.println("   Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
            System.out.println("   Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
            System.out.println("   Application Date: " + app.getApplicationDate());
            System.out.println();
        }

        System.out.print("Select a withdrawal request to process (enter number): ");
        int reqChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        if (reqChoice < 1 || reqChoice > withdrawalRequests.size()) {
            System.out.println("Invalid selection.");
            return;
        }

        Application selectedReq = withdrawalRequests.get(reqChoice - 1);
        User applicant = userController.viewUserDetails(selectedReq.getApplicantNRIC());
        Project project = projectController.getProjectDetails(selectedReq.getProjectId());

        System.out.println("\nWithdrawal Request Details:");
        System.out.println("Applicant: " + applicant.getName() + " (NRIC: " + applicant.getNric() + ")");
        System.out.println("Project: " + project.getProjectName() + " (" + project.getNeighborhood() + ")");
        System.out.println("Application Date: " + selectedReq.getApplicationDate());

        System.out.println("\nSelect action:");
        System.out.println("1. Approve Withdrawal Request");
        System.out.println("2. Reject Withdrawal Request");
        System.out.println("3. Back to Dashboard");
        System.out.print("Enter your choice: ");

        int actionChoice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (actionChoice) {
            case 1:
                // In a real implementation, you would have specific withdrawal request IDs
                // For now, we'll use the application ID
                boolean approved = managerController.approveWithdrawalRequest(selectedReq.getApplicationId());
                if (approved) {
                    System.out.println("Withdrawal request approved successfully!");
                } else {
                    System.out.println("Failed to approve withdrawal request.");
                }
                break;
            case 2:
                boolean rejected = managerController.rejectWithdrawalRequest(selectedReq.getApplicationId());
                if (rejected) {
                    System.out.println("Withdrawal request rejected successfully!");
                } else {
                    System.out.println("Failed to reject withdrawal request.");
                }
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
                break;
        }
    }

    private void displayGenerateReport(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("         GENERATE REPORT");
        System.out.println("==========================================");
    
        System.out.print("Enter Project ID (or leave blank for all projects): ");
        String projectId = scanner.nextLine();
        projectId = projectId.isEmpty() ? null : projectId;
    
        System.out.print("Enter Flat Type (e.g., 2-Room, 3-Room, or leave blank): ");
        String flatType = scanner.nextLine();
        flatType = flatType.isEmpty() ? null : flatType;
    
        System.out.print("Enter Marital Status (e.g., Single, Married, or leave blank): ");
        String maritalStatus = scanner.nextLine();
        maritalStatus = maritalStatus.isEmpty() ? null : maritalStatus;
    
        System.out.print("Enter Minimum Age (or leave blank): ");
        String minAgeInput = scanner.nextLine();
        int minAge = minAgeInput.isEmpty() ? 0 : Integer.parseInt(minAgeInput);
    
        System.out.print("Enter Maximum Age (or leave blank): ");
        String maxAgeInput = scanner.nextLine();
        int maxAge = maxAgeInput.isEmpty() ? Integer.MAX_VALUE : Integer.parseInt(maxAgeInput);
    
        Report report = reportController.generateApplicantReport(projectId, flatType, maritalStatus, minAge, maxAge);

        displayReportDetails(report, "Applicant");

        System.out.println("\nGenerated Report:");
        System.out.println("Report ID: " + report.getReportId());
        System.out.println("Report Type: " + report.getReportType());
        System.out.println("Generation Date: " + report.getGenerationDate());
        System.out.println("Statistics: " + report.getStatistics());
        System.out.println("Applications:");
        for (Application application : report.getApplications()) {
            System.out.println("- Applicant NRIC: " + application.getApplicantNRIC());
            System.out.println("  Project ID: " + application.getProjectId());
            System.out.println("  Flat Type: " + application.getFlatType());
            System.out.println("  Status: " + application.getStatus());
        }
    }
/* 
    private void displayReportFilters(Report report, String reportType) {
        boolean exitFilters = false;

        while (!exitFilters) {
            System.out.println("\nCurrent Report: " + reportType + " Report");
            System.out.println("Total Records: " + report.getTotalRecords());

            System.out.println("\nApply filters:");
            System.out.println("1. Filter by Marital Status");
            System.out.println("2. Filter by Flat Type");
            System.out.println("3. Filter by Age Range");
            System.out.println("4. Filter by Project");
            System.out.println("5. View Report");
            System.out.println("6. Back to Dashboard");
            System.out.print("Enter your choice: ");

            int filterChoice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (filterChoice) {
                case 1:
                    System.out.println("\nSelect marital status:");
                    System.out.println("1. Single");
                    System.out.println("2. Married");
                    System.out.print("Enter your choice: ");

                    int statusChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    String status = statusChoice == 1 ? "Single" : "Married";
                    report = reportController.filterReportByMaritalStatus(status);
                    break;
                case 2:
                    System.out.print("Enter flat type (e.g., 2-room, 3-room): ");
                    String flatType = scanner.nextLine();
                    report = reportController.filterReportByFlatType(flatType);
                    break;
                case 3:
                    System.out.print("Enter minimum age: ");
                    int minAge = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    System.out.print("Enter maximum age: ");
                    int maxAge = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    report = reportController.filterReportByAge(minAge, maxAge);
                    break;
                case 4:
                    List<Project> projects = projectController.viewAllProjects();

                    if (projects.isEmpty()) {
                        System.out.println("No projects found.");
                        continue;
                    }

                    System.out.println("\nSelect project:");
                    for (int i = 0; i < projects.size(); i++) {
                        System.out.println((i + 1) + ". " + projects.get(i).getProjectName());
                    }
                    System.out.print("Enter your choice: ");

                    int projectChoice = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (projectChoice < 1 || projectChoice > projects.size()) {
                        System.out.println("Invalid selection.");
                        continue;
                    }

                    Project selectedProject = projects.get(projectChoice - 1);
                    report = reportController.filterReportByProject(selectedProject.getProjectId());
                    break;
                case 5:
                    displayReportDetails(report, reportType);
                    break;
                case 6:
                    exitFilters = true;
                    break;
                default:
                    System.out.println("Invalid option.");
                    break;
            }
        }
    }

    private void displayReportDetails(Report report, String reportType) {
        System.out.println("\n==========================================");
        System.out.println("         " + reportType.toUpperCase() + " REPORT");
        System.out.println("==========================================");

        System.out.println("Total Records: " + report.getTotalRecords());
        System.out.println("Applied Filters: " + report.getAppliedFilters());

        // Display report data - in a real implementation, this would show the actual
        // data
        System.out.println("\nReport Data:");
        if (reportType.equals("Applicant")) {
            System.out.println("Name\tNRIC\tAge\tMarital Status\tIncome\tProject");
            // Sample data for illustration
            System.out.println("John Doe\tS1234567A\t30\tMarried\t$5000\tProject A");
            System.out.println("Jane Smith\tS7654321B\t28\tSingle\t$4500\tProject B");
        } else if (reportType.equals("Booking")) {
            System.out.println("Name\tNRIC\tProject\tFlat Type\tBooking Date");
            // Sample data for illustration
            System.out.println("John Doe\tS1234567A\tProject A\t4-room\t01/04/2025");
            System.out.println("Jane Smith\tS7654321B\tProject B\t3-room\t15/03/2025");
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    } */

    private void displayReportDetails(Report report, String reportType) {
        System.out.println("\n==========================================");
        System.out.println("         " + reportType.toUpperCase() + " REPORT");
        System.out.println("==========================================");

        System.out.println("Total Records: " + report.getTotalRecords());
        System.out.println(report.getAppliedFilters());

        System.out.println("\nReport Data:");
        for (Application app : report.getApplications()) {
            System.out.println("- Applicant NRIC: " + app.getApplicantNRIC());
            System.out.println("  Project ID: " + app.getProjectId());
            System.out.println("  Flat Type: " + app.getFlatType());
            System.out.println("  Status: " + app.getStatus());
        }

        System.out.println("\nPress Enter to continue...");
        scanner.nextLine();
    }

    private void displayUpdateProfile(HDBManager manager) {
        System.out.println("\n==========================================");
        System.out.println("         UPDATE PROFILE");
        System.out.println("==========================================");

        System.out.println("Current Profile:");
        System.out.println("Name: " + manager.getName());

        System.out.println("\nWhat would you like to update?");
        System.out.println("1. Name");
        System.out.println("2. Password");
        System.out.println("3. Back to Dashboard");
        System.out.print("Enter your choice: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline

        switch (choice) {
            case 1:
                System.out.print("Enter new name: ");
                String newName = scanner.nextLine();
                manager.setName(newName);
                break;
            case 2:
                System.out.print("Enter new password: ");
                String newPassword = scanner.nextLine();
                manager.setPassword(newPassword);
                break;
            case 3:
                return;
            default:
                System.out.println("Invalid option.");
                return;
        }
    }

    private void displayAllProjects() {
        System.out.println("\n=== ALL PROJECTS ===");
        List<Project> all = projectController.viewAllProjects();
        for (Project p : all) {
            System.out.printf("%s | %s | %s to %s | Slots %d/%d | %s\n",
                p.getProjectId(),
                p.getProjectName(),
                new SimpleDateFormat("dd/MM/yyyy").format(p.getApplicationOpeningDate()),
                new SimpleDateFormat("dd/MM/yyyy").format(p.getApplicationClosingDate()),
                p.getAvailableOfficerSlots(),
                p.getTotalOfficerSlots(),
                p.isVisible() ? "Visible" : "Hidden");
        }
        return;
    }
    
}
