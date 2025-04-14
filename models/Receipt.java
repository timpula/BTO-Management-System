package models;

import java.util.Date;

public class Receipt {
    private String receiptId;
    private String applicationId;
    private String applicantName;
    private String applicantNRIC;
    private int applicantAge;
    private String maritalStatus;
    private String projectName;
    private String flatType;
    private Date bookingDate;
    
    // Constructors
    public Receipt() {}
    
    public Receipt(String receiptId, String applicationId, String applicantName, 
                  String applicantNRIC, int applicantAge, String maritalStatus, 
                  String projectName, String flatType, Date bookingDate) {
        this.receiptId = receiptId;
        this.applicationId = applicationId;
        this.applicantName = applicantName;
        this.applicantNRIC = applicantNRIC;
        this.applicantAge = applicantAge;
        this.maritalStatus = maritalStatus;
        this.projectName = projectName;
        this.flatType = flatType;
        this.bookingDate = bookingDate;
    }
    
    // Getters and setters
    public String getReceiptId() {
        return receiptId;
    }
    
    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }
    
    public String getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(String applicationId) {
        this.applicationId = applicationId;
    }
    
    public String getApplicantName() {
        return applicantName;
    }
    
    public void setApplicantName(String applicantName) {
        this.applicantName = applicantName;
    }
    
    public String getApplicantNRIC() {
        return applicantNRIC;
    }
    
    public void setApplicantNRIC(String applicantNRIC) {
        this.applicantNRIC = applicantNRIC;
    }
    
    public int getApplicantAge() {
        return applicantAge;
    }
    
    public void setApplicantAge(int applicantAge) {
        this.applicantAge = applicantAge;
    }
    
    public String getMaritalStatus() {
        return maritalStatus;
    }
    
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getFlatType() {
        return flatType;
    }
    
    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }
    
    public Date getBookingDate() {
        return bookingDate;
    }
    
    public void setBookingDate(Date bookingDate) {
        this.bookingDate = bookingDate;
    }
}