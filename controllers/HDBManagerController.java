package controllers;

import models.HDBManager;
import models.Project;
import models.Registration;
import models.Application;
import models.User;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Controller for HDBManager-specific actions,
 * delegates operations to underlying controllers.
 */
public class HDBManagerController implements IChangePassword, IFilter {
    private UserController userController;
    private ProjectController projectController;
    private RegistrationController registrationController;
    private ApplicationController applicationController;
    private HDBOfficerController officerController;

    /**
     * Constructor for dependency injection.
     */
    public HDBManagerController(UserController uc,
                                ProjectController pc,
                                RegistrationController rc,
                                ApplicationController ac,
                                HDBOfficerController oc) {
        this.userController         = uc;
        this.projectController      = pc;
        this.registrationController = rc;
        this.applicationController  = ac;
        this.officerController      = oc;
    }

    /**
     * Default constructor wiring default controllers.
     */
    public HDBManagerController() {
        this(new UserController(),
             new ProjectController(),
             new RegistrationController(),
             new ApplicationController(),
             new HDBOfficerController());
    }

    // --- Officer Registration Approval/Rejection ---

  /**
     * Approve officer registration and update officer status.
     */
    public boolean approveOfficerRegistration(String registrationId) {
        // 1) update Registration record
        boolean ok = registrationController.updateRegistrationStatus(registrationId, "Approved");
        if (!ok) return false;

        // 2) sync officer model
        Registration reg = registrationController.getRegistrationById(registrationId);
        if (reg == null) return false;
        return officerController.setOfficerRegistrationStatus(
            reg.getOfficerNRIC(),
            reg.getProjectId(),
            "Approved"
        );
    }

    /**
     * Reject officer registration and update officer status.
     */
    public boolean rejectOfficerRegistration(String registrationId) {
        boolean ok = registrationController.updateRegistrationStatus(registrationId, "Rejected");
        if (!ok) return false;

        Registration reg = registrationController.getRegistrationById(registrationId);
        if (reg == null) return false;
        return officerController.setOfficerRegistrationStatus(
            reg.getOfficerNRIC(),
            reg.getProjectId(),
            "Rejected"
        );
    }

    /**
     * View registrations by status ("Pending", "Approved").
     */
    public List<Registration> viewOfficerRegistrationsByStatus(String status) {
        return registrationController.getRegistrationsByStatus(status);
    }

    // --- Application Approval/Rejection ---

    public boolean approveApplication(String applicationId) {
        return applicationController.updateApplicationStatus(applicationId, "SUCCESSFUL");
    }

    public boolean rejectApplication(String applicationId) {
        return applicationController.updateApplicationStatus(applicationId, "UNSUCCESSFUL");
    }

    // --- Withdrawal Request Approval/Rejection ---

    public boolean approveWithdrawalRequest(String applicationId) {
        return applicationController.processWithdrawal(applicationId);
    }

    public boolean rejectWithdrawalRequest(String applicationId) {
        // Revert withdrawal: set back to Successful
        return applicationController.updateApplicationStatus(applicationId, "SUCCESSFUL");
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
        return projectController.viewAllProjects().stream()
                .filter(p -> managerNric.equals(p.getCreatorNRIC()))
                .collect(Collectors.toList());
    }
}
