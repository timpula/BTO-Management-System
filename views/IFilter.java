package views;

import models.Project;
import java.util.List;

public interface IFilter {
    List<Project> filterProjects(List<Project> projects);
}