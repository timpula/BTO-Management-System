package controllers;

import models.Project;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


public class ProjectController {

    private static List<Project> projects = new ArrayList<>(); // Simulating a database
    private static int projectCounter = 0; // Static counter for project IDs

    // Create a new project
    public boolean createProject(Project project) {
        // Enforce officer‐slot limits
        int slots = project.getTotalOfficerSlots();
        if (slots < 1 || slots > 10) {
            System.out.println("Error: Officer slots must be between 1 and 10.");
            return false;
        }

        // Prevent overlapping application periods for the same manager
        String newBase = project.getProjectName().split(" - ")[0].trim();
        for (Project p : projects) {
            if (!p.getCreatorNRIC().equals(project.getCreatorNRIC())) continue;

               String existingBase = p.getProjectName().split(" - ")[0].trim();
              // skip overlap if the **base** names match
              if (existingBase.equalsIgnoreCase(newBase)) {
                  System.out.println("DEBUG: skipping overlap for same base name \"" + existingBase + "\"");
                  continue;
            }

            Date o1 = p.getApplicationOpeningDate(), c1 = p.getApplicationClosingDate();
            Date o2 = project.getApplicationOpeningDate(), c2 = project.getApplicationClosingDate();
            // if periods overlap
            if (!(c2.before(o1) || o2.after(c1))) {
                System.out.println("Error: You already have a project in that date range.");
                return false;
            }
        }

        if (project != null) {
            // Generate a unique projectId using the counter
            String projectId = String.format("PRJ%02d", projectCounter);
            project.setProjectId(projectId);

            // Increment the counter for the next project 
            projectCounter++;

            // Add the project to the list
            projects.add(project);

            System.out.println("Project created with ID: " + projectId);
            return true;
        }
        return false;
    }

    // Other methods (editProject, deleteProject, etc.) remain unchanged


    // Edit an existing project
    public boolean editProject(String projectId, Project updatedProject) {
        for (Project project : projects) {
            if (project.getProjectId().equals(projectId)) {
            // 3a) Enforce officer‐slot limits
            int newSlots = updatedProject.getTotalOfficerSlots();
            if (newSlots < 1 || newSlots > 10) {
                System.out.println("Error: Officer slots must be between 1 and 10.");
                return false;
            }

            // 3b) Prevent overlapping periods (skip the project itself)
            for (Project other : projects) {
                if (!other.getCreatorNRIC().equals(updatedProject.getCreatorNRIC())
                    || other.getProjectId().equals(projectId)) continue;
                Date o1 = other.getApplicationOpeningDate(), c1 = other.getApplicationClosingDate();
                Date o2 = updatedProject.getApplicationOpeningDate(), c2 = updatedProject.getApplicationClosingDate();
                if (!(c2.before(o1) || o2.after(c1))) {
                    System.out.println("Error: Dates overlap with project " + other.getProjectId());
                    return false;
                }
            }

                project.setProjectName(updatedProject.getProjectName());
                project.setNeighborhood(updatedProject.getNeighborhood());
                project.setApplicationOpeningDate(updatedProject.getApplicationOpeningDate());
                project.setApplicationClosingDate(updatedProject.getApplicationClosingDate());
                project.setTotalOfficerSlots(updatedProject.getTotalOfficerSlots());
                project.setAvailableOfficerSlots(updatedProject.getAvailableOfficerSlots());
                project.setFlatTypeUnits(updatedProject.getFlatTypeUnits());
                return true;
            }
        }
        return false;
    }

    // Delete a project
    public boolean deleteProject(String projectId) {
        return projects.removeIf(project -> project.getProjectId().equals(projectId));
    }

    // View all projects
    public List<Project> viewAllProjects() {
        return new ArrayList<>(projects); // Return a copy of the list
    }

    // Toggle project visibility
    public boolean toggleProjectVisibility(String projectId, boolean visibility) {
        for (Project project : projects) {
            if (project.getProjectId().equals(projectId)) {
                project.setVisibility(visibility);
                return true;
            }
        }
        return false;
    }

    // Get project details by ID
    public Project getProjectDetails(String projectId) {
        System.out.println("DEBUG: Looking for project ID = " + projectId);
        for (Project project : projects) {
            System.out.println("DEBUG: Checking project ID in list = " + project.getProjectId());
            if (project.getProjectId().equals(projectId)) {
                return project;
            }
        }
        return null;
    }

    // Get project by name
    public Project getProjectByName(String projectName) {
        for (Project project : projects) {
            if (project.getProjectName().equalsIgnoreCase(projectName)) {
                return project;
            }
        }
        return null; // Return null if project not found
    }

    // Get projects by neighborhood
    public List<Project> getProjectsByNeighborhood(String neighborhood) {
        List<Project> neighborhoodProjects = new ArrayList<>();
        for (Project project : projects) {
            if (project.getNeighborhood().equalsIgnoreCase(neighborhood)) {
                neighborhoodProjects.add(project);
            }
        }
        return neighborhoodProjects;
    }

    // Update HDB officer slots
    public boolean updateHDBOfficerSlots(String projectId, int newSlots) {
        // Enforce officer‐slot limits
        if (newSlots < 1 || newSlots > 10) {
            System.out.println("Error: Officer slots must be between 1 and 10.");
            return false;
        }
        for (Project project : projects) {
            if (project.getProjectId().equals(projectId)) {
                if (newSlots >= project.getTotalOfficerSlots() - project.getAvailableOfficerSlots()) {
                    project.setTotalOfficerSlots(newSlots);
                    project.setAvailableOfficerSlots(newSlots - (project.getTotalOfficerSlots() - project.getAvailableOfficerSlots()));
                    return true;
                }
                return false; // New slots are less than assigned officers
            }
        }
        return false; // Project not found
    }
}