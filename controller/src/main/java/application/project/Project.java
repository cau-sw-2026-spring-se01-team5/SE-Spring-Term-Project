package application.project;

import application.project.dto.CreateProjectCommand;
import application.project.dto.CreateProjectResult;

public interface Project {

    CreateProjectResult createProject(CreateProjectCommand command);
}