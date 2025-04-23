package controllers;

import models.HDBManager;
import models.Project;
import models.Registration;
import models.Application;
import models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Controller for HDBManager-specific actions,
 * delegates operations to underlying controllers.
 */
public class HDBManagerController implements IChangePassword, IFilter {
    private UserController userController;
    private ProjectController projectController;
    private RegistrationController registrationController;
    private ApplicationController applicationController;

    /**
     * Constructor for dependency injection.
     */
    public HDBManagerController(UserController uc,
                                ProjectController pc,
                                RegistrationController rc,
                                ApplicationController ac) {
        this.userController         = uc;
        this.projectController      = pc;
        this.registrationController = rc;
        this.applicationController  = ac;
    }

    /**
     * Default constructor wiring default controllers.
     */
    public HDBManagerController() {
        this(new UserController(),
             new ProjectController(),
             new RegistrationController(),
             new ApplicationController());
    }

    // --- Officer Registration Approval/Rejection ---

    public boolean approveOfficerRegistration(String registrationId) {
        return registrationController.approveOfficerRegistration(registrationId);
    }

    public boolean rejectOfficerRegistration(String registrationId) {
        return registrationController.rejectOfficerRegistration(registrationId);
    }

    /**
     * View registrations by status ("Pending", "Approved").
     */
    public List<Registration> viewOfficerRegistrationsByStatus(String status) {
        return registrationController.getRegistrationsByStatus(status);
    }

    // --- Application Approval/Rejection ---

    public boolean approveApplication(String applicationId) {
        return applicationController.approveApplication(applicationId);
    }

    public boolean rejectApplication(String applicationId) {
        return applicationController.rejectApplication(applicationId);
    }

    // --- Withdrawal Request Approval/Rejection ---

    public boolean approveWithdrawalRequest(String applicationId) {
        return applicationController.processWithdrawal(applicationId);
    }

    public boolean rejectWithdrawalRequest(String applicationId) {
        // Assuming that rejecting a withdrawal means setting back to "Approved"
        return applicationController.updateApplicationStatus(applicationId, "Approved");
    }

    // --- Project Visibility ---

    public boolean toggleProjectVisibility(String projectId, boolean visibility) {
        return projectController.toggleProjectVisibility(projectId, visibility);
    }

    // --- IChangePassword implementation ---

    @Override
    public boolean changePassword(String nric, String currentPassword, String newPassword) {
        User user = userController.viewUserDetails(nric);
        if (user instanceof HDBManager && user.getPassword().equals(currentPassword)) {
            user.setPassword(newPassword);
            return true;
        }
        return false;
    }

    // --- IFilter implementation ---

    @Override
    public List<Project> filterProjects(String managerNric) {
        List<Project> result = new ArrayList<>();
        for (Project p : projectController.viewAllProjects()) {
            if (managerNric.equals(p.getCreatorNRIC())) {
                result.add(p);
            }
        }
        return result;
    }
}
