package models;

public class Applicant extends User {
    private String currentApplication; // ID of current application if any

    // Constructor
    public Applicant(String nric, String name, String password, int age, String maritalStatus) {
        super(nric, name, password, "Applicant", age, maritalStatus);
    }

    public String getCurrentApplication() {
        return currentApplication;
    }

    public void setCurrentApplication(String currentApplication) {
        this.currentApplication = currentApplication;
    }
}