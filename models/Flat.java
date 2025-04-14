package models;

public class Flat {
    private String flatId;
    private String projectId;
    private String flatType; // "2-Room" or "3-Room"
    private boolean isBooked;
    
    // Constructors
    public Flat() {}
    
    public Flat(String flatId, String projectId, String flatType) {
        this.flatId = flatId;
        this.projectId = projectId;
        this.flatType = flatType;
        this.isBooked = false; // Default value
    }
    
    // Getters and setters
    public String getFlatId() {
        return flatId;
    }
    
    public void setFlatId(String flatId) {
        this.flatId = flatId;
    }
    
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getFlatType() {
        return flatType;
    }
    
    public void setFlatType(String flatType) {
        this.flatType = flatType;
    }
    
    public boolean isBooked() {
        return isBooked;
    }
    
    public void setBooked(boolean booked) {
        isBooked = booked;
    }
}