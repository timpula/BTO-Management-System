package controllers;

import models.User;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.Project;

import java.util.ArrayList;
import java.util.List;

public class UserController {
    private static List<User> users = new ArrayList<>(); // Shared user list
    private User currentUser = null;

    // Get the currently logged in user
    public User getCurrentUser() {
        return currentUser;
    }

    // ✅ Fixed: Login reuses actual stored object (e.g. HDBOfficer with assignedProjectId)
    public User login(String nric, String password) {
        User user = findUserByNric(nric);

        if (user != null && user.getPassword().equals(password)) {
            switch (user.getUserType()) {
                case "Applicant":
                    currentUser = (Applicant) user;
                    break;
                case "HDBOfficer":
                    currentUser = (HDBOfficer) user;
                    break;
                case "HDBManager":
                    currentUser = (HDBManager) user;
                    break;
                default:
                    currentUser = user;
                    break;
            }
            return currentUser;
        }

        System.out.println("Login failed for user: " + nric);
        return null;
    }

    // Find user by NRIC (internal use)
    private User findUserByNric(String nric) {
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                return user;
            }
        }
        return null;
    }

    // Lookup user by NRIC or Name
    public User viewUserDetails(String input) {
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(input) || user.getName().equalsIgnoreCase(input)) {
                return user;
            }
        }
        System.out.println("❌ User not found by: " + input);
        return null;
    }

    // Logout user
    public void logout(String nric) {
        System.out.println("User " + nric + " has logged out.");
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
        return false;
    }

    // Add user to shared list
    public boolean addUser(User user) {
        if (user != null) {
            users.add(user);
            System.out.println("✅ User added to shared list: " + user.getName() + " (" + user.getNric() + ")");
            return true;
        }
        System.out.println("❌ Failed to add user.");
        return false;
    }

    // Get user by NRIC and cast if Applicant
    public User getUserByNRIC(String nric) {
        User user = findUserByNric(nric);
        if (user instanceof Applicant) {
            return (Applicant) user;
        }
        return user;
    }

    // Filter projects by name
    public List<Project> filterProjects(String filterCriteria) {
        List<Project> filteredProjects = new ArrayList<>();
        ProjectController projectController = new ProjectController();
        List<Project> projects = projectController.viewAllProjects();

        for (Project project : projects) {
            if (project.getProjectName().toLowerCase().contains(filterCriteria.toLowerCase())) {
                filteredProjects.add(project);
            }
        }

        System.out.println("Projects filtered by criteria: " + filterCriteria);
        return filteredProjects;
    }
}
