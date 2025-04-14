package models;

import java.util.Date;

public class Enquiry {
    private String enquiryId;
    private String userNRIC;
    private String projectId;
    private String content;
    private String reply;
    private Date enquiryDate;
    private Date replyDate;
    
    // Constructors
    public Enquiry() {}
    
    public Enquiry(String enquiryId, String userNRIC, String projectId, 
                  String content, Date enquiryDate) {
        this.enquiryId = enquiryId;
        this.userNRIC = userNRIC;
        this.projectId = projectId;
        this.content = content;
        this.enquiryDate = enquiryDate;
    }
    
    // Getters and setters
    public String getEnquiryId() {
        return enquiryId;
    }
    
    public void setEnquiryId(String enquiryId) {
        this.enquiryId = enquiryId;
    }
    
    public String getUserNRIC() {
        return userNRIC;
    }
    
    public void setUserNRIC(String userNRIC) {
        this.userNRIC = userNRIC;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getContent() {
        return content;
    }
    
    public void setContent(String content) {
        this.content = content;
    }
    
    public String getReply() {
        return reply;
    }
    
    public void setReply(String reply) {
        this.reply = reply;
    }
    
    public Date getEnquiryDate() {
        return enquiryDate;
    }
    
    public void setEnquiryDate(Date enquiryDate) {
        this.enquiryDate = enquiryDate;
    }
    
    public Date getReplyDate() {
        return replyDate;
    }
    
    public void setReplyDate(Date replyDate) {
        this.replyDate = replyDate;
    }
}