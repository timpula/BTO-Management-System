package models;

import java.util.Date;

public class Application {
    private String applicationId;
    private String applicantNRIC;
    private String projectId;
    private Date applicationDate;
    private String status; // "Pending", "Successful", "Unsuccessful", "Booked"
    private String flatType; // Selected flat type if booked
    
    // Constructors
    public Application() {}
    
    public Application(String applicationId, String applicantNRIC, String projectId, 
                      Date applicationDate) {
        this.applicationId = applicationId;
        this.applicantNRIC = applicantNRIC;
        this.projectId = projectId;
        this.applicationDate = applicationDate;
        this.status = "Pending"; // Default status
    }
    
    // Getters and setters
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getApplicantNRIC() {
        return applicantNRIC;
    }
    
    public void setApplicantNRIC(String applicantNRIC) {
        this.applicantNRIC = applicantNRIC;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public Date getApplicationDate() {
        return applicationDate;
    }
    
    public void setApplicationDate(Date applicationDate) {
        this.applicationDate = applicationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status.trim().toUpperCase(); // Normalize to lowercase
    }
    
    public String getFlatType() {
        return flatType;
    }
    
    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }
}