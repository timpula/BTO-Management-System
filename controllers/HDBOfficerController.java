package controllers;

import models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HDBOfficerController {

    // Simulating local databases (users, applications, etc.)
    private static List<HDBOfficer> officers = new ArrayList<>();
    private static List<Application> applications = new ArrayList<>();
    private static List<Applicant> applicants = new ArrayList<>();
    private static List<Receipt> receipts = new ArrayList<>();

    /**
     * Allows an HDB Officer to register for a project if eligibility conditions are met.
     */
    public boolean registerForProject(String officerNRIC, String projectId) {
        for (Application app : applications) {
            if (app.getApplicantNRIC().equals(officerNRIC) && app.getProjectId().equals(projectId)) {
                return false;
            }
        }

        Date now = new Date();
        ProjectController projectController = new ProjectController();
        Project project = projectController.getProjectDetails(projectId);
        if (project == null ||
            now.before(project.getApplicationOpeningDate()) ||
            now.after(project.getApplicationClosingDate())) {
            return false;
        }

        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC)) {
                if (officer.getAssignedProjectId() != null) return false;
                officer.setAssignedProjectId(projectId);
                officer.setRegistrationStatus("Pending");
                return true;
            }
        }

        return false;
    }

    /**
     * View the status of officer registration for a project
     */
    public String viewRegistrationStatus(String officerNRIC, String projectId) {
        UserController userController = new UserController();
        User user = userController.viewUserDetails(officerNRIC);
    
        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
    
            System.out.println("DEBUG: Officer NRIC = " + officer.getNric());
            System.out.println("DEBUG: Assigned Project ID = " + officer.getAssignedProjectId());
            System.out.println("DEBUG: Registration Status = " + officer.getRegistrationStatus());
            System.out.println("DEBUG: Input Project ID = " + projectId);
    
            if (projectId.equals(officer.getAssignedProjectId())) {
                return officer.getRegistrationStatus();
            }
        }
    
        return "Not Registered";
    }
    

    /**
     * ✅ Retrieves the assigned project if the officer is approved
     * ✅ Uses ProjectController instead of local list
     */
    public Project viewAssignedProject(String officerNRIC) {
        UserController userController = new UserController();
        User user = userController.viewUserDetails(officerNRIC);
    
        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
    
            System.out.println("DEBUG: Officer NRIC = " + officerNRIC);
            System.out.println("DEBUG: Assigned Project ID = " + officer.getAssignedProjectId());
            System.out.println("DEBUG: Registration Status = " + officer.getRegistrationStatus());
    
            if (officer.getAssignedProjectId() != null && "Approved".equalsIgnoreCase(officer.getRegistrationStatus())) {
                ProjectController projectController = new ProjectController();
                String projectId = officer.getAssignedProjectId();
                System.out.println("DEBUG: Fetching project with ID = " + projectId);
                return projectController.getProjectDetails(projectId);
            }
        }
    
        System.out.println("DEBUG: Officer not found or not approved.");
        return null;
    }
    
       

    /**
     * Retrieves an applicant by NRIC or creates a dummy if found in applications.
     */
    public Applicant retrieveApplicantByNRIC(String applicantNRIC) {
        for (Applicant applicant : applicants) {
            if (applicant.getNric().equals(applicantNRIC)) {
                return applicant;
            }
        }

        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(applicantNRIC)) {
                Applicant newApplicant = new Applicant(application.getApplicantNRIC(), "Applicant Name", "password", 30, "Single");
                applicants.add(newApplicant);
                return newApplicant;
            }
        }

        return null;
    }

    /**
     * Updates the remaining flat units for a project.
     */
    public boolean updateFlatRemaining(String projectId, String flatType, int newCount) {
        ProjectController projectController = new ProjectController();
        Project project = projectController.getProjectDetails(projectId);
        if (project != null) {
            project.getFlatTypeUnits().put(flatType, newCount);
            return true;
        }
        return false;
    }

    /**
     * Updates the status of an application.
     */
    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    /**
     * Generates a booking receipt for a successful or booked application.
     */
    public Receipt generateBookingReceipt(String applicationId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId) &&
                (application.getStatus().equals("Successful") || application.getStatus().equals("Booked"))) {

                Applicant applicant = retrieveApplicantByNRIC(application.getApplicantNRIC());
                if (applicant == null) return null;

                ProjectController projectController = new ProjectController();
                Project project = projectController.getProjectDetails(application.getProjectId());
                if (project == null) return null;

                Receipt receipt = new Receipt();
                receipt.setReceiptId("REC" + System.currentTimeMillis());
                receipt.setApplicationId(application.getApplicationId());
                receipt.setApplicantNRIC(applicant.getNric());
                receipt.setApplicantName(applicant.getName());
                receipt.setApplicantAge(applicant.getAge());
                receipt.setMaritalStatus(applicant.getMaritalStatus());
                receipt.setProjectName(project.getProjectName());
                receipt.setFlatType(application.getFlatType());
                receipt.setBookingDate(new Date());

                receipts.add(receipt);
                return receipt;
            }
        }
        return null;
    }

    /**
     * Updates the selected flat type for an application.
     */
    public boolean updateFlatSelection(String applicationId, String flatType) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setFlatType(flatType);
                return true;
            }
        }
        return false;
    }

    /**
     * Updates the applicant profile with selected project and flat type.
     */
    public boolean updateApplicantProfile(String applicantNRIC, String projectId, String flatType) {
        Applicant applicant = retrieveApplicantByNRIC(applicantNRIC);
        if (applicant == null) return false;

        applicant.setBookedFlatType(flatType);
        applicant.setBookedProjectId(projectId);
        return true;
    }

    /**
     * Checks if an applicant has a given application status for a specific project.
     */
    public boolean checkApplicantApplicationStatus(String applicantNRIC, String projectId, String status) {
        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(applicantNRIC) &&
                application.getProjectId().equals(projectId) &&
                application.getStatus().equals(status)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Retrieves a booking receipt by application ID.
     */
    public Receipt getReceiptByApplicationId(String applicationId) {
        for (Receipt receipt : receipts) {
            if (receipt.getApplicationId().equals(applicationId)) {
                return receipt;
            }
        }
        return null;
    }

    // For testing purposes
    public void addDummyOfficer(HDBOfficer officer) {
        officers.add(officer);
    }

    public void addDummyApplication(Application application) {
        applications.add(application);
    }

    public void addDummyApplicant(Applicant applicant) {
        applicants.add(applicant);
    }
}
