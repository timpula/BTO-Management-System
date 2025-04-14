package models;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Project {
    private String projectId;
    private String projectName;
    private String neighborhood;
    private Map<String, Integer> flatTypeUnits; // Map of flat type to number of units
    private Date applicationOpeningDate;
    private Date applicationClosingDate;
    private String managerInCharge; // NRIC of HDB Manager
    private int availableOfficerSlots;
    private boolean visibility;
    private int totalOfficerSlots; // Total number of officer slots available
    private String creatorNRIC; // NRIC of the officer who created the project
    
    // Constructors
    public Project() {
        this.flatTypeUnits = new HashMap<>();
    }
    
    public Project(String projectId, String projectName, String neighborhood, 
                  Date applicationOpeningDate, Date applicationClosingDate, 
                  String managerInCharge) {
        this.projectId = projectId;
        this.projectName = projectName;
        this.neighborhood = neighborhood;
        this.applicationOpeningDate = applicationOpeningDate;
        this.applicationClosingDate = applicationClosingDate;
        this.managerInCharge = managerInCharge;
        this.availableOfficerSlots = 10; // Default value
        this.visibility = false; // Default visibility is off
        this.flatTypeUnits = new HashMap<>();
    }
    
    // Methods to add and update flat types
    public void addFlatType(String flatType, int units) {
        flatTypeUnits.put(flatType, units);
    }
    
    public boolean updateFlatUnits(String flatType, int newUnitsCount) {
        if (flatTypeUnits.containsKey(flatType)) {
            flatTypeUnits.put(flatType, newUnitsCount);
            return true;
        }
        return false;
    }
    
    // Getters and setters
    public String getProjectId() {
        return projectId;
    }
    
    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }
    
    public String getProjectName() {
        return projectName;
    }
    
    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }
    
    public String getNeighborhood() {
        return neighborhood;
    }
    
    public void setNeighborhood(String neighborhood) {
        this.neighborhood = neighborhood;
    }
    
    public Map<String, Integer> getFlatTypeUnits() {
        return flatTypeUnits;
    }
    
    public Date getApplicationOpeningDate() {
        return applicationOpeningDate;
    }
    
    public void setApplicationOpeningDate(Date applicationOpeningDate) {
        this.applicationOpeningDate = applicationOpeningDate;
    }
    
    public Date getApplicationClosingDate() {
        return applicationClosingDate;
    }
    
    public void setApplicationClosingDate(Date applicationClosingDate) {
        this.applicationClosingDate = applicationClosingDate;
    }
    
    public String getManagerInCharge() {
        return managerInCharge;
    }
    
    public void setManagerInCharge(String managerInCharge) {
        this.managerInCharge = managerInCharge;
    }
    
    public int getAvailableOfficerSlots() {
        return availableOfficerSlots;
    }
    
    public void setAvailableOfficerSlots(int availableOfficerSlots) {
        this.availableOfficerSlots = availableOfficerSlots;
    }
    
    public boolean isVisible() {
        return visibility;
    }
    
    public void setVisibility(boolean visibility) {
        this.visibility = visibility;
    }

    public int getTotalOfficerSlots() {
        return totalOfficerSlots;
    }
    public void setTotalOfficerSlots(int totalOfficerSlots) {
        this.totalOfficerSlots = totalOfficerSlots;
    }
    public String getCreatorNRIC() {
        return creatorNRIC;
    }
    public void setCreatorNRIC(String creatorNRIC) {
        this.creatorNRIC = creatorNRIC;
    }  
    public void setFlatTypeUnits(Map<String, Integer> flatTypeUnits) {
        this.flatTypeUnits = flatTypeUnits;
    }

}