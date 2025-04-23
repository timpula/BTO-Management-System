package models;

public class HDBOfficer extends User {
    private String assignedProjectId;
    private String registrationStatus; // "Pending", "Approved", "Rejected"

    private String filterNeighborhood;
    private String filterFlatType;
    
    // Constructors
    public HDBOfficer() {
        super();
    }
    
    public HDBOfficer(String nric, String name, String password, int age, String maritalStatus) {
        super(nric, name, password, "HDBOfficer", age, maritalStatus);    

    }
    
    // Getters and setters
    public String getAssignedProjectId() {
        return assignedProjectId;
    }
    
    public void setAssignedProjectId(String assignedProjectId) {
        this.assignedProjectId = assignedProjectId;
    }
    
    public String getRegistrationStatus() {
        return registrationStatus;
    }
    
    public void setRegistrationStatus(String registrationStatus) {
        this.registrationStatus = registrationStatus;
    }

    public String getFilterNeighborhood() {
        return filterNeighborhood;
    }

    public void setFilterNeighborhood(String filterNeighborhood) {
        this.filterNeighborhood = filterNeighborhood;
    }

    public String getFilterFlatType() {
        return filterFlatType;
    }

    public void setFilterFlatType(String filterFlatType) {
        this.filterFlatType = filterFlatType;
    }
}