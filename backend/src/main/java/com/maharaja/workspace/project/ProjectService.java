package com.maharaja.workspace.project;

import com.maharaja.workspace.task.TaskRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;
    private final TaskRepository taskRepository;

    public ProjectService(ProjectRepository projectRepository, TaskRepository taskRepository) {
        this.projectRepository = projectRepository;
        this.taskRepository = taskRepository;
    }

    public Project create(ProjectRequest request) {
        String prefix = request.keyPrefix();
        if (prefix == null || !prefix.matches("[A-Z]{2,5}")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "keyPrefix must be 2-5 capital letters, e.g. WS");
        }
        if (projectRepository.existsByKeyPrefix(prefix)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "keyPrefix already used");
        }
        Project project = new Project();
        project.setName(request.name());
        project.setDescription(request.description());
        project.setKeyPrefix(prefix);
        return projectRepository.save(project);
    }

    public List<Project> findAll() {
        return projectRepository.findAll();
    }

    public Project get(Long id) {
        return projectRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));
    }

    public Project update(Long id, ProjectRequest request) {
        Project project = get(id);
        project.setName(request.name());
        project.setDescription(request.description());
        return projectRepository.save(project); // keyPrefix never changes
    }

    @Transactional
    public void delete(Long id) {
        Project project = get(id);
        taskRepository.deleteByProjectId(id);
        projectRepository.delete(project);
    }
}