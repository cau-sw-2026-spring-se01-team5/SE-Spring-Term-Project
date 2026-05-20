package repository;

import domain.Project;

public interface ProjectRepository {
    Integer save(Project project) throws Exception;
    Project load(Integer id) throws Exception;
}
