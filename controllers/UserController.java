package controllers;

import models.User;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.Project; // Assuming Project is a class in models package

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private static List<User> users = new ArrayList<>(); // Simulating a database of users
    private User currentUser = null;

    public User getCurrentUser() {
        return currentUser;
    }

    public User login(String nric, String password) {
        User user = findUserByNric(nric); // Use the helper method to find the user
    
        if (user != null && user.getPassword().equals(password)) {
            // Convert the user to the appropriate subclass based on userType
            switch (user.getUserType()) {
                case "Applicant":
                    currentUser = new Applicant(user.getNric(), user.getName(), user.getPassword(),
                            user.getAge(), user.getMaritalStatus());
                    break;
                case "HDBOfficer":
                    currentUser = new HDBOfficer(user.getNric(), user.getName(), user.getPassword(),
                            user.getAge(), user.getMaritalStatus());
                    break;
                case "HDBManager":
                    currentUser = new HDBManager(user.getNric(), user.getName(), user.getPassword(),
                            user.getAge(), user.getMaritalStatus());
                    break;
                default:
                    currentUser = null;
                    break;
            }
            System.out.println("Login successful for user: " + currentUser.getName() + " (" + currentUser.getUserType() + ")");
            return currentUser;
        }
        System.out.println("Login failed for user: " + nric);
        return null; // Login failed
    }

    // Helper method to find a user by NRIC
    private User findUserByNric(String nric) {
        for (User user : users) {
            if (user.getNric().equals(nric)) {
                return user;
            }
        }
        return null;
    }

    // Logout
    public void logout(String nric) {
        System.out.println("User " + nric + " has logged out.");
    }

    // Update user profile
    public boolean updateProfile(User updatedUser) {
        for (User user : users) {
            if (user.getNric().equals(updatedUser.getNric())) {
                user.setName(updatedUser.getName());
                user.setNric(updatedUser.getNric());
                user.setUserType(updatedUser.getUserType());
                System.out.println("Profile updated for user: " + updatedUser.getNric());
                return true;
            }
        }
        System.out.println("User not found: " + updatedUser.getNric());
        return false; // User not found
    }

    // Change password
    public boolean changePassword(String nric, String oldPassword, String newPassword) {
        for (User user : users) {
            if (user.getNric().equals(nric) && user.getPassword().equals(oldPassword)) {
                user.setPassword(newPassword);
                System.out.println("Password changed successfully for user: " + nric);
                return true;
            }
        }
        System.out.println("Password change failed for user: " + nric);
        return false; // Password change failed
    }

    // View user details
    public User viewUserDetails(String nric) {
        for (User user : users) {
            if (user.getNric().equals(nric)) {
                System.out.println("User details retrieved for: " + nric);
                return user;
            }
        }
        System.out.println("User not found: " + nric);
        return null; // User not found
    }

    // Filter projects
    public List<Project> filterProjects(String filterCriteria) {
        List<Project> filteredProjects = new ArrayList<>();
        ProjectController projectController = new ProjectController();
        List<Project> projects = projectController.viewAllProjects(); // Assuming this method returns all projects
        for (Project project : projects) {
            if (project.getProjectName().toLowerCase().contains(filterCriteria.toLowerCase())) {
                filteredProjects.add(project);
            }
        }
        System.out.println("Projects filtered by criteria: " + filterCriteria);
        return filteredProjects;
    }

    // Add a new user (for testing purposes)
    public boolean addUser(User user) {
        if (user != null) {
            users.add(user);
            System.out.println("User added: " + user.getNric());
            return true;
        }
        System.out.println("Failed to add user.");
        return false;
    }
    public User getUserByNRIC(String nric) {
        User user = findUserByNric(nric); // Assume this retrieves a User object
        if (user instanceof Applicant) {
            return (Applicant) user; // Return as Applicant if applicable
        }
        return user; // Otherwise, return as a generic User
    }
}
