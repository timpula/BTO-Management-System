package models;

public class Applicant extends User {
    private String currentApplication; // ID of current application if any
    private String bookedFlatType;
    private String bookedProjectId;

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
}