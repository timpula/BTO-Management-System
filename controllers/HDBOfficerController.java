package controllers;

import models.HDBOfficer;
import models.Project;
import models.Application;
import models.Applicant;
import models.Receipt;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HDBOfficerController {

    private static List<HDBOfficer> officers = new ArrayList<>(); // Simulating a database of officers
    private static List<Project> projects = new ArrayList<>(); // Simulating a database of projects
    private static List<Application> applications = new ArrayList<>(); // Simulating a database of applications

    // Register officer for a project
    public boolean registerForProject(String officerNRIC, String projectId) {
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC)) {
                if (officer.getAssignedProjectId() != null) {
                    System.out.println("Officer is already assigned to a project.");
                    return false;
                }
                officer.setAssignedProjectId(projectId);
                officer.setRegistrationStatus("Pending");
                return true;
            }
        }
        return false; // Officer not found
    }

    // View registration status
    public String viewRegistrationStatus(String officerNRIC, String projectId) {
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC) && projectId.equals(officer.getAssignedProjectId())) {
                return officer.getRegistrationStatus();
            }
        }
        return "Not Registered"; // Officer not registered for the project
    }

    // View assigned project
    public Project viewAssignedProject(String officerNRIC) {
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC) && "Approved".equals(officer.getRegistrationStatus())) {
                for (Project project : projects) {
                    if (project.getProjectId().equals(officer.getAssignedProjectId())) {
                        return project;
                    }
                }
            }
        }
        return null; // No assigned project found
    }

    // Retrieve applicant by NRIC
    public Applicant retrieveApplicantByNRIC(String applicantNRIC) {
        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(applicantNRIC)) {
                return new Applicant(application.getApplicantNRIC(), "Applicant Name", "password", 30, "Single");
            }
        }
        return null; // Applicant not found
    }

    // Update remaining flats for a project and flat type
    public boolean updateFlatRemaining(String projectId, String flatType, int newCount) {
        for (Project project : projects) {
            if (project.getProjectId().equals(projectId)) {
                project.getFlatTypeUnits().put(flatType, newCount);
                return true;
            }
        }
        return false; // Project not found
    }

    // Update application status
    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setStatus(newStatus);
                return true;
            }
        }
        return false; // Application not found
    }

    // Generate booking receipt
    public Receipt generateBookingReceipt(String applicationId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId) && "Successful".equals(application.getStatus())) {
                Receipt receipt = new Receipt();
                receipt.setReceiptId("REC" + System.currentTimeMillis());
                receipt.setApplicantNRIC(application.getApplicantNRIC());
                receipt.setApplicantName("Applicant Name"); // Retrieve from applicant details
                receipt.setProjectName("Project Name"); // Retrieve from project details
                receipt.setFlatType(application.getFlatType());
                receipt.setBookingDate(new Date());
                return receipt;
            }
        }
        return null; // Receipt cannot be generated
    }
}