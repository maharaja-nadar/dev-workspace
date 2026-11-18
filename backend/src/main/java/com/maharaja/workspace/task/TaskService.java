package com.maharaja.workspace.task;

import com.maharaja.workspace.project.Project;
import com.maharaja.workspace.project.ProjectRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class TaskService {

    private final TaskRepository taskRepository;
    private final ProjectRepository projectRepository;

    public TaskService(TaskRepository taskRepository, ProjectRepository projectRepository) {
        this.taskRepository = taskRepository;
        this.projectRepository = projectRepository;
    }

    @Transactional
    public Task create(Long projectId, TaskRequest request) {
        Project project = projectRepository.findById(projectId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Project not found"));

        Task task = new Task();
        task.setTaskKey(project.getKeyPrefix() + "-" + project.getNextTaskNumber());
        project.setNextTaskNumber(project.getNextTaskNumber() + 1); // saved automatically
        task.setProjectId(projectId);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setAssignee(request.assignee());
        return taskRepository.save(task);
    }

    public List<Task> listByProject(Long projectId) {
        return taskRepository.findByProjectIdOrderByIdAsc(projectId);
    }

    public Task get(Long id) {
        return taskRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Task not found"));
    }

    public Task update(Long id, TaskRequest request) {
        Task task = get(id);
        task.setTitle(request.title());
        task.setDescription(request.description());
        task.setAssignee(request.assignee());
        return taskRepository.save(task);
    }

    public Task updateStatus(Long id, TaskStatus status) {
        Task task = get(id);
        task.setStatus(status);
        return taskRepository.save(task);
    }

    public void delete(Long id) {
        taskRepository.delete(get(id));
    }
}