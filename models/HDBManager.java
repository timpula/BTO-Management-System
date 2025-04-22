package models;

import views.IFilter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;



public class HDBManager extends User implements IFilter{
    private List<String> createdProjects;
    private String currentProject;

    // Constructors
    public HDBManager() {
        super();
        this.createdProjects = new ArrayList<>();
    }

    public HDBManager(String nric, String name, String password, int age, String maritalStatus) {
        super(nric, name, password, "HDBManager", age, maritalStatus);
        this.createdProjects = new ArrayList<>();
    }

    // Getters and setters
    public List<String> getCreatedProjects() {
        return createdProjects;
    }

    public void addCreatedProject(String projectId) {
        this.createdProjects.add(projectId);
    }

    public String getCurrentProject() {
        return currentProject;
    }

    public void setCurrentProject(String currentProject) {
        this.currentProject = currentProject;
    }

    @Override
    public List<Project> filterProjects(List<Project> projects) {
        return projects.stream()
                .filter(project -> project.getCreatorNRIC().equals(this.getNric()))
                .collect(Collectors.toList());
    }

}