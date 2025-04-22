package models;

public class Applicant extends User {
    private String currentApplication; // ID of current application if any
    private String bookedFlatType;
    private String bookedProjectId;

    private String filterNeighborhood; // Filter by neighborhood
    private String filterFlatType; // Filter by flat type

    // Getters and setters for filter settings
    public Applicant(String nric, String name, String password, int age, String maritalStatus) {
        super(nric, name, password, "Applicant", age, maritalStatus);
    }

    public String getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(String currentApplication) {
        this.currentApplication = currentApplication;
    }

    public String getBookedFlatType() {
        return bookedFlatType;
    }

    public void setBookedFlatType(String flatType) {
        this.bookedFlatType = flatType;
    }

    public String getBookedProjectId() {
        return bookedProjectId;
    }

    public void setBookedProjectId(String projectId) {
        this.bookedProjectId = projectId;
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