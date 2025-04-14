package controllers;

import models.HDBOfficer;
import models.Project;
import models.Application;
import models.Applicant;
import models.Receipt;
import models.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HDBOfficerController {

    private static List<HDBOfficer> officers = new ArrayList<>(); // Simulating a database of officers
    private static List<Project> projects = new ArrayList<>(); // Simulating a database of projects
    private static List<Application> applications = new ArrayList<>(); // Simulating a database of applications
    private static List<Applicant> applicants = new ArrayList<>(); // Simulating a database of applicants
    private static List<Receipt> receipts = new ArrayList<>(); // Simulating a database of receipts

    // Register officer for a project
    public boolean registerForProject(String officerNRIC, String projectId) {
        // Check if officer has applied for this project
        for (Application app : applications) {
            if (app.getApplicantNRIC().equals(officerNRIC) && app.getProjectId().equals(projectId)) {
                System.out.println("Cannot register as HDB Officer. You have already applied for this project as an Applicant.");
                return false;
            }
        }
        
        // Check if officer is already registered for another project within application period
        for (Project project : projects) {
            if (project.getProjectId().equals(projectId)) {
                continue; // Skip the current project being applied for
            }
            
            // Check if officer is registered for another project in its application period
            Date currentDate = new Date();
            if (currentDate.after(project.getApplicationOpeningDate()) && 
                currentDate.before(project.getApplicationClosingDate())) {
                
                for (HDBOfficer officer : officers) {
                    if (officer.getNric().equals(officerNRIC) && 
                        officer.getAssignedProjectId() != null && 
                        officer.getAssignedProjectId().equals(project.getProjectId())) {
                        System.out.println("Cannot register. You are already registered for another project within its application period.");
                        return false;
                    }
                }
            }
        }
        
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
        // First check our applicants list
        for (Applicant applicant : applicants) {
            if (applicant.getNric().equals(applicantNRIC)) {
                return applicant;
            }
        }
        
        // If not found in our list, check applications and create a mock applicant
        for (Application application : applications) {
            if (application.getApplicantNRIC().equals(applicantNRIC)) {
                Applicant newApplicant = new Applicant(application.getApplicantNRIC(), "Applicant Name", "password", 30, "Single");
                applicants.add(newApplicant); // Add to our list for future reference
                return newApplicant;
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
            if (application.getApplicationId().equals(applicationId) && 
               (application.getStatus().equals("Successful") || application.getStatus().equals("Booked"))) {
                
                // Get applicant details
                Applicant applicant = retrieveApplicantByNRIC(application.getApplicantNRIC());
                if (applicant == null) {
                    return null; // No applicant found
                }
                
                // Get project details
                Project project = null;
                for (Project p : projects) {
                    if (p.getProjectId().equals(application.getProjectId())) {
                        project = p;
                        break;
                    }
                }
                
                if (project == null) {
                    return null; // No project found
                }
                
                // Create and save receipt
                Receipt receipt = new Receipt();
                receipt.setReceiptId("REC" + System.currentTimeMillis());
                receipt.setApplicantNRIC(application.getApplicantNRIC());
                receipt.setApplicantName(applicant.getName());
                receipt.setApplicantAge(applicant.getAge());
                receipt.setMaritalStatus(applicant.getMaritalStatus());
                receipt.setProjectName(project.getProjectName());
                receipt.setFlatType(application.getFlatType());
                receipt.setBookingDate(new Date());
                
                receipts.add(receipt); // Save receipt for future reference
                return receipt;
            }
        }
        return null; // Receipt cannot be generated
    }
    
    // Update flat selection for an application
    public boolean updateFlatSelection(String applicationId, String flatType) {
        for (Application application : applications) {
            if (application.getApplicationId().equals(applicationId)) {
                application.setFlatType(flatType);
                return true;
            }
        }
        return false; // Application not found
    }
    
    // Update applicant profile with flat type under a project
    public boolean updateApplicantProfile(String applicantNRIC, String projectId, String flatType) {
        Applicant applicant = retrieveApplicantByNRIC(applicantNRIC);
        if (applicant == null) {
            return false; // Applicant not found
        }
        
        // In a real implementation, you would update the applicant's profile with the chosen flat type
        // For this simulation, we'll just assume it's successful
        return true;
    }
    
    // Check if an applicant has the specified application status for a project
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
    
    // Get receipt by application ID
    public Receipt getReceiptByApplicationId(String applicationId) {
        for (Receipt receipt : receipts) {
            if (receipt.getApplicationId().equals(applicationId)) {
                return receipt;
            }
        }
        return null; // Receipt not found
    }
    
    // Add these dummy initialization methods for testing
    public void addDummyOfficer(HDBOfficer officer) {
        officers.add(officer);
    }
    
    public void addDummyProject(Project project) {
        projects.add(project);
    }
    
    public void addDummyApplication(Application application) {
        applications.add(application);
    }
    
    public void addDummyApplicant(Applicant applicant) {
        applicants.add(applicant);
    }
}