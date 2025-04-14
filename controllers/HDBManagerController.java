package controllers;

import models.Registration;
import models.Application;
import models.Project;
import java.util.ArrayList;
import java.util.List;

public class HDBManagerController {

    private static List<Registration> registrations = new ArrayList<>(); // Simulating a database of registrations
    private static List<Application> applications = new ArrayList<>(); // Simulating a database of applications
    private static List<Project> projects = new ArrayList<>(); // Simulating a database of projects

    // Approve officer registration
    public boolean approveOfficerRegistration(String registrationId) {
        for (Registration registration : registrations) {
            if (registration.getRegistrationId().equals(registrationId) && registration.getStatus().equals("Pending")) {
                registration.setStatus("Approved");
                return true;
            }
        }
        return false; // Registration not found or already processed
    }

    // Reject officer registration
    public boolean rejectOfficerRegistration(String registrationId) {
        for (Registration registration : registrations) {
            if (registration.getRegistrationId().equals(registrationId) && registration.getStatus().equals("Pending")) {
                registration.setStatus("Rejected");
                return true;
            }
        }
        return false; // Registration not found or already processed
    }

    // Approve application
    public boolean approveApplication(String applicationId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId) && application.getStatus().equals("Pending")) {
                application.setStatus("Approved");
                return true;
            }
        }
        return false; // Application not found or already processed
    }

    // Reject application
    public boolean rejectApplication(String applicationId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId) && application.getStatus().equals("Pending")) {
                application.setStatus("Rejected");
                return true;
            }
        }
        return false; // Application not found or already processed
    }

    // Approve withdrawal request
    public boolean approveWithdrawalRequest(String withdrawalRequestId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(withdrawalRequestId) && application.getStatus().equals("Withdrawal Requested")) {
                application.setStatus("Withdrawn");
                return true;
            }
        }
        return false; // Withdrawal request not found or already processed
    }

    // Reject withdrawal request
    public boolean rejectWithdrawalRequest(String withdrawalRequestId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(withdrawalRequestId) && application.getStatus().equals("Withdrawal Requested")) {
                application.setStatus("Rejected");
                return true;
            }
        }
        return false; // Withdrawal request not found or already processed
    }

    // Filter projects by creator
    public List<Project> filterProjectsByCreator(String managerNRIC) {
        List<Project> filteredProjects = new ArrayList<>();
        for (Project project : projects) {
            if (project.getCreatorNRIC().equals(managerNRIC)) {
                filteredProjects.add(project);
            }
        }
        return filteredProjects;
    }
}