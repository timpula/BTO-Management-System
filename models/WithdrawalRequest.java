package models;

import java.util.Date;

public class WithdrawalRequest {
    private String requestId;
    private String applicationId;
    private String applicantNRIC;
    private Date requestDate;
    private String status; // "Pending", "Approved", "Rejected"
    private String reason;
    
    // Constructors
    public WithdrawalRequest() {}
    
    public WithdrawalRequest(String requestId, String applicationId, String applicantNRIC, 
                           Date requestDate, String reason) {
        this.requestId = requestId;
        this.applicationId = applicationId;
        this.applicantNRIC = applicantNRIC;
        this.requestDate = requestDate;
        this.status = "Pending"; // Default status
        this.reason = reason;
    }
    
    // Getters and setters
    public String getRequestId() {
        return requestId;
    }
    
    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }
    
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
    
    public Date getRequestDate() {
        return requestDate;
    }
    
    public void setRequestDate(Date requestDate) {
        this.requestDate = requestDate;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}