package controllers;

import models.Project;
import models.Application;
import models.Applicant;
import java.util.ArrayList;
import java.util.List;

public class ApplicantController {
    private List<Application> applications = new ArrayList<>(); // Simulating a database
    private ApplicationController applicationController;

    public ApplicantController(ApplicationController applicationController) {
        this.applicationController = applicationController;
    }

    private boolean saveApplication(Application application) {
        return applicationController.submitApplication(application);
    }

    // View eligible projects for an applicant
    public List<Project> viewEligibleProjects(Applicant applicant) {
        List<Project> eligibleProjects = new ArrayList<>();
        ProjectController projectController = new ProjectController();
        List<Project> projects = projectController.viewAllProjects();

        System.out.println("Total projects available: " + projects.size());

        for (Project project : projects) {
            System.out.println("Checking eligibility for project: " + project.getProjectName());
            if (checkEligibility(applicant, project)) {
                System.out.println("Applicant is eligible for project: " + project.getProjectName());
                eligibleProjects.add(project);
            } else {
                System.out.println("Applicant is NOT eligible for project: " + project.getProjectName());
            }
        }

        return eligibleProjects;
    }

    // Apply for a project
    public boolean applyForProject(Applicant applicant, String projectId) {
        Application application = new Application();
        application.setApplicationId("APP" + System.currentTimeMillis());
        application.setApplicantNRIC(applicant.getNric());
        application.setProjectId(projectId);
        application.setApplicationDate(new java.util.Date());
        application.setStatus("Pending");

        // Save the application
        boolean saved = saveApplication(application);

        if (saved) {
            // Retrieve the applicant and update their current application
            applicant.setCurrentApplication(application.getApplicationId());
            System.out.println("Application submitted successfully for Applicant: " + applicant.getName());
            return true;

        }

        return false;
    }

    // View application status
    public Application viewApplicationStatus(Applicant applicant) {
        System.out.println("Checking application status for Applicant NRIC: " + applicant.getNric());
        for (Application application : applications) {
            System.out.println("Application ID: " + application.getApplicationId() +
                    ", NRIC: " + application.getApplicantNRIC() +
                    ", Status: " + application.getStatus());
            if (application.getApplicantNRIC().equalsIgnoreCase(applicant.getNric()) &&
                    !application.getStatus().equalsIgnoreCase("Withdrawn")) {
                System.out.println("Active application found: " + application.getApplicationId());
                return application;
            }
        }
        System.out.println("No active application found for Applicant NRIC: " + applicant.getNric());
        return null; // No active application found
    }

    // Request withdrawal of an application
    public boolean requestWithdrawal(Applicant applicant, String applicationId) {
        // Retrieve the current application of the applicant
        Application application = applicationController.getApplicationByNRIC(applicant.getNric());
    
        if (application != null && application.getApplicationId().equals(applicationId)) {
            application.setStatus("Withdrawn"); // Update the status to "Withdrawn"
            System.out.println("Application withdrawn: " + applicationId);
            return true;
        } else {
            System.out.println("No matching application found for withdrawal.");
            return false;
        }
    }

    // Check if an applicant is eligible for a project
    public boolean checkEligibility(Applicant applicant, Project project) {
        for (String flatType : project.getFlatTypeUnits().keySet()) {
            boolean ageEligible = validateAgeRequirement(applicant, flatType);
            boolean maritalStatusEligible = validateMaritalStatus(applicant, flatType);

            if (ageEligible && maritalStatusEligible) {
                System.out.println("Applicant is eligible for flat type: " + flatType);
                return true;
            } else {
                System.out.println("Applicant is NOT eligible for flat type: " + flatType);
            }
        }
        return false;
    }

    // Validate age requirement for a flat type
    public boolean validateAgeRequirement(Applicant applicant, String flatType) {
        int age = applicant.getAge();

        if (flatType.equals("2-Room") && age >= 35) {
            return true;
        } else if (!flatType.equals("2-Room") && age >= 21) {
            return true;
        }
        return false;
    }

    // Validate marital status requirement for a flat type
    public boolean validateMaritalStatus(Applicant applicant, String flatType) {
        String maritalStatus = applicant.getMaritalStatus();

        if (flatType.equals("2-Room") && maritalStatus.equals("Single")) {
            return true;
        } else if (!flatType.equals("2-Room") && maritalStatus.equals("Married")) {
            return true;
        }
        return false;
    }
}