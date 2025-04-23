package controllers;

import models.User;
import models.Applicant;
import models.HDBManager;
import models.HDBOfficer;
import models.Project;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class UserController {
    private static List<User> users = new ArrayList<>(); // Shared user list
    private User currentUser = null;

    // Get the currently logged in user
    public User getCurrentUser() {
        return currentUser;
    }

    public User login(String nric, String password) {
        List<User> matchingUsers = findUsersByNric(nric);
        
        if (matchingUsers.isEmpty()) {
            System.out.println("User not found.");
            return null;
        }

        // Verify password (assuming same password for all roles)
        if (!matchingUsers.get(0).getPassword().equals(password)) {
            System.out.println("Invalid password.");
            return null;
        }

        // If only one role, return that user
        if (matchingUsers.size() == 1) {
            currentUser = matchingUsers.get(0);
            return currentUser;
        }

        // Multiple roles found - ask user to choose
        System.out.println("\nMultiple roles found. Please select your role:");
        for (int i = 0; i < matchingUsers.size(); i++) {
            System.out.println((i + 1) + ". " + matchingUsers.get(i).getUserType());
        }

        Scanner scanner = new Scanner(System.in);
        int choice;
        do {
            System.out.print("Enter choice (1-" + matchingUsers.size() + "): ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= matchingUsers.size()) {
                    currentUser = matchingUsers.get(choice - 1);
                    return currentUser;
                }
            } catch (NumberFormatException e) {
                // Invalid input, will prompt again
            }
            System.out.println("Invalid choice. Please try again.");
        } while (true);
    }

    private List<User> findUsersByNric(String nric) {
        List<User> matchingUsers = new ArrayList<>();
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                matchingUsers.add(user);
            }
        }
        return matchingUsers;
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

    // Get user by NRIC and cast if Applicant, HDBOfficer, or HDBManager
    public User getUserByNRIC(String nric) {
        List<User> matchingUsers = findUsersByNric(nric);
        if (!matchingUsers.isEmpty()) {
            User user = matchingUsers.get(0);
            if (user instanceof Applicant) {
                return (Applicant) user;
            } else if (user instanceof HDBOfficer) {
                return (HDBOfficer) user;
            } else if (user instanceof HDBManager) {
                return (HDBManager) user;
            }
            return user;
        }
        return null;
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
