package controllers;

import models.Project;
import models.Application;
import models.Applicant;
import java.util.ArrayList;
import java.util.List;

public class ApplicantController implements IChangePassword, IFilter {

    private ApplicationController applicationController;
    private UserController userController;
    private ProjectController projectController;

    public ApplicantController(ApplicationController applicationController, UserController userController,
            ProjectController projectController) {
        this.applicationController = applicationController;
        this.userController = userController;
        this.projectController = projectController;
    }

    // Constructor for backward compatibility
    public ApplicantController(ApplicationController applicationController) {
        this.applicationController = applicationController;
        this.userController = new UserController();
        this.projectController = new ProjectController();
    }

    // Get applicant details by NRIC
    public Applicant getApplicantByNRIC(String nric) {
        return (Applicant) userController.getUserByNRIC(nric);
    }

    // Get applicant name
    public String getApplicantName(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        return applicant != null ? applicant.getName() : "Unknown";
    }

    // Get applicant user type
    public String getApplicantUserType(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        return applicant != null ? applicant.getUserType() : "Unknown";
    }

    // Get applicant age
    public int getApplicantAge(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        return applicant != null ? applicant.getAge() : 0;
    }

    // Get applicant marital status
    public String getApplicantMaritalStatus(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        return applicant != null ? applicant.getMaritalStatus() : "Unknown";
    }

    // Get applicant current application
    public Application getCurrentApplication(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        if (applicant == null) {
            System.out.println("Applicant not found.");
            return null;
        }
        return applicationController.getApplicationByNRIC(nric);
    }

    // Save an application
    private boolean saveApplication(Application application) {
        return applicationController.submitApplication(application);
    }

    // View eligible projects for an applicant
    public List<Project> viewEligibleProjects(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        if (applicant == null) {
            System.out.println("Applicant not found.");
            return new ArrayList<>();
        }

        List<Project> eligibleProjects = new ArrayList<>();
        List<Project> projects = projectController.viewAllProjects();

        for (Project project : projects) {
            if (!project.isVisible()) continue; 
            if (checkEligibility(nric, project)) {
                eligibleProjects.add(project);
            }
        }
        return eligibleProjects;
    }

    // Apply for a project
    public boolean applyForProject(String nric, String projectId) {
        Project project = projectController.getProjectDetails(projectId);

        // 1) derive flatType from the project’s name:
        String rawName = project.getProjectName();
        String flatType = "";
        if (rawName.contains(" - ")) {
            // split on " - " and take the last segment
            String[] parts = rawName.split(" - ");
            flatType = parts[parts.length - 1].trim();
        }
        Applicant applicant = getApplicantByNRIC(nric);
        if (applicant == null) {
            System.out.println("Applicant not found.");
            return false;
        }

        Application application = new Application();
        application.setApplicationId("APP" + System.currentTimeMillis());
        application.setApplicantNRIC(nric);
        application.setProjectId(projectId);
        application.setApplicationDate(new java.util.Date());
        application.setStatus("Pending");
        application.setFlatType(flatType); 

        // Save the application
        boolean saved = saveApplication(application);

        if (saved) {
            return true;
        }
        return false;
    }

    // View application status
    public Application viewApplicationStatus(String nric) {
        System.out.println("Checking application status for Applicant NRIC: " + nric);

        // Delegate to ApplicationController to get the application by NRIC
        Application application = applicationController.getApplicationByNRIC(nric);

        if (application != null) {
            System.out.println("Active application found: " + application.getApplicationId());
            return application;
        }

        System.out.println("No active application found for Applicant NRIC: " + nric);
        return null; // No active application found
    }

    // Request withdrawal of an application
    public boolean requestWithdrawal(String nric, String applicationId) {
        Application application = applicationController.getApplicationByNRIC(nric);

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
    public boolean checkEligibility(String nric, Project project) {
        Applicant applicant = getApplicantByNRIC(nric);
        if (applicant == null) {
            return false;
        }

        for (String flatType : project.getFlatTypeUnits().keySet()) {
            boolean ageEligible = validateAgeRequirement(applicant, flatType);
            boolean maritalStatusEligible = validateMaritalStatus(applicant, flatType);

            if (ageEligible && maritalStatusEligible) {
                return true;
            }
        }
        return false;
    }

    // Validate age requirement for a flat type
    private boolean validateAgeRequirement(Applicant applicant, String flatType) {
        int age = applicant.getAge();

        if (flatType.equals("2-Room") && age >= 35) {
            return true;
        } else if (!flatType.equals("2-Room") && age >= 21) {
            return true;
        }
        return false;
    }

    // Validate marital status requirement for a flat type
    private boolean validateMaritalStatus(Applicant applicant, String flatType) {
        String maritalStatus = applicant.getMaritalStatus();

        if (flatType.equals("2-Room") && maritalStatus.equals("Single")) {
            return true;
        } else if (flatType.equals("3-Room") && maritalStatus.equals("Married")) {
            return true;
        } else if (flatType.equals("2-Room") && maritalStatus.equals("Married")) {
            return true;
        }
        return false;
    }

    // Set filters for an applicant
    public void setFilters(String nric, String neighborhood, String flatType) {
        Applicant applicant = getApplicantByNRIC(nric);
        if (applicant != null) {
            applicant.setFilterNeighborhood(neighborhood.isEmpty() ? null : neighborhood);
            applicant.setFilterFlatType(flatType.isEmpty() ? null : flatType);
        }
    }

    // Get filter neighborhood
    public String getFilterNeighborhood(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        return applicant != null ? applicant.getFilterNeighborhood() : null;
    }

    // Get filter flat type
    public String getFilterFlatType(String nric) {
        Applicant applicant = getApplicantByNRIC(nric);
        return applicant != null ? applicant.getFilterFlatType() : null;
    }

    @Override
    public boolean changePassword(String nric, String currentPassword, String newPassword) {
        Applicant applicant = (Applicant) userController.getUserByNRIC(nric);
        if (applicant != null && applicant.getPassword().equals(currentPassword)) {
            applicant.setPassword(newPassword);
            return true;
        }
        return false;
    }

    @Override
    public List<Project> filterProjects(String nric) {
        // Retrieve the current applicant
        Applicant applicant = getApplicantByNRIC(nric);
        if (applicant == null) {
            System.out.println("No applicant found for the current user.");
            return new ArrayList<>(); // Return an empty list if no applicant is found
        }

        // Retrieve all projects
        List<Project> allProjects = projectController.viewAllProjects();

        // Retrieve filters from the applicant
        String filterNeighborhood = applicant.getFilterNeighborhood();
        String filterFlatType = applicant.getFilterFlatType();

        // Stream from all projects, but only include those that are visible
        return projectController.viewAllProjects().stream()
                .filter(Project::isVisible)                               // only visible projects
                .filter(project -> filterNeighborhood == null
                        || project.getNeighborhood().equalsIgnoreCase(filterNeighborhood))
                .filter(project -> filterFlatType == null
                        || project.getFlatTypeUnits().containsKey(filterFlatType))
                .toList();
    }
}