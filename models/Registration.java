package models;

import java.util.Date;

public class Registration {
    private String registrationId;
    private String officerNRIC;
    private String projectId;
    private Date registrationDate;
    private String status; // "Pending", "Approved", "Rejected"
    
    // Constructors
    public Registration() {}
    
    public Registration(String registrationId, String officerNRIC, String projectId, 
                       Date registrationDate) {
        this.registrationId = registrationId;
        this.officerNRIC = officerNRIC;
        this.projectId = projectId;
        this.registrationDate = registrationDate;
        this.status = "Pending"; // Default status
    }
    
    // Getters and setters
    public String getRegistrationId() {
        return registrationId;
    }
    
    public void setRegistrationId(String registrationId) {
        this.registrationId = registrationId;
    }
    
    public String getOfficerNRIC() {
        return officerNRIC;
    }
    
    public void setOfficerNRIC(String officerNRIC) {
        this.officerNRIC = officerNRIC;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public Date getRegistrationDate() {
        return registrationDate;
    }
    
    public void setRegistrationDate(Date registrationDate) {
        this.registrationDate = registrationDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
}