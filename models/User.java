package models;

public class User {
    private String nric;
    private String name;
    private String password;
    private String userType; // "Applicant", "HDBOfficer", "HDBManager"
    private int age;
    private String maritalStatus;
    
    // Constructors, getters, setters
    public User() {}
    
    public User(String nric, String name, String password, String userType,int age, String maritalStatus) {    
        this.nric = nric;
        this.name = name;
        this.password = password;
        this.userType = userType;
        this.age = age;
        this.maritalStatus = maritalStatus; 
    }
    
    // Getters and setters
    public String getNric() {
        return nric;
    }
    
    public void setNric(String nric) {
        this.nric = nric;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getUserType() {
        return userType;
    }
    
    public void setUserType(String userType) {
        this.userType = userType;
    }
    public void setAge(int age) {
        this.age = age;
    }
    public int getAge() {
        return age;
    }  
    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
    public String getMaritalStatus() {
        return maritalStatus;
    }
    
}