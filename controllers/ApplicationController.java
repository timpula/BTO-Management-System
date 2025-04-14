package controllers;

import models.Application;
import java.util.ArrayList;
import java.util.List;

public class ApplicationController {
    private static List<Application> applications = new ArrayList<>(); // Simulating a database

    // Submit an application
    public boolean submitApplication(Application application) {
        if (application != null) {
            // Check if application with same ID already exists
            for (Application existingApplication : applications) {
                if (existingApplication.getApplicationId().equals(application.getApplicationId())) {
                    System.out.println("Application with ID " + application.getApplicationId() + " already exists.");
                    return false;
                }
            }
            
            // Check if applicant already has an active application
            Application existingApplication = getApplicationByNRIC(application.getApplicantNRIC());
            if (existingApplication != null && !existingApplication.getStatus().equals("Withdrawn")) {
                System.out.println("Applicant already has an active application: " + existingApplication.getApplicationId());
                return false;
            }
            
            applications.add(application);
            System.out.println("Application submitted successfully: " + application.getApplicationId());
            return true;
        }
        System.out.println("Cannot submit null application");
        return false;
    }

    // Update application status
    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        if (applicationId == null || newStatus == null) {
            System.out.println("Application ID or new status cannot be null");
            return false;
        }
        
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                // Validate status transition
                if (isValidStatusTransition(application.getStatus(), newStatus)) {
                    application.setStatus(newStatus);
                    System.out.println("Application " + applicationId + " status updated to: " + newStatus);
                    return true;
                } else {
                    System.out.println("Invalid status transition from " + application.getStatus() + " to " + newStatus);
                    return false;
                }
            }
        }
        System.out.println("Application not found: " + applicationId);
        return false;
    }

    // Validate status transition
    private boolean isValidStatusTransition(String currentStatus, String newStatus) {
        // Define valid transitions
        if (currentStatus.equals("Pending")) {
            return newStatus.equals("Successful") || newStatus.equals("Unsuccessful") || newStatus.equals("Withdrawn");
        } else if (currentStatus.equals("Successful")) {
            return newStatus.equals("Booked") || newStatus.equals("Withdrawn");
        } else if (currentStatus.equals("Booked")) {
            return false; // Final state, no further transitions
        } else if (currentStatus.equals("Unsuccessful")) {
            return false; // Final state, no further transitions
        } else if (currentStatus.equals("Withdrawn")) {
            return false; // Final state, no further transitions
        }
        return false;
    }

    // Get application by NRIC
    public Application getApplicationByNRIC(String applicantNRIC) {
        if (applicantNRIC == null) {
            System.out.println("NRIC cannot be null");
            return null;
        }

        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(applicantNRIC) && !application.getStatus().equals("Withdrawn")) {
                System.out.println("Found active application for NRIC: " + applicantNRIC);
                return application;
            }
        }
        System.out.println("No active application found for NRIC: " + applicantNRIC);
        return null; // No active application found
    }

    // Get application by ID
    public Application getApplicationById(String applicationId) {
        if (applicationId == null) {
            System.out.println("Application ID cannot be null");
            return null;
        }
        
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                return application;
            }
        }
        System.out.println("Application not found: " + applicationId);
        return null;
    }

    // Update an existing application
    public boolean updateApplication(Application updatedApplication) {
        if (updatedApplication == null || updatedApplication.getApplicationId() == null) {
            System.out.println("Invalid application data for update");
            return false;
        }
        
        for (int i = 0; i < applications.size(); i++) {
            if (applications.get(i).getApplicationId().equals(updatedApplication.getApplicationId())) {
                applications.set(i, updatedApplication);
                System.out.println("Application updated: " + updatedApplication.getApplicationId());
                return true;
            }
        }
        System.out.println("Application not found for update: " + updatedApplication.getApplicationId());
        return false;
    }

    // Validate application eligibility
    public boolean validateApplicationEligibility(Application application) {
        if (application == null) {
            return false;
        }
        
        // Check application status
        if (!application.getStatus().equals("Pending")) {
            return false;
        }
        
        // Additional validation logic can be added here
        
        return true;
    }

    // Get applications by project ID
    public List<Application> getApplicationsByProject(String projectId) {
        if (projectId == null) {
            System.out.println("Project ID cannot be null");
            return new ArrayList<>();
        }
        
        List<Application> projectApplications = new ArrayList<>();
        for (Application application : applications) {
            if (application.getProjectId().equals(projectId)) {
                projectApplications.add(application);
            }
        }
        System.out.println("Found " + projectApplications.size() + " applications for project: " + projectId);
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
        if (status == null) {
            System.out.println("Status cannot be null");
            return new ArrayList<>();
        }
        
        List<Application> statusApplications = new ArrayList<>();
        for (Application application : applications) {
            if (application.getStatus().equals(status)) {
                statusApplications.add(application);
            }
        }
        System.out.println("Found " + statusApplications.size() + " applications with status: " + status);
        return statusApplications;
    }

    // Process withdrawal of an application
    public boolean processWithdrawal(String applicationId) {
        if (applicationId == null) {
            System.out.println("Application ID cannot be null");
            return false;
        }
        
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                // Only allow withdrawal if status is Pending or Successful
                if (application.getStatus().equals("Pending") || application.getStatus().equals("Successful")) {
                    application.setStatus("Withdrawn");
                    System.out.println("Application withdrawn: " + applicationId);
                    return true;
                } else {
                    System.out.println("Cannot withdraw application with status: " + application.getStatus());
                    return false;
                }
            }
        }
        System.out.println("Application not found: " + applicationId);
        return false;
    }
    
    // Get application by applicant and project
    public Application getApplicationByApplicantAndProject(String applicantNRIC, String projectId) {
        if (applicantNRIC == null || projectId == null) {
            System.out.println("NRIC or project ID cannot be null");
            return null;
        }
        
        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(applicantNRIC) && 
                application.getProjectId().equals(projectId)) {
                return application;
            }
        }
        return null;
    }
    
    // Update flat selection for an application
    public boolean updateFlatSelection(String applicationId, String flatType) {
        if (applicationId == null || flatType == null) {
            System.out.println("Application ID or flat type cannot be null");
            return false;
        }
        
        Application application = getApplicationById(applicationId);
        if (application != null) {
            application.setFlatType(flatType);
            System.out.println("Flat type updated for application: " + applicationId);
            return true;
        }
        return false;
    }
    
    // Get all applications (for admin purposes)
    public List<Application> getAllApplications() {
        return new ArrayList<>(applications);
    }
}