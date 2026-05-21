package repository;

import domain.Project;

import java.util.List;

public interface ProjectRepository {
    Integer save(Project project) throws Exception;
    Project load(Integer id) throws Exception;
    void delete(Integer id) throws Exception;
    void update(Project project) throws Exception;
    List<Project> list() throws Exception;
}
