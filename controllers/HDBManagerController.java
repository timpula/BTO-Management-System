package controllers;

import models.Registration;
import models.User;
import models.Application;
import models.HDBManager;
import models.Project;
import java.util.ArrayList;
import java.util.List;

public class HDBManagerController implements IChangePassword, IFilter{
    private UserController userController;
    private ProjectController projectController;
    private RegistrationController registrationController;
    private ApplicationController applicationController;

    // Dependency-injected constructor
    public HDBManagerController(UserController uc,
                                ProjectController pc,
                                RegistrationController rc,
                                ApplicationController ac) {
        this.userController = uc;
        this.projectController = pc;
        this.registrationController = rc;
        this.applicationController = ac;
    }

    // Default constructor for backward compatibility
    public HDBManagerController() {
        this(new UserController(),
             new ProjectController(),
             new RegistrationController(),
             new ApplicationController());
    }

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

    // Return all officer registrations with the given status
    public List<Registration> viewOfficerRegistrationsByStatus(String status) {
        return registrationController.getRegistrationsByStatus(status);
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

    

    //Allow an HDBManager to change their own password.
    @Override
    public boolean changePassword(String nric, String currentPassword, String newPassword) {
        User user = userController.viewUserDetails(nric);
        if (user instanceof HDBManager && user.getPassword().equals(currentPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    // Toggle visiblity of a project
    public boolean toggleProjectVisibility(String projectId, boolean visibility) {
        return projectController.toggleProjectVisibility(projectId, visibility);
    }    
    
    //Filter projects by the manager's NRIC (only their own projects).
    @Override
    public List<Project> filterProjects(String managerNric) {
        List<Project> allProjects = projectController.viewAllProjects();
        List<Project> filtered = new ArrayList<>();
        for (Project p : allProjects) {
            if (managerNric.equals(p.getCreatorNRIC())) {
                filtered.add(p);
            }
        }
        return filtered;
    }
}