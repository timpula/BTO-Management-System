package controllers;

import models.Application;
import java.util.ArrayList;
import java.util.List;

public class ApplicationController {
    private static List<Application> applications = new ArrayList<>(); // Simulating a database

    // Submit an application
    public boolean submitApplication(Application application) {
        if (application != null) {
            applications.add(application);
            return true;
        }
        return false;
    }

    // Update application status
    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    // Get application by NRIC
    public Application getApplicationByNRIC(String applicantNRIC) {

        for (Application application : applications) {

            if (application.getApplicantNRIC().equals(applicantNRIC) && !application.getStatus().equals("Withdrawn")) {
                return application;
            }
        }
        System.out.println("No active application found for NRIC: " + applicantNRIC);
        return null; // No active application found
    }

    // Validate application eligibility
    public boolean validateApplicationEligibility(Application application) {
        // Example logic: Check if the application meets certain criteria
        return application.getStatus().equals("Pending");
    }

    // Get applications by project ID
    public List<Application> getApplicationsByProject(String projectId) {
        List<Application> projectApplications = new ArrayList<>();
        for (Application application : applications) {
            if (application.getProjectId().equals(projectId)) {
                projectApplications.add(application);
            }
        }
        return projectApplications;
    }
    public List<Application> getApplicationsByApplicant(String appNRIC) {
        List<Application> applicantApplications = new ArrayList<>();
        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(appNRIC)) {
                applicantApplications.add(application);
            }
        }
        return applicantApplications;
    }

    // Get applications by status
    public List<Application> getApplicationsByStatus(String status) {
        List<Application> statusApplications = new ArrayList<>();
        for (Application application : applications) {
            if (application.getStatus().equals(status)) {
                statusApplications.add(application);
            }
        }
        return statusApplications;
    }

    // Process withdrawal of an application
    public boolean processWithdrawal(String applicationId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setStatus("Withdrawn");
                return true;
            }
        }
        return false;
    }
}