package controllers;

import models.Project;
import java.util.List;

public interface IFilter {
    List<Project> filterProjects(String nric);
}
