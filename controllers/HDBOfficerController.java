package controllers;

import models.*;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HDBOfficerController implements IChangePassword, IFilter {

    private static List<HDBOfficer> officers = new ArrayList<>();
    private static List<Application> applications = new ArrayList<>();
    private static List<Applicant> applicants = new ArrayList<>();
    private static List<Receipt> receipts = new ArrayList<>();
    private ProjectController projectController = new ProjectController();

    public boolean registerForProject(String officerNRIC, String projectId) {
        // Existing validation code...
        for (Application app : applications) {
            if (app.getApplicantNRIC().equals(officerNRIC) 
                && app.getProjectId().equals(projectId) 
                && !app.getStatus().equals("Withdrawn")) {
                System.out.println("Cannot register: You have an active application for this project");
                return false;
            }
        }
    
        Project targetProject = projectController.getProjectDetails(projectId);
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC) && officer.getAssignedProjectId() != null) {
                Project assignedProject = projectController.getProjectDetails(officer.getAssignedProjectId());
                if (hasOverlappingPeriod(targetProject, assignedProject)) {
                    System.out.println("Cannot register: Already registered for another project in same period");
                    return false;
                }
            }
        }
    
        // ADD THIS: Update the officer's information
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC)) {
                officer.setAssignedProjectId(projectId);
                officer.setRegistrationStatus("Pending"); // Set initial status
                System.out.println("Registration submitted for approval");
                return true;
            }
        }
    
        return false; // Officer not found
    }

    private boolean hasOverlappingPeriod(Project project1, Project project2) {
        return !(project1.getApplicationClosingDate().before(project2.getApplicationOpeningDate()) ||
                 project2.getApplicationClosingDate().before(project1.getApplicationOpeningDate()));
    }

    public String viewRegistrationStatus(String officerNRIC, String projectId) {
        UserController userController = new UserController();
        User user = userController.viewUserDetails(officerNRIC);

        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            if (projectId.equals(officer.getAssignedProjectId())) {
                return officer.getRegistrationStatus();
            }
        }

        return "Not Registered";
    }

    public Project viewAssignedProject(String officerNRIC) {
        UserController userController = new UserController();
        User user = userController.viewUserDetails(officerNRIC);

        if (user instanceof HDBOfficer) {
            HDBOfficer officer = (HDBOfficer) user;
            if (officer.getAssignedProjectId() != null && "Approved".equalsIgnoreCase(officer.getRegistrationStatus())) {
                return projectController.getProjectDetails(officer.getAssignedProjectId());
            }
        }

        return null;
    }

    public Applicant retrieveApplicantByNRIC(String applicantNRIC) {
        UserController userController = new UserController();
        User user = userController.viewUserDetails(applicantNRIC);
        if (user instanceof Applicant) {
            return (Applicant) user;
        }
        return null;
    }

    public boolean updateFlatRemaining(String projectId, String flatType, int newCount) {
        Project project = projectController.getProjectDetails(projectId);
        if (project != null) {
            project.getFlatTypeUnits().put(flatType, newCount);
            return true;
        }
        return false;
    }

    public boolean updateApplicationStatus(String applicationId, String newStatus) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setStatus(newStatus);
                return true;
            }
        }
        return false;
    }

    public Receipt generateBookingReceipt(String applicationId) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId) &&
                (application.getStatus().equals("SUCCESSFUL") || application.getStatus().equals("BOOKED"))) {

                Applicant applicant = retrieveApplicantByNRIC(application.getApplicantNRIC());
                if (applicant == null) return null;

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

    public boolean updateFlatSelection(String applicationId, String flatType) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setFlatType(flatType);
                return true;
            }
        }
        return false;
    }

    public boolean updateApplicantProfile(String applicantNRIC, String projectId, String flatType) {
        Applicant applicant = retrieveApplicantByNRIC(applicantNRIC);
        if (applicant == null) return false;

        applicant.setBookedFlatType(flatType);
        applicant.setBookedProjectId(projectId);
        return true;
    }

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

    public Receipt getReceiptByApplicationId(String applicationId) {
        for (Receipt receipt : receipts) {
            if (receipt.getApplicationId().equals(applicationId)) {
                return receipt;
            }
        }
        return null;
    }

    // Implement IChangePassword
    @Override
    public boolean changePassword(String nric, String currentPassword, String newPassword) {
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(nric) && officer.getPassword().equals(currentPassword)) {
                officer.setPassword(newPassword);
                return true;
            }
        }
        return false;
    }

    // Implement IFilter
    @Override
    public List<Project> filterProjects(String nric) {
        List<Project> filtered = new ArrayList<>();
        HDBOfficer target = null;

        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(nric)) {
                target = officer;
                break;
            }
        }

        if (target == null) return filtered;

        for (Project project : projectController.viewAllProjects()) {
            boolean match = (target.getFilterNeighborhood() == null || 
                             project.getNeighborhood().equalsIgnoreCase(target.getFilterNeighborhood())) &&
                            (target.getFilterFlatType() == null || 
                             project.getFlatTypeUnits().containsKey(target.getFilterFlatType()));
            if (match) {
                filtered.add(project);
            }
        }

        return filtered;
    }

    public void setFilters(String officerNRIC, String neighborhood, String flatType) {
        for (HDBOfficer officer : officers) {
            if (officer.getNric().equals(officerNRIC)) {
                officer.setFilterNeighborhood(neighborhood.isEmpty() ? null : neighborhood);
                officer.setFilterFlatType(flatType.isEmpty() ? null : flatType);
                return;
            }
        }
    }

    // For testing and preloading
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