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
        // Validate and convert NRIC format
        if (!nric.matches("[STFG]\\d{7}[A-Z]")) {
            System.out.println("Invalid NRIC format. Must be in format like S1234567A");
            return null;
        }

        List<User> matchingUsers = new ArrayList<>();
        String upperNric = nric.toUpperCase();
        
        // First check for HDBOfficer role
        for (User user : users) {
            if (user.getNric().equals(upperNric) && user instanceof HDBOfficer) {
                matchingUsers.add(user);
            }
        }
        
        // Then check for other roles
        for (User user : users) {
            if (user.getNric().equals(upperNric) && !(user instanceof HDBOfficer)) {
                matchingUsers.add(user);
            }
        }

        if (matchingUsers.isEmpty()) {
            System.out.println("User not found.");
            return null;
        }

        if (!matchingUsers.get(0).getPassword().equals(password)) {
            System.out.println("Invalid password.");
            return null;
        }

        if (matchingUsers.size() == 1) {
            currentUser = matchingUsers.get(0);
            return currentUser;
        }

        System.out.println("\nMultiple roles found. Please select your role:");
        for (int i = 0; i < matchingUsers.size(); i++) {
            System.out.println((i + 1) + ". " + matchingUsers.get(i).getUserType());
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter choice (1-" + matchingUsers.size() + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= matchingUsers.size()) {
                    currentUser = matchingUsers.get(choice - 1);
                    return currentUser;
                }
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
                continue;
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    private List<User> findUsersByNric(String nric) {
        List<User> matchingUsers = new ArrayList<>();
        
        // Convert input NRIC to uppercase and validate format
        String upperNric = nric.toUpperCase();
        if (!upperNric.matches("[STFG]\\d{7}[A-Z]")) {
            System.out.println("Invalid NRIC format. Must be like S1234567A");
            return matchingUsers;
        }

        for (User user : users) {
            // Compare with uppercase stored NRIC
            if (user.getNric().equals(upperNric)) {
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
            return true;
        }
        return false;
    }

    // Get user by NRIC
    public User getUserByNRIC(String nric) {
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                return user;
            }
        }
        return null;
    }

    public User getUserByNRICAndRole(String nric, String roleType) {
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric) && user.getUserType().equals(roleType)) {
                System.out.println("DEBUG: Found user " + user.getName() + " with role " + roleType);
                return user;
            }
        }
        System.out.println("DEBUG: No user found with NRIC " + nric + " and role " + roleType);
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

    public List<User> getAllUsers() {
        return new ArrayList<>(users); // Return a copy of the users list
    }

    public User authenticate(String nric, String password) {
        List<User> matchingUsers = new ArrayList<>();
        
        // First check for HDBOfficer role
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric) && user instanceof HDBOfficer) {
                matchingUsers.add(user);
            }
        }
        
        // If no officer found, check other roles
        if (matchingUsers.isEmpty()) {
            for (User user : users) {
                if (user.getNric().equalsIgnoreCase(nric) && !(user instanceof HDBOfficer)) {
                    matchingUsers.add(user);
                }
            }
        }

        // No users found
        if (matchingUsers.isEmpty()) {
            System.out.println("DEBUG: No users found for NRIC: " + nric);
            return null;
        }

        // Check password
        if (!matchingUsers.get(0).getPassword().equals(password)) {
            System.out.println("DEBUG: Invalid password for NRIC: " + nric);
            return null;
        }

        // Single role - return the user
        if (matchingUsers.size() == 1) {
            System.out.println("DEBUG: Authenticated as " + matchingUsers.get(0).getUserType());
            return matchingUsers.get(0);
        }

        // Multiple roles - show selection menu
        System.out.println("\nMultiple roles found. Please select your role:");
        for (int i = 0; i < matchingUsers.size(); i++) {
            System.out.println((i + 1) + ". " + matchingUsers.get(i).getUserType());
        }

        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.print("Enter choice (1-" + matchingUsers.size() + "): ");
            try {
                int choice = Integer.parseInt(scanner.nextLine());
                if (choice >= 1 && choice <= matchingUsers.size()) {
                    User selectedUser = matchingUsers.get(choice - 1);
                    System.out.println("DEBUG: Selected role: " + selectedUser.getUserType());
                    return selectedUser;
                }
            } catch (NumberFormatException e) {
                // Invalid input
            }
            System.out.println("Invalid choice. Please try again.");
        }
    }

    public List<User> getUsersByNRIC(String nric) {
        List<User> result = new ArrayList<>();
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric)) {
                result.add(user);
            }
        }
        return result;
    }    

    public Applicant getApplicantByNRIC(String nric) {
        for (User user : users) {
            if (user.getNric().equalsIgnoreCase(nric) && user instanceof Applicant) {
                return (Applicant) user;
            }
        }
        return null;
    }
}
